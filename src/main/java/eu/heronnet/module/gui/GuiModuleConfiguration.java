package eu.heronnet.module.gui;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import com.google.common.eventbus.EventBus;
import eu.heronnet.module.gui.fx.controller.UIController;
import eu.heronnet.module.gui.fx.task.SearchByPredicate;
import eu.heronnet.module.gui.fx.task.SignBundleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author edoardocausarano
 */
@Configuration
public class GuiModuleConfiguration {

    @Bean(name = {"uiBus"})
    EventBus uiBus() {
        // deliberately not AsyncEventBus so we remain on the UI thread
        return new EventBus("UI_BUS");
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    SearchByPredicate searchByPredicate() {
        return new SearchByPredicate();
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    SignBundleService signBundleService() {
        return new SignBundleService();
    }

    @Bean
    UIController uiController() {
        return new UIController();
    }
}
