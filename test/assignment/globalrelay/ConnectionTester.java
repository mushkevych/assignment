package assignment.globalrelay;

import junit.framework.TestCase;

import java.util.logging.Logger;

/**
 * license: BSD - see LICENSE for details
 *
 * @author  Bohdan Mushkevych
 * Description: unit tests Service Monitor
 */
public class ConnectionTester extends TestCase {
    public static final String LOGGER_TAG = "unit test";
    public static final Logger logger = Logger.getLogger(LOGGER_TAG);
    private MonitorServer server;

    ServiceListener echoListener = new ServiceListener() {
        @Override
        public void serviceUp(String name, long timestamp) {
            ServiceConfiguration configuration = server.configurator.getConfiguration(name);
            logger.info((String.format("%s service @ %s:%s is UP at %s",
                    configuration.getName(),
                    configuration.getHost(),
                    configuration.getPort(),
                    String.valueOf(timestamp))));
        }

        @Override
        public void serviceDown(String name, long timestamp) {
            ServiceConfiguration configuration = server.configurator.getConfiguration(name);
            logger.info((String.format("%s service @ %s:%s is DOWN at %s",
                    configuration.getName(),
                    configuration.getHost(),
                    configuration.getPort(),
                    String.valueOf(timestamp))));
        }
    };

    @Override
    protected void setUp() throws Exception {
        server = new MonitorServer();
        for (String name : server.configurator.getServiceNames()) {
            ServiceConfiguration configuration = server.configurator.getConfiguration(name);
            configuration.addListener(echoListener);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if (server != null) {
            server.stopServer();
        }
    }

    public void testAllGoodConnections() throws Exception {
        logger.info("testAllGoodConnections");
        for (String name : server.configurator.getServiceNames()) {
            ServiceConfiguration configuration = server.configurator.getConfiguration(name);
            configuration.setHost("www.google.com");
            configuration.setPort(80);
        }

        server.start();
        Thread.sleep(10*1000);
    }

    public void test_3Good_1Bad_Connections() throws Exception {
        logger.info("test_3Good_1Bad_Connections");
        ServiceConfiguration configuration = server.configurator.getConfiguration(Configurator.SERVICE_ALPHA);
        configuration.setHost("www.google.com");
        configuration.setPort(80);

        configuration = server.configurator.getConfiguration(Configurator.SERVICE_BETA);
        configuration.setHost("www.yahoo.com");
        configuration.setPort(80);

        configuration = server.configurator.getConfiguration(Configurator.SERVICE_GAMA);
        configuration.setHost("www.bing.com");
        configuration.setPort(80);

        configuration = server.configurator.getConfiguration(Configurator.SERVICE_DELTA);
        configuration.setHost("www.wrongaddress.address");
        configuration.setPort(80);

        server.start();
        Thread.sleep(20*1000);
    }

    public void testConnectionsSwitch() throws Exception {
        logger.info("testConnectionsSwitch");
        ServiceConfiguration configuration = server.configurator.getConfiguration(Configurator.SERVICE_ALPHA);
        configuration.setHost("www.google.com");
        configuration.setPort(80);

        configuration = server.configurator.getConfiguration(Configurator.SERVICE_BETA);
        configuration.setHost("www.yahoo.com");
        configuration.setPort(80);

        configuration = server.configurator.getConfiguration(Configurator.SERVICE_GAMA);
        configuration.setHost("www.bing.com");
        configuration.setPort(80);

        configuration = server.configurator.getConfiguration(Configurator.SERVICE_DELTA);
        configuration.setHost("www.meta.ua");
        configuration.setPort(80);

        server.start();
        Thread.sleep(20*1000);

        configuration = server.configurator.getConfiguration(Configurator.SERVICE_BETA);
        configuration.setHost("www.wrongaddress.address");
        configuration.setPort(80);
        Thread.sleep(20*1000);
    }

    public void testGraceThreshold() throws Exception {
        ServiceConfiguration configuration = server.configurator.getConfiguration(Configurator.SERVICE_ALPHA);
        configuration.setQueryInterval(10);
        configuration.setGraceInterval(5);
        assertEquals(configuration.getQueryInterval(), 5);
    }

    public static void main(String[] args) {
        String className = Thread.currentThread().getStackTrace()[1].getClassName();
        org.junit.runner.JUnitCore.main(className);
    }
}
