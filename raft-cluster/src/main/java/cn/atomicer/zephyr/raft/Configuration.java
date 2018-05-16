package cn.atomicer.zephyr.raft;

import cn.atomicer.zephyr.raft.exception.ZephyrRaftException;
import cn.atomicer.zephyr.raft.model.Config;
import cn.atomicer.zephyr.raft.model.Machine;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rao Mengnan
 *         on 2018/5/12.
 */
public class Configuration {

    private Config config;
    private Map<String, Machine> machines;

    public Configuration() {
        this.machines = new HashMap<>();
    }

    public void loadConfig(String path) throws IOException {
        File file = new File(path);
        ObjectMapper mapper =
                new ObjectMapper(new YAMLFactory())
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Config config = mapper.readValue(file, Config.class);
        if (config == null) {
            throw new ZephyrRaftException("illegal config file: " + path);
        }
        this.config = config;

        this.machines = new HashMap<>();
        Machine localhost = this.config.getMachineInfo();
        splitMachinePort(localhost);
        machines.put(localhost.getName(), localhost);
        if (config.getPeers() != null) {
            this.config.getPeers().forEach(m ->{
                splitMachinePort(m);
                machines.put(m.getName(), m);
            });
        }
    }

    private void splitMachinePort(Machine machine) {
        if (machine == null) throw new ZephyrRaftException("machine instance must be not null");
        String[] group = machine.getPort().split(":");
        if (group.length != 2) throw new ZephyrRaftException("illegal config: " + machine);
        try {
            machine.setServerPort(Integer.valueOf(group[0]));
            machine.setElectionPort(Integer.valueOf(group[1]));
        } catch (NumberFormatException e) {
            throw new ZephyrRaftException("illegal config: port should be integer: " + machine);
        }
    }

    public Config getConfig() {
        return config;
    }

    public Map<String, ? extends Machine> getMachines() {
        return machines;
    }

}
