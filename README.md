# Hazelcast Consul Resolver

The plugin for Hazelcast fetches nodes belonging to a given service from the Consul server. Currently it has been implemented against https://github.com/hazelcast-incubator/hazelcast-discovery-spi.

In order to get this plugin running, you must place the JAR on the classpath along with this above build of Hazelcast. Then you must alter the cluster.xml like this:

 ```
 <join>
    <multicast enabled="false">
        .....................
    </multicast>

    <discovery-providers>
        <discovery-provider class="com.braindrainpain.hazelcast.consul.ConsulDiscovery" enabled="true">
          <properties>
              <property name ="host">consul</property>
              <property name ="port">8500</property>
              <property name ="name">my-hazelcast-service</property>
          </properties>
        </discovery-provider>
    </discovery-providers>
</join>
```
        
It is important to know that you must register you Hazelcast service in Consul along with the appropriate port (e.g. 5701). The ConsulDiscovery will read all IPs from this service together with their ports!

# Authors
- Jan De Cooman
-  Quan Weng

Thanks to Christoph Engelbert from Hazelcast for the support.