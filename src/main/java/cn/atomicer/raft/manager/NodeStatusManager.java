package cn.atomicer.raft.manager;

import cn.atomicer.raft.*;
import cn.atomicer.raft.annotation.Status;
import cn.atomicer.raft.exception.StatusChangeException;
import cn.atomicer.raft.model.Configuration;
import cn.atomicer.raft.model.NodeInfo;
import cn.atomicer.raft.proto.EntryInfo;
import cn.atomicer.raft.proto.MessageType;
import cn.atomicer.raft.proto.Response;
import cn.atomicer.raft.proto.Vote;
import io.grpc.StatusRuntimeException;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author Rao Mengnan
 * on 2018/11/21.
 */
public class NodeStatusManager implements StatusManager {
    private static Log logger = LogFactory.getLog(NodeStatusManager.class);
    private static final Response NULL_RESPONSE = Response.getDefaultInstance();

    private Configuration config;
    private TermManager termManager;
    private EntryManager entryManager;
    private List<Node> allNodes;

    private NodeInfo localhost;

    private AtomicReference<Status> status;
    private Map<Status, ? extends State> stateMap;

    public NodeStatusManager(Configuration config, TermManager termManager, EntryManager entryManager, List<Node> allNodes) {
        this.config = config;
        this.termManager = termManager;
        this.entryManager = entryManager;
        this.allNodes = allNodes;

        this.localhost = config.getNodeInfo();

        this.status = new AtomicReference<>();

        this.initStateMap();
        // set init state
        this.accept(Status.FOLLOWER);
    }

    private void initStateMap() {
        Map<Status, State> stateMap = new HashMap<>();
        stateMap.put(Status.LEADER, new LeaderState());
        stateMap.put(Status.FOLLOWER, new FollowerState());
        stateMap.put(Status.CANDIDATE, new CandidateState());

        this.stateMap = stateMap;
    }

    @Override
    public synchronized void accept(Status status) {
        // leader -> follower -> candidate -> candidate/leader/follower
        logger.debug(String.format("Accept status change, last status: %s, to: %s, leader: %s, term: %s",
                this.status.get(), status, termManager.getLeaderId(), termManager.getTerm()));

        Status lastStatus = this.status.get();
        switch (status) {
            case LEADER:
                // candidate -> leader
                if (lastStatus != Status.CANDIDATE) {
                    logger.warn(String.format("State %s to leader was rejected.", lastStatus));
                    return;
                }
                break;
            case CANDIDATE:
                // follower / candidate -> candidate
                if (lastStatus != Status.FOLLOWER && lastStatus != Status.CANDIDATE) {
                    logger.warn(String.format("State %s to candidate was rejected.", lastStatus));
                    return;
                }
                break;
            default:
                // any -> follower
                logger.trace(String.format("apply status: %s", status));
        }

        if (lastStatus != null && stateMap.get(lastStatus) != null) {
            State last = stateMap.get(lastStatus);
            last.onExit();
        }

        this.status.set(status);
        State current = stateMap.get(status);
        current.onEntry();
        current.apply();

        logStatus(status);
    }

    private void logStatus(Status status) {
        EntryInfo lastLog = entryManager.getLatestLog();
        String lastLogInfo = null;
        if (lastLog != null) {
            lastLogInfo = String.format("(prevLogIndex: %s, prevLogTerm: %s)",
                    lastLog.getLogIndex(), lastLog.getCreateTerm());
        }
        if (status == Status.FOLLOWER) {
            logger.trace(String.format("%s info: current term: %s, latest log: %s",
                    status, termManager.getTerm(), lastLogInfo));
        } else {
            logger.debug(String.format("%s info: current term: %s, latest log: %s",
                    status, termManager.getTerm(), lastLogInfo));
        }
    }

    public Status getStatus() {
        return status.get();
    }

    class FollowerState implements State {
        private Disposable disposable;
        private AtomicLong lastBeat = new AtomicLong();

        @Override
        public void onEntry() {
            heartbeat();
        }

        @Override
        public void apply() {
            if (status.accumulateAndGet(Status.FOLLOWER, (o, n) -> {
                if (this.disposable == null || this.disposable.isDisposed()) {
                    logger.info("Create new disposable interval to check heartbeat");
                    newDisposable();
                    return n;
                }
                return o;
            }) == Status.FOLLOWER) {
                logger.debug("Status applied: FOLLOWER");
            }
        }

        @Override
        public void onExit() {
            if (this.disposable != null && !this.disposable.isDisposed()) {
                this.disposable.dispose();
            }
        }

        private void heartbeat() {
            lastBeat.set(System.currentTimeMillis());
            logger.debug(String.format(
                    "Heartbeat from leader/candidate, term: %s, leader: %s",
                    termManager.getTerm(), termManager.getLeaderId()));
        }

        private void newDisposable() {
            this.disposable = Observable.interval(config.getElectionTimeout() / 2, TimeUnit.MILLISECONDS)
                    .forEachWhile(l -> {
                        long elapsed = System.currentTimeMillis() - lastBeat.get();
                        logger.debug(String.format("this is %s times, elapsed: %s, default election timeout: %s",
                                l, elapsed, config.getElectionTimeout()));
                        if (elapsed < config.getElectionTimeout()) {
                            return true;
                        }

                        logger.info(String.format("Election timeout, become candidate, current term: %s",
                                termManager.getTerm()));
                        accept(Status.CANDIDATE);
                        return false;
                    });
        }
    }

    class CandidateState implements State {
        private Disposable disposable;

        @Override
        public void onEntry() {
            termManager.increaseTerm();
        }

        @Override
        public void apply() {
            if (this.disposable == null || this.disposable.isDisposed()) {
                newDisposable();
            }
        }

        @Override
        public void onExit() {
            if (this.disposable != null && !this.disposable.isDisposed()) {
                this.disposable.dispose();
            }
        }

        private void newDisposable() {
            long socketTimeout = config.getSocketTimeout() > 0 ? config.getSocketTimeout() : 500;
            this.disposable = Observable
                    .fromIterable(allNodes)
                    .delay(10, TimeUnit.MILLISECONDS)
                    .map(node -> node.requestVote(createVote()))
                    .doOnError(e -> onError("Failed to request a node to vote", e))
                    .timeout(socketTimeout, TimeUnit.MILLISECONDS)
                    .onErrorReturnItem(NULL_RESPONSE)
                    .observeOn(Schedulers.io())
                    .collect(
                            (Callable<ArrayList<Response>>) ArrayList::new,
                            (list, v) -> {
                                if (v != null) list.add(v);
                            })
                    .subscribe(
                            this::statisticVoteAndChangeStatus,
                            e -> onError("Vote request failed", e));
        }

        private void statisticVoteAndChangeStatus(List<Response> response) {
            logger.info("Statistic all votes");
            long count = response.stream()
                    .filter(resp -> resp.getType() != null)
                    .map(resp -> {
                        if (resp.getTerm() > termManager.getTerm()) {
                            termManager.updateTerm(resp.getTerm());
                            accept(Status.FOLLOWER);
                        }
                        return resp.getType();
                    }).filter(type -> type == MessageType.OK)
                    .count();

            logger.info(String.format("Accept votes: %s, current status: %s", count, status.get()));
            if (count > allNodes.size() / 2) {
                accept(Status.LEADER);
            } else {
                try {
                    Thread.sleep(150 + new Random().nextInt(50));
                    accept(Status.CANDIDATE);
                } catch (InterruptedException e) {
                    logger.warn(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }

        private Vote createVote() {
            EntryInfo latest = entryManager.getLatestLog();
            if (latest == null) latest = EntryManager.NULL_ENTRY_INFO;
            return Vote.newBuilder()
                    .setTerm(termManager.getTerm())
                    .setNodeId(localhost.getName())
                    .setLastEntryInfo(latest)
                    .build();
        }

        private void onError(String msg, Throwable e) {
            if (e instanceof ConnectException || e instanceof StatusRuntimeException) {
                logger.info(String.format("%s, cause: %s", msg, e));
                logger.trace(msg, e);
            } else {
                logger.warn(msg, e);
            }
        }
    }

    class LeaderState implements State {

        private Disposable disposable;

        @Override
        public void onEntry() {
        }

        @Override
        public void apply() {
            if (disposable == null || disposable.isDisposed()) {
                newDisposable();
            }
        }

        @Override
        public void onExit() {
            if (this.disposable != null && !this.disposable.isDisposed()) {
                this.disposable.dispose();
            }
        }

        private void newDisposable() {
            long electionCycle = config.getElectionTimeout() * 2 / 3;
            OnAppendSuccess onAppendSuccess = new OnAppendSuccess();
            List<EntriesSynchronize> synchronizes = allNodes.stream()
                    .filter(n -> !localhost.getName().equals(n.getId()))
                    .map(n -> new EntriesSynchronize(n, entryManager, NodeStatusManager.this, termManager, onAppendSuccess))
                    .collect(Collectors.toList());

            this.disposable = Observable
                    .interval(electionCycle, TimeUnit.MILLISECONDS)
                    .delay(10, TimeUnit.MILLISECONDS)
                    .forEachWhile(times -> {
                        if (status.get() != Status.LEADER) return true;
                        askAppendEntries(synchronizes);
                        return true;
                    });
        }

        private void askAppendEntries(List<EntriesSynchronize> synchronizes) {
            AtomicReference<Disposable> reference = new AtomicReference<>();
            Disposable disposable = Observable
                    .fromIterable(synchronizes)
                    .subscribeOn(Schedulers.io())
                    .subscribe(agent -> {
                        if (status.get() != Status.LEADER) {
                            throw new StatusChangeException(
                                    String.format(
                                            "Status changed while node %s attempt to append entries", localhost.getName()));
                        } else {
                            agent.doSync();
                        }
                    }, e -> {
                        logger.warn(e);
                        Disposable d = reference.get();
                        if (d != null && !d.isDisposed()) d.dispose();
                    });

            reference.set(disposable);
        }
    }

    class OnAppendSuccess implements BiConsumer<String, Long> {

        private Map<String, Long> followersCommitted;

        OnAppendSuccess() {
            this.followersCommitted = new ConcurrentHashMap<>();
        }

        @Override
        public void accept(String nodeId, Long index) {
            // 更新最后提交的logIndex
            followersCommitted.put(nodeId, index);
            long minIdxAbleToCommit = entryManager.getLatestLog().getLogIndex();
            for (Long fCommitted : followersCommitted.values()) {
                if (fCommitted <= minIdxAbleToCommit) {
                    minIdxAbleToCommit = fCommitted;
                }
            }
            entryManager.updateCommittedIndex(minIdxAbleToCommit);
        }
    }

}