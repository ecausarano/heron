package eu.heronnet.module.kad;

import eu.heronnet.module.kad.model.RadixTree;
import eu.heronnet.module.kad.model.TreeSetImpl;
import eu.heronnet.module.kad.net.IdGenerator;
import eu.heronnet.module.kad.net.SelfNodeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;
import java.net.SocketException;

/**
 * @author edoardocausarano
 */
@Configuration
@ComponentScan(basePackages = "eu.heronnet.module.kad")
public class KadModuleConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    IdGenerator idGenerator;

    @Bean
    @Named("self")
    SelfNodeProvider selfNodeProvider() {
        return new SelfNodeProvider();
    }

    @Bean
    RadixTree radixTree() throws SocketException {
        return new TreeSetImpl(selfNodeProvider().getSelf());
    }
}
