package eu.heronnet.module.bus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import eu.heronnet.module.bus.handler.FindHandler;
import eu.heronnet.module.bus.handler.GetHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.concurrent.Executor;

/**
 * @author edoardocausarano
 */
@Configuration
public class BusModuleConfiguration {

    @Inject
    Executor executor;

    @Bean()
    EventBus mainBus() {
        return new AsyncEventBus("MAIN_BUS", executor);
    }

    @Bean
    FindHandler findHandler() {
        return new FindHandler();
    }

    @Bean
    GetHandler getHandler() {
        return new GetHandler();
    }

}
