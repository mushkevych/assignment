package assignment.globalrelay;

import java.util.HashMap;
import java.util.Map;

/**
 * license: BSD - see LICENSE for details
 *
 * @author Bohdan Mushkevych
 * Description: JMX empowered singleton to hold map of service configurations
 */
public class Configurator {
    private Map<String, ServiceConfiguration> configuration = new HashMap<String, ServiceConfiguration>();

    // Singleton init block
    private static final Configurator instance = new Configurator();

    private Configurator() {}

    public static Configurator getInstance() {
        return instance;
    }

    public ServiceConfiguration getConfiguration(String name) {
        return configuration.get(name);
    }

    public void addConfiguration(ServiceConfiguration serviceConfiguration) {
        configuration.put(serviceConfiguration.getName(), serviceConfiguration);
    }

    public void removeConfiguration(String name) {
        configuration.remove(name);
    }

    public void reset() {
        configuration.clear();
    }

    public Iterable<String> getServiceNames() {
        return configuration.keySet();
    }
}
