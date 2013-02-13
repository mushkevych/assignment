package assignment.globalrelay;

/**
 * license: BSD - see LICENSE for details
 *
 * @author Bohdan Mushkevych
 *         Description: tracks duration of service being in either of state RUNNING or NOT_RUNNING
 */
public class ServiceState {
    public enum State {
        UNKNOWN,
        RUNNING,
        NOT_RUNNING;
    }

    private long stateTimestamp;
    private State state = State.UNKNOWN;

    /**
     * @param timestamp of the event
     * @param afterNSeconds grace period before temporary state change is considered permanent
     * @return true if server was OFF before, and now was switched ON
     */
    public boolean markAsRunning(long timestamp, int afterNSeconds) {
        switch (state) {
            case UNKNOWN:
                state = State.RUNNING;
                stateTimestamp = System.currentTimeMillis();
                return false;

            case NOT_RUNNING:
                // this is _possible_ server UP event
                if (stateTimestamp + afterNSeconds * 1000 <= timestamp) {
                    // this is server UP event for SURE
                    state = State.RUNNING;
                    stateTimestamp = timestamp;
                    return true;
                }
                return false;

            case RUNNING:
                // falls thru
            default:
                return false;
        }
    }

    /**
     * @param timestamp of the event
     * @param afterNSeconds grace period before temporary state change is considered permanent
     * @return true if server was ON before, and now was switched OFF
     */
    public boolean markAsNotRunning(long timestamp, int afterNSeconds) {
        switch (state) {
            case UNKNOWN:
                state = State.NOT_RUNNING;
                stateTimestamp = System.currentTimeMillis();
                return false;

            case RUNNING:
                // this is _possible_ server DOWN event
                if (stateTimestamp + afterNSeconds * 1000 <= timestamp) {
                    // this is server DOWN event for SURE
                    state = State.NOT_RUNNING;
                    stateTimestamp = timestamp;
                    return true;
                }
                return false;

            case NOT_RUNNING:
                // falls thru
            default:
                return false;
        }
    }
}
