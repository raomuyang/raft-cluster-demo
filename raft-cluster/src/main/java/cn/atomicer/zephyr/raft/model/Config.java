package cn.atomicer.zephyr.raft.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author Rao Mengnan
 *         on 2018/5/12.
 */
public class Config implements Serializable {

    @JsonProperty("machine")
    private Machine machineInfo;
    private List<Machine> peers;

    @JsonProperty("socket.timeout")
    private long socketTimeout;
    @JsonProperty("heartbeat.timeout")
    private long heartbeatTimeout;
    @JsonProperty("heartbeat.cycle")
    private long heartbeatCycle;
    @JsonProperty("election.timeout")
    private long electionTimeout;
    @JsonProperty("election.connection.max")
    private int maxConnections;

    public Machine getMachineInfo() {
        return machineInfo;
    }

    public void setMachineInfo(Machine machineInfo) {
        this.machineInfo = machineInfo;
    }

    public List<Machine> getPeers() {
        return peers;
    }

    public void setPeers(List<Machine> peers) {
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

    public long getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(long heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public long getHeartbeatCycle() {
        return heartbeatCycle;
    }

    public void setHeartbeatCycle(long heartbeatCycle) {
        this.heartbeatCycle = heartbeatCycle;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @Override
    public String toString() {
        return "Config{" +
                "machineInfo=" + machineInfo +
                ", peers=" + peers +
                ", electionTimeout=" + electionTimeout +
                ", socketTimeout=" + socketTimeout +
                ", heartbeatTimeout=" + heartbeatTimeout +
                ", heartbeatCycle=" + heartbeatCycle +
                ", maxConnections=" + maxConnections +
                '}';
    }
}
