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
    public static final String SERVICE_ALPHA = "alpha";
    public static final String SERVICE_BETA = "beta";
    public static final String SERVICE_GAMA = "gama";
    public static final String SERVICE_DELTA = "delta";

    public static final String LOCALHOST = "127.0.0.1";

    private Map<String, ServiceConfiguration> configuration = new HashMap<String, ServiceConfiguration>();

    // Singleton init block
    private static final Configurator instance;

    static {
        instance = new Configurator();
        instance.configuration.put(SERVICE_ALPHA, new ServiceConfiguration(SERVICE_ALPHA, LOCALHOST, 9991, true, 1, 3));
        instance.configuration.put(SERVICE_BETA, new ServiceConfiguration(SERVICE_BETA, LOCALHOST, 9992, true, 1, 3));
        instance.configuration.put(SERVICE_GAMA, new ServiceConfiguration(SERVICE_GAMA, LOCALHOST, 9993, true, 1, 3));
        instance.configuration.put(SERVICE_DELTA, new ServiceConfiguration(SERVICE_DELTA, LOCALHOST, 9994, true, 1, 3));
    }

    private Configurator() {}

    public static Configurator getInstance() {
        return instance;
    }

    public ServiceConfiguration getConfiguration(String name) {
        return configuration.get(name);
    }

    public Iterable<String> getServiceNames() {
        return configuration.keySet();
    }
}
