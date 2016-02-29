package eu.heronnet.module.gui;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

import com.google.common.eventbus.EventBus;
import eu.heronnet.module.gui.fx.controller.UIController;
import eu.heronnet.module.gui.fx.task.ExtractDocumentMetadataService;
import eu.heronnet.module.gui.fx.task.SearchByPredicateService;
import eu.heronnet.module.gui.fx.task.SignBundleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author edoardocausarano
 */
@Configuration
public class GuiModuleConfiguration {

    @Inject
    ExecutorService executor;

    @Bean(name = {"uiBus"})
    EventBus uiBus() {
        // deliberately not AsyncEventBus so we remain on the UI thread
        return new EventBus("UI_BUS");
    }

    @Bean
    SearchByPredicateService searchByPredicateService() {
        final SearchByPredicateService service = new SearchByPredicateService();
        service.setExecutor(executor);
        return service;
    }

    @Bean
    SignBundleService signBundleService() {
        final SignBundleService service = new SignBundleService();
        service.setExecutor(executor);
        return service;
    }
    @Bean
    ExtractDocumentMetadataService extractDocumentMetadataService() {
        final ExtractDocumentMetadataService service = new ExtractDocumentMetadataService();
        service.setExecutor(executor);
        return service;
    }

    @Bean
    UIController uiController() {
        return new UIController();
    }
}
