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

    @Override
    public String toString() {
        return "Config{" +
                "machineInfo=" + machineInfo +
                ", peers=" + peers +
                '}';
    }
}
