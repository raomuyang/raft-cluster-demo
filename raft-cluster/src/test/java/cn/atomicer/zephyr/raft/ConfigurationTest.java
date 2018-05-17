package cn.atomicer.zephyr.raft;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Rao Mengnan
 *         on 2018/5/12.
 */
public class ConfigurationTest {
    @Test
    public void testLoadConfig() throws Exception {
        Configuration configuration = new Configuration();
        configuration.loadConfig(getClass().getResource("/config.yml").getPath());
        assertNotNull(configuration.getConfig());
        assertEquals(3, configuration.getMachines().size());

        assertEquals("127.0.0.1", configuration.getConfig().getMachineInfo().getHost());
        assertEquals(1734, configuration.getConfig().getMachineInfo().getServerPort());
        assertEquals(2734, configuration.getConfig().getMachineInfo().getElectionPort());
        assertEquals(30, configuration.getConfig().getMaxConnections());
    }

}