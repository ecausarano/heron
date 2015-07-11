package eu.heronnet.config;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

/**
 * @author edoardocausarano
 */
@Configuration
@ComponentScan(basePackages = "eu.heronnet", includeFilters = @ComponentScan.Filter(
        Configuration.class
))
public class SpringConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpringConfiguration.class);

    @Inject
    ApplicationContext applicationContext;

    @Bean
    Executor executor() {
        return Executors.newWorkStealingPool();
    }

    @Bean
    ServiceManager serviceManager() {
        Map<String, Service> services = applicationContext.getBeansOfType(Service.class);
        ServiceManager manager = new ServiceManager(services.values());

        manager.addListener(new ServiceManager.Listener() {
            public void stopped() {}
            public void healthy() {
                logger.debug("Done initializing services, Heron application up");
            }
            public void failure(Service service) {
                logger.error("Service={} has failed to bootstrap, exiting", service.getClass());
            }
        }, executor());

        return manager;
    }
}
