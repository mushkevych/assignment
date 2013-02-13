package assignment.globalrelay;

/**
 * license: BSD - see LICENSE for details
 *
 * @author Bohdan Mushkevych
 * Description: Interface of main monitoring events: server up and server down
 */
public interface ServiceListener {
    void serviceUp(String name, long timestamp);
    void serviceDown(String name, long timestamp);
}
