/**
 * Author: Jan De Cooman
 */
package com.hazelcast.consul;

import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveredNode;
import com.hazelcast.spi.discovery.DiscoveryMode;
import com.hazelcast.spi.discovery.DiscoveryProvider;
import com.hazelcast.spi.discovery.SimpleDiscoveredNode;
import com.hazelcast.util.ExceptionUtil;
import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsulDiscovery implements DiscoveryProvider {

    private static final Logger LOG = Logger.getLogger(ConsulDiscovery.class.getName());

    private CatalogClient agentClient;

    final private String consulHost;
    
    final private int consulPort;
    
    final private String serviceName;

    ConsulDiscovery(Map<String, Comparable> properties) {
        this.consulHost = (String) properties.getOrDefault("host", "localhost");
        this.consulPort = (Integer)properties.getOrDefault("port", 8500);
        this.serviceName = (String) properties.get("name");
        if (this.serviceName == null) {
            throw new RuntimeException("Property 'name' is missing in the consul provider");
        }
    }

    /**
     * Open connection to consul.
     * 
     * @param discoveryMode 
     */
    @Override
    public void start(DiscoveryMode discoveryMode) {
        this.agentClient = Consul.
                newClient(consulHost, consulPort).
                catalogClient();
    }

    @Override
    public Collection<DiscoveredNode> discoverNodes() {

        Collection<DiscoveredNode> list = new LinkedList<>();
        Properties empty = new Properties();

        if (this.agentClient != null) {
            try {

                ConsulResponse<List<CatalogService>> service = this.agentClient.getService(serviceName);

                LOG.log(Level.WARNING, "Resolving service: {0}", serviceName);

                for (CatalogService s : service.getResponse()) {
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.log(Level.FINEST, "Found service at: {0}", s.getAddress());
                    }
                    Address address = new Address(s.getAddress(), s.getServicePort());
                    list.add(new SimpleDiscoveredNode(address, empty));
                }

                if (list.isEmpty()) {
                    LOG.warning("No consul instances found!");
                }
                return list;
            } catch (Exception e) {
                LOG.severe(e.getMessage());
                throw ExceptionUtil.rethrow(e);
            }
        }
        return list;
    }

    @Override
    public void destroy() {
    }

}
