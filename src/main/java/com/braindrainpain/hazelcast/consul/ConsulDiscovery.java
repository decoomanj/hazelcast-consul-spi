/**
 * Author: Jan De Cooman
 */
package com.braindrainpain.hazelcast.consul;

import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.hazelcast.util.ExceptionUtil;
import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsulDiscovery implements DiscoveryStrategy {

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
     */
    @Override
    public void start() {
        this.agentClient = Consul.
                newClient(consulHost, consulPort).
                catalogClient();
    }

    @Override
    public Collection<DiscoveryNode> discoverNodes() {

        Collection<DiscoveryNode> list = new LinkedList<>();
        if (this.agentClient != null) {
            try {

                ConsulResponse<List<CatalogService>> service = this.agentClient.getService(serviceName);

                LOG.log(Level.WARNING, "Resolving service: {0}", serviceName);

                for (CatalogService s : service.getResponse()) {
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.log(Level.FINEST, "Found service at: {0}", s.getAddress());
                    }
                    Address address = new Address(s.getAddress(), s.getServicePort());
                    list.add(new SimpleDiscoveryNode(address));
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
