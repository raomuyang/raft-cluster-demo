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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
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
    private AtomicLong lastBeat;
    private AtomicReference<Disposable> disposableReference;

    public NodeStatusManager(Configuration config, TermManager termManager, EntryManager entryManager, List<Node> allNodes) {
        this.config = config;
        this.termManager = termManager;
        this.entryManager = entryManager;
        this.allNodes = allNodes;

        this.localhost = config.getNodeInfo();
        this.lastBeat = new AtomicLong();
        this.status = new AtomicReference<>();
        this.disposableReference = new AtomicReference<>();

        this.accept(Status.FOLLOWER);
    }

    void disposeLast() {
        Disposable last = disposableReference.getAndSet(null);
        if (last != null && !last.isDisposed()) {
            try {
                last.dispose();
            } catch (Throwable e) {
                logger.warn("Last operation dispose failed", e);
            }
        }
    }

    void atomicResetDisposable(Disposable disposable) {
        System.out.println(String.format("current: %s, %s", status.get(), disposableReference.get()));
        boolean added = disposableReference.compareAndSet(null, disposable);
        if (!added) {
            System.out.println("dispose, current:" + status.get());
            disposable.dispose();
        }
    }

    void becomeFollower() {

        heartbeat();
        if (status.accumulateAndGet(Status.FOLLOWER, (o, n) -> {
            if (o == Status.CANDIDATE || o == Status.LEADER || o == null) {
                disposeLast();
                logger.info("Start heartbeat check interval");
                Disposable disposable = Observable.interval(config.getElectionTimeout() / 2, TimeUnit.MILLISECONDS)
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
                atomicResetDisposable(disposable);
                return n;
            }
            return o;
        }) == Status.FOLLOWER) {
            logger.debug("Status applied: FOLLOWER");
        }
    }

    void becomeCandidate() {
        logStatus(Status.CANDIDATE);
        long socketTimeout = config.getSocketTimeout() > 0 ? config.getSocketTimeout() : 500;
        disposeLast();
        termManager.increaseTerm();
        Disposable disposable = Observable
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
                .subscribe(this::statisticVoteAndChangeStatus,
                        e -> logger.warn("Vote request failed", e));

        atomicResetDisposable(disposable);
    }

    private void onError(String msg, Throwable e) {
        if (e instanceof ConnectException || e instanceof StatusRuntimeException) {
            logger.info(String.format("%s, cause: %s", msg, e));
            logger.trace(msg, e);
        } else {
            logger.warn(msg, e);
        }
    }

    void becomeLeader() {
        logStatus(Status.LEADER);
        long electionCycle = config.getElectionTimeout() * 2 / 3;
        disposeLast();

        OnAppendSuccess onAppendSuccess = new OnAppendSuccess();
        List<EntriesSynchronize> synchronizes = allNodes.stream()
                .filter(n -> !localhost.getName().equals(n.getId()))
                .map(n -> new EntriesSynchronize(n, entryManager, this, termManager, onAppendSuccess))
                .collect(Collectors.toList());

        Disposable disposable = Observable
                .interval(electionCycle, TimeUnit.MILLISECONDS)
                .delay(10, TimeUnit.MILLISECONDS)
                .forEachWhile(times -> {
                    if (status.get() != Status.LEADER) return true;
                    askAppendEntries(synchronizes);
                    return true;
                });
        atomicResetDisposable(disposable);
    }

    void statisticVoteAndChangeStatus(List<Response> response) {
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

    private Vote createVote() {
        EntryInfo latest = entryManager.getLatestLog();
        if (latest == null) latest = EntryManager.NULL_ENTRY_INFO;
        return Vote.newBuilder()
                .setTerm(termManager.getTerm())
                .setNodeId(localhost.getName())
                .setLastEntryInfo(latest)
                .build();
    }

    private void heartbeat() {
        this.lastBeat.set(System.currentTimeMillis());
        logger.debug(String.format(
                "Heartbeat from leader/candidate, term: %s, leader: %s",
                termManager.getTerm(), termManager.getLeaderId()));
    }

    private void logStatus(Status status) {
        EntryInfo lastLog = entryManager.getLatestLog();
        String lastLogInfo = null;
        if (lastLog != null) {
            lastLogInfo = String.format("(prevLogIndex: %s, prevLogTerm: %s)",
                    lastLog.getLogIndex(), lastLog.getCreateTerm());
        }
        logger.info(String.format("%s info: current term: %s, latest log: %s",
                status, termManager.getTerm(), lastLogInfo));
    }

    @Override
    public void accept(Status status) {
        // leader -> follower -> candidate -> candidate/leader/follower
        logger.debug(String.format("Accept status change, last status: %s, to: %s, leader: %s, term: %s",
                this.status.get(), status, termManager.getLeaderId(), termManager.getTerm()));

        switch (status) {
            case LEADER:
                if (this.status.compareAndSet(Status.CANDIDATE, status)) {
                    becomeLeader();
                }
                break;
            case CANDIDATE:
                if (this.status.accumulateAndGet(
                        status,
                        (old, newer) -> (old == Status.FOLLOWER || old == Status.CANDIDATE)
                                ? Status.CANDIDATE
                                : old) == Status.CANDIDATE) {
                    becomeCandidate();
                }
                break;
            default:
                becomeFollower();
        }
    }

    public Status getStatus() {
        return status.get();
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