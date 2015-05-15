package eu.heronnet.module.kad;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Random;

import javax.inject.Named;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.heronnet.module.kad.model.Node;

/**
 * @author edoardocausarano
 */
@Configuration
public class KadModuleConfiguration {

    @Bean
    @Named("self")
    Node node() {
        try {
            Node self = new Node();

            InetAddress localHost = InetAddress.getLocalHost();
            self.setAddress(new InetSocketAddress(localHost, 6565));
            self.setPort(6565);

            Random random = new Random();
            byte[] randomNodeId = new byte[20];
            random.nextBytes(randomNodeId);
            self.setId(randomNodeId);

            return self;
        }
        catch (UnknownHostException e) {
            throw new RuntimeException("Cannot resolve local host address, is the network down?");
        }
    }
}