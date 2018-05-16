package cn.atomicer.zephyr.raft.model;

/**
 * @author Rao Mengnan
 *         on 2018/5/12.
 */
public class Machine {
    private String host;
    private String port; // server:election
    private String name;

    // extensions
    private int serverPort;
    private int electionPort;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getElectionPort() {
        return electionPort;
    }

    public void setElectionPort(int electionPort) {
        this.electionPort = electionPort;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
