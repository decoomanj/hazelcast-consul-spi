/*
* Author: Jan De Cooman
*/
package com.braindrainpain.hazelcast.consul;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jan
 */
public class ConsulDiscoveryFactory implements DiscoveryStrategyFactory {

    private final Collection<PropertyDefinition> propertyDefinitions;

    public ConsulDiscoveryFactory() {
        List<PropertyDefinition> properties = new ArrayList<>();
        properties.add(new SimplePropertyDefinition("host", PropertyTypeConverter.STRING));
        properties.add(new SimplePropertyDefinition("port", PropertyTypeConverter.INTEGER));
        properties.add(new SimplePropertyDefinition("name", PropertyTypeConverter.STRING));
        this.propertyDefinitions = Collections.unmodifiableCollection(properties);
    }

    @Override
    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
        return ConsulDiscovery.class;
    }

    @Override
    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger,
                                                  Map<String, Comparable> properties) {
        return new ConsulDiscovery(properties);
    }

    @Override
    public Collection<PropertyDefinition> getConfigurationProperties() {
        return propertyDefinitions;
    }

}
