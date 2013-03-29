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
    public static final String SERVICE_ALPHA = "alpha";
    public static final String SERVICE_BETA = "beta";
    public static final String SERVICE_GAMA = "gama";
    public static final String SERVICE_DELTA = "delta";

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
    }

    @Override
    protected void tearDown() throws Exception {
        if (server != null) {
            server.configurator.reset();
            server.stopServer();
            server = null;
        }
    }

    public void testAllGoodConnections() throws Exception {
        logger.info("testGoogleConnections");
        ServiceConfiguration configuration = new ServiceConfiguration(SERVICE_ALPHA, "www.google.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        // register listener
        for (String name : server.configurator.getServiceNames()) {
            ServiceConfiguration sc = server.configurator.getConfiguration(name);
            sc.addListener(echoListener);
        }
        server.start();
        Thread.sleep(10*1000);
    }

    public void test_3Good_1Bad_Connections() throws Exception {
        logger.info("test_3Good_1Bad_Connections");
        ServiceConfiguration configuration = new ServiceConfiguration(SERVICE_ALPHA, "www.google.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        configuration = new ServiceConfiguration(SERVICE_BETA, "www.yahoo.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        configuration = new ServiceConfiguration(SERVICE_GAMA, "www.bing.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        configuration = new ServiceConfiguration(SERVICE_DELTA, "www.wrongaddress.address", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        // register listener
        for (String name : server.configurator.getServiceNames()) {
            ServiceConfiguration sc = server.configurator.getConfiguration(name);
            sc.addListener(echoListener);
        }
        server.start();
        Thread.sleep(20*1000);
    }

    public void testConnectionsSwitch() throws Exception {
        logger.info("testConnectionsSwitch");
        ServiceConfiguration configuration = new ServiceConfiguration(SERVICE_ALPHA, "www.google.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        configuration = new ServiceConfiguration(SERVICE_BETA, "www.yahoo.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        configuration = new ServiceConfiguration(SERVICE_GAMA, "www.bing.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        configuration = new ServiceConfiguration(SERVICE_DELTA, "www.meta.ua", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        // register listener
        for (String name : server.configurator.getServiceNames()) {
            ServiceConfiguration sc = server.configurator.getConfiguration(name);
            sc.addListener(echoListener);
        }
        server.start();
        Thread.sleep(20*1000);

        configuration = server.configurator.getConfiguration(SERVICE_BETA);
        configuration.setHost("www.wrongaddress.address");
        configuration.setPort(80);
        server.configurator.addConfiguration(configuration);

        Thread.sleep(20*1000);
    }

    public void testGraceThreshold() throws Exception {
        ServiceConfiguration configuration = new ServiceConfiguration(SERVICE_ALPHA, "www.google.com", 80, true, 1, 3);
        configuration.setQueryInterval(10);
        configuration.setGraceInterval(5);
        assertEquals(configuration.getQueryInterval(), 5);
    }

    public static void main(String[] args) {
        String className = Thread.currentThread().getStackTrace()[1].getClassName();
        org.junit.runner.JUnitCore.main(className);
    }
}
