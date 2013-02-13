package assignment.globalrelay;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * license: BSD - see LICENSE for details
 *
 * @author Bohdan Mushkevych
 *         Description: Module connects to service and updates timestamp of the last successful attempt
 *         In case if waiting threashold is exceeded - an event of ServerUp or ServerDown is fired
 */
public class MonitorWorker implements Runnable {
    protected Logger logger;
    protected ServiceConfiguration configuration;

    private void connectionOk() {
        logger.debug(String.format("Connection OK for %s", configuration.getName()));

        long timestamp = System.currentTimeMillis();
        configuration.setTimestampLastRun(timestamp);
        boolean stateChanged = configuration.getState().markAsRunning(timestamp, configuration.getGraceInterval());
        if (stateChanged) {
            configuration.fireEventServiceUp(timestamp);
        }
    }

    private void connectionNotOk() {
        logger.debug(String.format("Connection NOT OK for %s", configuration.getName()));

        long timestamp = System.currentTimeMillis();
        configuration.setTimestampLastRun(timestamp);
        boolean stateChanged = configuration.getState().markAsNotRunning(timestamp, configuration.getGraceInterval());
        if (stateChanged) {
            configuration.fireEventServiceDown(timestamp);
        }
    }

    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(configuration.getHost(), configuration.getPort());
            if (socket.isConnected()) {
                connectionOk();
            } else {
                connectionNotOk();
            }
        } catch (Exception e) {
            connectionNotOk();
            logger.debug("Unexpected exception on socket connection level", e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                logger.error("Exception on closing socket", e);
            }
        }
    }

    public MonitorWorker(String name) throws IOException {
        this.logger = Logger.getLogger(name);
        configuration = Configurator.getInstance().getConfiguration(name);
    }
}
