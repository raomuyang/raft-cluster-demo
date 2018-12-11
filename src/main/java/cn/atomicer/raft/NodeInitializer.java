package cn.atomicer.raft;

import cn.atomicer.raft.annotation.Status;
import cn.atomicer.raft.manager.NodeManager;
import cn.atomicer.raft.manager.NodeStatusManager;
import cn.atomicer.raft.manager.NodeTermManager;
import cn.atomicer.raft.model.Configuration;
import cn.atomicer.raft.model.NodeInfo;
import cn.atomicer.raft.proto.EntryInfo;
import cn.atomicer.raft.rpc.NodeProxy;
import cn.atomicer.raft.rpc.NodeProxyGrpcService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Rao Mengnan
 * on 2018/11/21.
 */
public class NodeInitializer {
    private Log logger = LogFactory.getLog(getClass());

    private TermManager termManager;
    private EntryManager entryManager;
    private StatusManager statusManager;
    private NodeManager nodeManager;

    private NodeProxyGrpcService service;

    private Configuration config;
    private List<Node> nodeList;  // include localhost
    private Function<NodeInfo, Node> nodeProxyFactory;

    public NodeInitializer(String configPath, EntryManager entryManager) throws IOException {
        this(loadConfig(configPath), entryManager);
    }

    public NodeInitializer(String configPath, EntryManager entryManager,
                           Function<NodeInfo, Node> nodeProxyFactory) throws IOException {
        this(loadConfig(configPath), entryManager, nodeProxyFactory);
    }

    public NodeInitializer(Configuration config, EntryManager entryManager) {
        this(config, entryManager, NodeProxy::new);
    }

    public NodeInitializer(Configuration config, EntryManager entryManager, Function<NodeInfo, Node> nodeProxyFactory) {
        this.entryManager = entryManager;
        this.config = config;
        this.nodeProxyFactory = nodeProxyFactory;

        this.nodeList = Lists.newArrayList();
        this.initManagers();
        this.initNodeList();

        this.service = new NodeProxyGrpcService(this.nodeManager, this.config.getNodeInfo().getPort());
        logger.info(String.format("Managers initialized, status: %s, peers join: %s",
                statusManager.getStatus(), nodeList.size()));
    }

    private void initNodeList() {
        this.nodeList.add(nodeManager);
        this.nodeList.addAll(initPeers());
    }

    private List<Node> initPeers() {
        List<NodeInfo> peersInfo = config.getPeers();
        if (peersInfo == null) return Lists.newArrayList();
        return peersInfo
                .stream()
                .map(nodeProxyFactory)
                .collect(Collectors.toList());
    }

    private void initManagers() {
        Supplier<EntryInfo> latestLogSupplier = () -> entryManager.getLatestLog();
        Consumer<Status> statusConsumer = (status) -> {
            // lazy load
            if (statusManager == null) {
                logger.warn("Status consumer still uninitialized");
                return;
            }
            statusManager.accept(status);
        };

        this.termManager = new NodeTermManager(latestLogSupplier, statusConsumer);
        this.statusManager = new NodeStatusManager(config, termManager, entryManager, nodeList);
        this.nodeManager = new NodeManager(config, termManager, entryManager, statusManager);
    }

    private static Configuration loadConfig(String path) throws IOException {
        File file = new File(path);
        ObjectMapper mapper =
                new ObjectMapper(new YAMLFactory())
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Configuration config = mapper.readValue(file, Configuration.class);
        if (config == null) {
            throw new IllegalArgumentException("Illegal config file: " + path);
        }
        return config;

    }

    public EntryManager getEntryManager() {
        return entryManager;
    }

    public void setEntryManager(EntryManager entryManager) {
        this.entryManager = entryManager;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public Function<NodeInfo, Node> getNodeProxyFactory() {
        return nodeProxyFactory;
    }

    public void setNodeProxyFactory(Function<NodeInfo, Node> nodeProxyFactory) {
        this.nodeProxyFactory = nodeProxyFactory;
    }

    public TermManager getTermManager() {
        return termManager;
    }

    public void setTermManager(TermManager termManager) {
        this.termManager = termManager;
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }

    public void setStatusManager(StatusManager statusManager) {
        this.statusManager = statusManager;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public void setNodeManager(NodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    public NodeProxyGrpcService getService() {
        return service;
    }
}
