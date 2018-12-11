
### 实现raft共识算法，使用 `gRPC` 完成raft集群间的通信

在阅读相关文章后，决定尝试自己实现raft共识算法加深理解。

第一版的实现，用的是ED-FSM（事件驱动状态机）

#### 参考文档

关于raft的介绍很多，有以下文档可以参考：

1. [The Raft Consensus Algorithm ](1)
2. [In Search of an Understandable Consensus Algorithm](2)
3. [Raft一致性算法论文：探索一种易理解的一致性算法 中文译文](3)
4. [CoreOS 实战：剖析 etcd](4)
5. [etcd/raft design](5)



#### etcd/raft中的实现细节

> Raft as a state machine. The state machine takes a `Message` as input. A message can either be a local timer update or a network message sent from a remote peer. The state machine's output is a 3-tuple `{[]Messages, []LogEntries, NextState}` consisting of an array of `Messages`, `log entries`, and `Raft state changes`. For state machines with the same state, the same state machine input should always generate the same state machine output.

欲理解etcd/raft的实现，我只关注三个点：
1. etcd/raft的实现和我对raft算法的理解是否有偏差
2. etcd/raft如何处理snapshot
3. etcd/raft中如何实现复制状态机






#### etcd/raft的日志同步

etcd/raft中用了`Progress`来控制leader和follower之间的日志同步，progress 表示的是在Leader视角下的Follower的日志复制的进度，其中包含了 `probe` `snapshot` `replicate`三种状态，以下是etcd的desin文稿中的描述以及我的部分整理：

```
                            +--------------------------------------------------------+          
                            |                  send snapshot                         |          
                            |                                                        |          
                  +---------+----------+                                  +----------v---------+
              +--->       probe        |                                  |      snapshot      |
              |   |  max inflight = 1  <----------------------------------+  max inflight = 0  |
              |   +---------+----------+                                  +--------------------+
              |             |            1. snapshot success                                    
              |             |               (next=snapshot.index + 1)                           
              |             |            2. snapshot failure                                    
              |             |               (no change)                                         
              |             |            3. receives msgAppResp(rej=false&&index>lastsnap.index)
              |             |               (match=m.index,next=match+1)                        
receives msgAppResp(rej=true)                                                                   
(next=match+1)|             |                                                                   
              |             |                                                                   
              |             |                                                                   
              |             |   receives msgAppResp(rej=false&&index>match)                     
              |             |   (match=m.index,next=match+1)                                    
              |             |                                                                   
              |             |                                                                   
              |             |                                                                   
              |   +---------v----------+                                                        
              |   |     replicate      |                                                        
              +---+  max inflight = n  |                                                        
                  +--------------------+                                                        
```

##### Probe状态

> `max inflight = 1 `

在这个状态中，leader会在每个心跳周期内至多发送一个复制信息(`replication message`) 到progress对应的follower，此时leader发送的速度是缓慢的，并同时尝试找出follower真实匹配的index（当收到的回复`msgAppResp`是reject类型时，会触发下一交发送操作，可见raft论文中对index匹配的描述）



##### Replicate状态
> `max inflight = n `

leader会发送大量的日志条目到follower中，这个过程etcd-raft是做了优化的

##### Snapshot状态
> `max inflight = 0 `

在这个状态中，leader不会向follower中发送任何日志条目

##### 状态切换

progerss允许的状态切换： `probe  <-> replicate <-> snapshot`

1. init: probe

每一任新的leader产生时， 会针对所有的follower初始化一个progress，并将progerss的状态初始化为`Probe`，由此leader会尝试慢慢地试探follower中可匹配的index。

> etcd/raft/design.md: A newly elected leader sets the progresses of all the followers to probe state with match = 0 and next = last index [etcd/raft design](5)


2. probe <-> replicate

当progress的状态为probe时，leader在试探性地发送`replication message`, 在开始时 match 为 0, next 为最新的日志index + 1，从此处开始试探，直到找到匹配的index为止;

当progress的状态为replicate时，leader会按nextIndex发送日志条目到follower，但可能由于网络通信、机器故障等原因，造成node之间同步失败，follower返回reject信息，此时重新回到probe状态（The progress will fall back to probe when the follower replies a rejection msgAppResp or the link layer reports the follower is unreachable）

> The leader maintains a nextIndex for each follower,
which is the index of the next log entry the leader will
send to that follower. When a leader first comes to power,
it initializes all nextIndex values to the index just after the
last one in its log (11 in Figure 7). If a follower’s log is
inconsistent with the leader’s, the AppendEntries consistency
check will fail in the next AppendEntries RPC. After
a rejection, the leader decrements nextIndex and retries
the AppendEntries RPC. Eventually nextIndex will reach
a point where the leader and follower logs match. When
this happens, AppendEntries will succeed, which removes
any conflicting entries in the follower’s log and appends
entries from the leader’s log (if any). Once AppendEntries
succeeds, the follower’s log is consistent with the leader’s,
and it will remain that way for the rest of the term. [raft paper](2)

3. probe <-> snapshot

当follower的index落后当前太多条目或需要创建一个snapshot时, leader会发送一个msgSnap的消息，然后等待follower返回任何成功、失败、中止操作等response后重新转为`probe`状态，在这个过程中leader不会向follower发送任何日志条目







[1]: https://raft.github.io
[2]: https://raft.github.io/raft.pdf
[3]: http://blog.luoyuanhang.com/2017/02/02/raft-paper-in-zh-CN/
[4]: https://www.infoq.cn/article/coreos-analyse-etcd
[5]: https://github.com/etcd-io/etcd/blob/master/raft/design.md
