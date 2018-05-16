package cn.atomicer.zephyr.raft;

import cn.atomicer.zephyr.raft.exception.SocketException;
import cn.atomicer.zephyr.raft.function.Action;
import cn.atomicer.zephyr.raft.model.*;
import cn.atomicer.zephyr.raft.serialize.SerializeTool;
import cn.atomicer.zephyr.raft.socket.ElectionRPC;
import cn.atomicer.zephyr.raft.socket.MessageSender;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Rao Mengnan
 *         on 2018/5/14.
 */
public class ElectionManager extends Thread {
    private Log log = LogFactory.getLog(getClass());
    private static final Gson gson = new Gson();

    private Configuration configuration;
    private Machine localhost;
    private Action<Long> prevLogIndex;

    private AtomicReference<String> knowableLeader; // knowableLeader name
    private AtomicReference<StatusEnum> status;
    private AtomicReference<VoteInfo> myVote;
    private int term;
    private int leaderTerm;
    private Long leaderLogIndex;

    private long electionStartTs;
    private long lastHeartbeatTs;

    private MessageSender messageSender;
    private ElectionRPC rpc;

    ElectionManager(Configuration configuration) {
        setDaemon(true);
        setName("election-worker");

        this.configuration = configuration;
        this.localhost = configuration.getConfig().getMachineInfo();
        this.prevLogIndex = () -> 0L;
        this.messageSender = new MessageSender(configuration);
        this.rpc = new ElectionRPCImpl();

        this.status = new AtomicReference<>(StatusEnum.FOLLOWER);
        this.myVote = new AtomicReference<>();
        this.knowableLeader = new AtomicReference<>();
    }

    @Override
    public void run() {
        lastHeartbeatTs = System.currentTimeMillis();
        while (true) {
            try {
                processStatus(status.get());
            } catch (InterruptedException e) {
                log.warn(e);
                interrupt();
                break;
            } catch (Exception e) {
                log.warn("Unknown", e);
            }
        }
    }


    StatusEnum getStatus() {
        return status.get();
    }

    ElectionManager setPrevLogIndex(Action<Long> prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
        return this;
    }

    /**
     * @param from 发送投票信息的机器mid
     * @param vote 投票信息
     */
    Vote processVoteRequest(String from, Vote vote) throws Exception {
        log.debug(String.format("ELECTION-MANAGER: peer: %s, vote content: %s", from, vote));

        String leaderName = knowableLeader.get();
        if (leaderName != null) {
            // 已存在，拒绝
            log.debug(String.format("ELECTION-MANAGER: leader's lease in effect: %s", leaderName));
            return new Vote(leaderName, leaderTerm, leaderLogIndex);
        }

        switch (status.get()) {
            case FOLLOWER:
                log.debug("ELECTION-MANAGER: try to make a vote");
                if (myVote.get() != null && myVote.get().isOverdue()) {
                    // 选票已存在且未过期
                    return myVote.get().vote;
                } else {
                    // 选票不存在或已过期
                    electionStartTs = System.currentTimeMillis();
                    VoteInfo raiseVote = new VoteInfo(vote);
                    myVote.set(raiseVote);
                    return vote;
                }
            case CANDIDATE:
                VoteInfo latestVote = myVote.get();
                Vote currentVote = latestVote.vote;

                Vote winner;
                if (currentVote != null && latestVote.isOverdue()) {
                    log.debug(String.format("ELECTION-MANAGER: %s ask for (%s) but already update self vote: %s",
                            from, vote, currentVote));
                    winner = vote;
                } else {
                    winner = compareVote(currentVote, vote);
                }

                if (winner != currentVote) {
                    myVote.set(new VoteInfo(vote));
                }
                if (!winner.getMid().equals(localhost.getName())) {
                    status.set(StatusEnum.FOLLOWER);
                }
                return winner;
            case LEADER:
                return new Vote(localhost.getName(), term, prevLogIndex.run());
            default:
                log.error("ELECTION-MANAGER: unknown status: " + status);
                return null;
        }

    }

    Message processAddEntitiesRequest(Message message) throws Exception {
        log.debug(String.format("ELECTION-MANAGER: heartbeat: src term: %s, local term: %s, leader term: %s",
                message.getTerm(), term, leaderTerm));

        long lastLogIndex = prevLogIndex.run();
        String requestId = message.getRequestId().toString();
        if (message.getTerm() < term) {
            return buildMessage(MessageTypes.REJECT, lastLogIndex, requestId);
        }

        long remoteLogIndex = message.getData() == null ? 0 : SerializeTool.bytes2long(message.getData().array());
        switch (status.get()) {
            case CANDIDATE:
            case LEADER:
                if (message.getTerm() >= term && remoteLogIndex == lastLogIndex
                        || remoteLogIndex > lastLogIndex) {
                    status.set(StatusEnum.FOLLOWER);
                    leaderTerm = message.getTerm();
                    knowableLeader.set(message.getMid().toString());
                    leaderLogIndex = remoteLogIndex;
                    return buildMessage(MessageTypes.ACK, lastLogIndex, requestId);
                } else {
                    log.info(String.format("ELECTION-MANAGER: conflict with self: %s, leader term: %s, logIndex: %s",
                            localhost.getName(), term, lastLogIndex));
                }
                break;
            case FOLLOWER:
                updateHeartbeatTs();
                leaderTerm = message.getTerm();
                knowableLeader.set(message.getMid().toString());
                if (remoteLogIndex == lastLogIndex) {
                    leaderLogIndex = remoteLogIndex;
                    return buildMessage(MessageTypes.ACK, lastLogIndex, requestId);
                }
        }

        return buildMessage(MessageTypes.REJECT, lastLogIndex, requestId);
    }

    private void sendHeartbeat() throws InterruptedException {
        for (Machine machine : configuration.getConfig().getPeers()) {
            if (status.get() != StatusEnum.LEADER) return;
            try {
                rpc.appendEntries(machine.getName());
                log.debug(String.format("ELECTION-MANAGER: rpc invoke (%s) successfully: ping", machine.getName()));
            } catch (Throwable throwable) {
                log.warn(String.format("ELECTION-MANAGER: rpc invoke error (%s)", machine.getHost()), throwable);
            }
        }

        try {
            Thread.sleep(Constants.HEARTBEAT_RECYCLE);
        } catch (InterruptedException e) {
            log.warn(e);
            interrupt();
        }
    }

    private void processStatus(StatusEnum status) throws Exception {
        log.debug("---" + status);
        switch (status) {
            case CANDIDATE:
                // 随机等待100 - 150 ms, 再次尝试选举
                long retreat = Constants.WAIT_VOTE_TIMEOUT + new Random().nextInt(50);
                Thread.sleep(retreat);
                if (getStatus() == StatusEnum.CANDIDATE) {
                    newElection();
                }
                break;
            case FOLLOWER:
                Thread.sleep(Constants.HEARTBEAT_TIMEOUT);
                if (System.currentTimeMillis() - lastHeartbeatTs > Constants.HEARTBEAT_TIMEOUT
                        && System.currentTimeMillis() - electionStartTs > Constants.ELECTION_TIMEOUT) {
                    newElection();
                }
                break;
            case LEADER:
                sendHeartbeat();
                break;
        }
    }

    private void newElection() throws Exception {
        log.debug("--- new election");

        Vote voteForSelf = new Vote(localhost.getName(), term, prevLogIndex.run());
        // 更新状态为candidate
        boolean updateStatusRes =
                this.status.compareAndSet(StatusEnum.FOLLOWER, StatusEnum.CANDIDATE)
                        || this.status.get() == StatusEnum.CANDIDATE;
        log.info(String.format("ELECTION-MANAGER: %s: update status: %s", status.get(), updateStatusRes));
        if (!updateStatusRes) return;

        this.myVote.set(new VoteInfo(voteForSelf));
        knowableLeader.set(null);

        Map<String, Vote> votesBox = new HashMap<>(); // 投票者: 投票内容
        votesBox.put(localhost.getName(), voteForSelf);

        for (Machine machine : configuration.getConfig().getPeers()) {
            log.debug(String.format("ELECTION-MANAGER: ask vote for: %s, send to %s:%s",
                    voteForSelf, machine.getHost(), machine.getElectionPort()));
            Message resp;
            try {
                resp = rpc.requestVote(machine.getName(), voteForSelf);
                if (resp == null) {
                    log.warn("ELECTION-MANAGER: No response content got: request vote for self, term: " + term);
                    return;
                }
            } catch (Throwable throwable) {
                log.warn(String.format("ELECTION-MANAGER: rpc (%s) invoke error", machine.getName()), throwable);
                continue;
            }
            log.debug(String.format("ELECTION-MANAGER: rpc invoke (%s) responded: %s", machine.getName(), resp));
            switch (resp.getType().toString()) {
                case MessageTypes.VOTE:
                    // 保存选票
                    try {
                        Vote respondedVote = gson.fromJson(new String(resp.getData().array()), Vote.class);
                        votesBox.put(machine.getName(), respondedVote);
                    } catch (JsonSyntaxException e) {
                        log.warn(String.format("ELECTION-MANAGER: Illegal rpc (%s) response data, requestId: %s",
                                machine.getName(), resp.getRequestId()));
                    }

                    break;
                default:
                    log.warn(String.format("ELECTION-MANAGER: unsupported reply type: %s, request vote responded from %s",
                            resp, machine.getName()));
            }
        }

        // 判断是否半数通过
        Vote popular = statisticVotesAndGetPopular(votesBox);
        if (popular != null) {
            log.info(String.format("Election completely, %s become the new knowableLeader", popular.getMid()));

            if (popular.getMid().equals(voteForSelf.getMid())) {
                // 当选为leader
                boolean res = status.compareAndSet(StatusEnum.CANDIDATE, StatusEnum.LEADER);
                if (res) {
                    term += 1;
                    leaderTerm = term;
                }
                // 选举过程中碰到比自己更大的，状态已经改变
            } else {
                status.set(StatusEnum.FOLLOWER);
            }
            log.info("ELECTION-MANAGER: localhost status: " + status);
            return;
        }

        log.info("ELECTION-MANAGER: conflict, wait next election");
        // 接下来应该随机等待一段时间，尝试下次选举
    }

    private void updateHeartbeatTs() {
        this.lastHeartbeatTs = System.currentTimeMillis();
    }

    /**
     * 比较已投的选票和收到的选票
     *
     * @param selfVote  已投
     * @param otherVote 收到
     * @return term更大或logIndex更大的选票
     * @throws Exception prevLogIndex 或 Interrupted 异常
     */
    private Vote compareVote(Vote selfVote, Vote otherVote) throws Exception {

        if (selfVote == null) return otherVote;

        Vote winner;
        long logCompare = otherVote.getLogIndex() - prevLogIndex.run();
        if (otherVote.getTerm() > selfVote.getTerm() && logCompare == 0
                || logCompare > 0) {
            log.info(String.format("ELECTION-MANAGER: Change vote for %s", otherVote));
            winner = otherVote;
        } else {
            log.debug("ELECTION-MANAGER: self is winner: " + selfVote);
            winner = selfVote;
        }
        return winner;
    }

    private Vote statisticVotesAndGetPopular(Map<String, Vote> votesBox) {
        // 统计投票信息
        int half = configuration.getMachines().size() / 2;
        if (votesBox.size() > half) {
            log.info("ELECTION-MANAGER: voters more than half, statistic the votes: " + votesBox);
            Map<String, Integer> votesInfo = new HashMap<>();
            int max = 0;
            Vote mostVotesInfo = null;
            for (Vote v : votesBox.values()) {
                int voteTimes = votesInfo.get(v.getMid()) == null ? 0 : votesInfo.get(v.getMid());
                if (max < voteTimes + 1) {
                    max = voteTimes + 1;
                    mostVotesInfo = v;
                }
                votesInfo.put(v.getMid(), voteTimes + 1);
            }
            log.info(String.format("ELECTION-MANAGER: statistic finished, max: %s, votes: %s", mostVotesInfo, max));
            assert mostVotesInfo != null;
            if (max > half && mostVotesInfo.getTerm() >= term) {
                return mostVotesInfo;
            }
        }
        log.debug("ELECTION-MANAGER: election no conclusion: " + votesBox);
        return null;
    }

    private Message buildMessage(String type, Object data) {
        ObjectId objectId = new ObjectId();
        return buildMessage(type, data, objectId.toHexString());
    }

    Message buildMessage(String type, Object data, String requestId) {
        ByteBuffer buffer = null;
        if (data != null) {
            String json = gson.toJson(data);
            buffer = ByteBuffer.wrap(json.getBytes());
        }
        return Message.newBuilder()
                .setMid(localhost.getName())
                .setRequestId(requestId)
                .setType(type)
                .setData(buffer)
                .setTerm(term)
                .build();
    }

    private class ElectionRPCImpl implements ElectionRPC {

        @Override
        public void appendEntries(String machineName) throws Throwable {
            Machine machine = configuration.getMachines().get(machineName);
            Message message = buildMessage(MessageTypes.PING, null);
            log.debug(String.format("send message, requestId: %s, message type: %s",
                    message.getRequestId(), message.getType()));
            Future<Channel> future = messageSender.sendMessage(machine.getName(), message);
            CountDownLatch countDownLatch = new CountDownLatch(1);
            future.addListener(f -> {
                if (f.isDone()) countDownLatch.countDown();
            });
            countDownLatch.await();
            messageSender.release(message.getRequestId().toString());
        }

        @Override
        public Message requestVote(String machineName, Vote vote) throws Throwable {
            Machine machine = configuration.getMachines().get(machineName);

            Message message = buildMessage(MessageTypes.VOTE, vote);
            log.debug(String.format("ask vote for: %s, send to %s:%s",
                    vote, machine.getHost(), machine.getElectionPort()));
            Future<Channel> future = messageSender.sendMessage(machine.getName(), message);
            return waitResponse(message, future);
        }

        private Message waitResponse(Message message, Future<Channel> future) throws Throwable {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            future.addListener(f -> {
                if (f.isDone()) countDownLatch.countDown();
            });
            countDownLatch.await();

            String requestId = message.getRequestId().toString();

            log.debug(String.format("rpc invoke (%s) , isDone: %s, successful: %s",
                    requestId, future.isDone(), future.isSuccess()));

            if (!future.isSuccess()) {
                if (messageSender.getCause(requestId) != null) {
                    Throwable cause = messageSender.popCause(requestId);
                    log.warn(String.format("rpc invoke exception, requestId: %s, message type: %s cause: %s",
                            message.getRequestId(), message.getType(), cause.getMessage()));
                    throw cause;
                }
                throw new SocketException("rpc invoke failed, cause: unknown");
            }

            boolean res = messageSender.tryAcquire(message.getRequestId().toString(), Constants.SOCKET_TIMEOUT);
            if (!res) {
                throw new SocketException("read response timeout");
            }

            Message resp = messageSender.popResponse(requestId);
            if (resp != null) {
                return resp;
            }

            Throwable cause = messageSender.popCause(requestId);
            if (cause != null) throw cause;
            return null;
        }

    }

    static class VoteInfo {

        VoteInfo(Vote vote) {
            this.vote = vote;
            this.timestamp = System.currentTimeMillis();
        }

        Vote vote;
        long timestamp;

        boolean isOverdue() {
            return System.currentTimeMillis() - timestamp > Constants.VOTE_EXPIRE_TIMEOUT;
        }
    }


}
