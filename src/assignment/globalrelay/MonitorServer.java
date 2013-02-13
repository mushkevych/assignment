package assignment.globalrelay;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * license: BSD - see LICENSE for details
 *
 * @author Bohdan Mushkevych
 *         Description: Monitor Server manages Monitor Workers (starts them and stop them accordingly)
 */

public class MonitorServer extends Thread {
    public static final int MAIN_THREAD_INTERVAL_MILLISECONDS = 1000;
    public static final String PROPERTY_LOG4J = "log4j.properties";

    protected boolean isThreadRunning = true;
    protected Configurator configurator = Configurator.getInstance();

    protected Logger logger;
    protected ExecutorService threadPool = Executors.newCachedThreadPool();

    static {
        try {
            PropertyConfigurator.configure(System.getProperty(PROPERTY_LOG4J));
        } catch (Exception e) {
            System.out.println("Property file can not be retrieved. Check JVM parameters.");
        }
    }

    public MonitorServer() {
        logger = Logger.getLogger(this.getClass().getSimpleName());
        logger.info(String.format("Started Monitoring Server at %s", System.currentTimeMillis()));
    }

    protected boolean isTimeToRun(ServiceConfiguration configuration) {
        long timestamp = System.currentTimeMillis();
        return configuration.isRunning()
                && !configuration.isInOutage(timestamp)
                && (timestamp >= configuration.getTimestampLastRun() + configuration.getQueryInterval() * 1000);
    }

    public void run() {
        try {
            while (isThreadRunning) {
                try {
                    for (String serviceName : configurator.getServiceNames()) {
                        ServiceConfiguration configuration = configurator.getConfiguration(serviceName);
                        if (!isTimeToRun(configuration)) {
                            continue;
                        }

                        threadPool.submit(new MonitorWorker(serviceName));
                    }

                    Thread.sleep(MAIN_THREAD_INTERVAL_MILLISECONDS);
                } catch (Exception e) {
                    logger.error("Exception on worker spawning level", e);
                }
            }
        } catch (Exception e) {
            logger.error("Server side exception.", e);
        }

        logger.info(String.format("Monitoring Server Stopped at %s", System.currentTimeMillis()));
    }

    public void stopServer() {
        isThreadRunning = false;
    }

    public static void main(String argv[]) throws Exception {
        MonitorServer server = new MonitorServer();
        server.start();
    }
}
