package cn.atomicer.raft.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author Rao Mengnan
 *         on 2018/5/12.
 */
public class Configuration implements Serializable {

    @JsonProperty("machine")
    private NodeInfo nodeInfo;
    private List<NodeInfo> peers;
    @JsonProperty("socket.timeout")
    private long socketTimeout;
    @JsonProperty("election.timeout")
    private long electionTimeout;

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public List<NodeInfo> getPeers() {
        return peers;
    }

    public void setPeers(List<NodeInfo> peers) {
        this.peers = peers;
    }

    public long getElectionTimeout() {
        return electionTimeout;
    }

    public void setElectionTimeout(long electionTimeout) {
        this.electionTimeout = electionTimeout;
    }

    public long getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(long socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    @Override
    public String toString() {
        return "Config{" +
                "nodeInfo=" + nodeInfo +
                ", peers=" + peers +
                ", socketTimeout=" + socketTimeout +
                ", electionTimeout=" + electionTimeout +
                '}';
    }
}
