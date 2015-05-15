package eu.heronnet.module.bus;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import eu.heronnet.module.bus.handler.FindHandler;
import eu.heronnet.module.bus.handler.GetHandler;
import eu.heronnet.module.bus.handler.PutHandler;

/**
 * @author edoardocausarano
 */
@Configuration
public class BusModuleConfiguration {

    @Bean
    EventBus eventBus() {
        return new AsyncEventBus("MAIN_BUS", Executors.newFixedThreadPool(5));
    }

    @Bean
    FindHandler findHandler() {
        return new FindHandler();
    }

    @Bean
    PutHandler putHandler() {
        return new PutHandler();
    }

    @Bean
    GetHandler getHandler() {
        return new GetHandler();
    }

}
