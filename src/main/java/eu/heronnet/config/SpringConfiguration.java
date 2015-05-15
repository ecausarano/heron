package eu.heronnet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import eu.heronnet.kad.model.Node;
import eu.heronnet.kad.model.RadixTree;
import eu.heronnet.kad.model.RadixTreeImpl;
import eu.heronnet.kad.net.Client;
import eu.heronnet.kad.net.ClientImpl;
import eu.heronnet.kad.net.Server;
import eu.heronnet.kad.net.ServerImpl;
import eu.heronnet.kad.net.codec.KadMessageCodec;
import eu.heronnet.kad.net.handler.FindNodeRequestHandler;
import eu.heronnet.kad.net.handler.PingRequestHandler;
import eu.heronnet.kad.net.handler.StoreValueRequestHandler;
import eu.heronnet.module.network.dht.DHTService;
import eu.heronnet.module.network.dht.DHTServiceImpl;

/**
 * @author edoardocausarano
 */
@Configuration
@ComponentScan(basePackages = "eu.heronnet")
public class SpringConfiguration {

    @Bean
    DHTService dhtService() {
        return new DHTServiceImpl();
    }

    @Bean
    Client client() {
        return new ClientImpl();
    }

    @Bean
    Server server() {
        return new ServerImpl();
    }

    @Bean
    RadixTree radixTree() {
        return new RadixTreeImpl();
    }

    @Bean
    Node node() {
        return new Node();
    }

    @Bean
    KadMessageCodec kadMessageCodec() {
        return new KadMessageCodec();
    }

    @Bean
    PingRequestHandler pingRequestHandler() {
        return new PingRequestHandler();
    }

    @Bean
    FindNodeRequestHandler findNodeRequestHandler() {
        return new FindNodeRequestHandler();
    }

    @Bean
    StoreValueRequestHandler storeValueRequestHandler() {
        return new StoreValueRequestHandler();
    }

}
