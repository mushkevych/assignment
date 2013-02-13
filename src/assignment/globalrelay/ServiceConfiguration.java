package assignment.globalrelay;

import java.util.ArrayList;
import java.util.List;

/**
 * license: BSD - see LICENSE for details
 *
 * @author Bohdan Mushkevych
 * Description: wrapper around service configuration parameters
 */
public class ServiceConfiguration {
    private String name;
    private String host;
    private int port;

    private boolean isRunning;       //true-thread is up. false - thread is dead
    private int queryInterval;       //seconds
    private int graceInterval;       //seconds

    private long outageLowerBoundary;
    private long outageUpperBoundary;

    private long timestampLastRun;
    private ServiceState state = new ServiceState();

    private List<ServiceListener> listeners = new ArrayList<ServiceListener>();

    public ServiceConfiguration(String name,
                                String host,
                                int port,
                                boolean running,
                                int queryInterval,
                                int graceInterval) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.isRunning = running;
        this.queryInterval = queryInterval;
        this.graceInterval = graceInterval;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    /**
     * If the grace time is less than the polling frequency, the monitor should schedule extra checks of the service.
     * if #graceInterval is less than #queryInterval then #graceInterval is returned
     */
    public synchronized int getQueryInterval() {
        if (graceInterval < queryInterval) {
            return graceInterval;
        } else {
            return queryInterval;
        }
    }

    public void setQueryInterval(int queryInterval) {
        this.queryInterval = queryInterval;
    }

    public int getGraceInterval() {
        return graceInterval;
    }

    public void setGraceInterval(int graceInterval) {
        this.graceInterval = graceInterval;
    }

    synchronized public long[] getOutageBoundaries() {
        return new long[]{outageLowerBoundary, outageUpperBoundary};
    }

    public long getTimestampLastRun() {
        return timestampLastRun;
    }

    public void setTimestampLastRun(long timestampLastRun) {
        this.timestampLastRun = timestampLastRun;
    }

    public ServiceState getState() {
        return state;
    }

    public void setOutageLowerBoundary(long outageLowerBoundary) {
        this.outageLowerBoundary = outageLowerBoundary;
    }

    public void setOutageUpperBoundary(long outageUpperBoundary) {
        this.outageUpperBoundary = outageUpperBoundary;
    }

    synchronized public boolean isInOutage(long currentTimestamp) {
        if(outageLowerBoundary > 0 && outageUpperBoundary > 0) {
            return currentTimestamp >= outageLowerBoundary && currentTimestamp <= outageUpperBoundary;
        }
        return false;
    }

    synchronized public void addListener(ServiceListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    synchronized public void removeListener(ServiceListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    synchronized public void fireEventServiceUp(long timestamp) {
        for (ServiceListener listener : listeners) {
            listener.serviceUp(name, timestamp);
        }
    }

    synchronized public void fireEventServiceDown(long timestamp) {
        for (ServiceListener listener : listeners) {
            listener.serviceDown(name, timestamp);
        }
    }
}
