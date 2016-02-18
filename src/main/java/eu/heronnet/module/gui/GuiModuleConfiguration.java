package eu.heronnet.module.gui;

import javax.inject.Inject;

import com.google.common.eventbus.EventBus;
import eu.heronnet.module.gui.fx.controller.FileUploadWindowController;
import eu.heronnet.module.gui.fx.controller.MainWindowController;
import eu.heronnet.module.gui.fx.controller.UIController;
import eu.heronnet.module.gui.fx.views.IdentityDetails;
import eu.heronnet.module.gui.fx.views.BundleView;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 * @author edoardocausarano
 */
@Configuration
public class GuiModuleConfiguration {

    @Inject
    ApplicationContext applicationContext;

    @Bean
    public Callback<Class<?>, Object> getControllerFactory() {
        return param -> applicationContext.getBean(param);
    }

    @Bean
    EventBus uiBus() {
        // deliberately not AsyncEventBus so we remain on the UI thread
        return new EventBus("UI_BUS");
    }

    @Bean
    UIController uiController() {
        return new UIController();
    }

    @Bean @Scope("prototype")
    FXMLLoader fxmlLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(getControllerFactory());
        return fxmlLoader;
    }

    @Bean @Lazy
    MainWindowController mainWindowController() {
        return new MainWindowController();
    }

    @Bean @Lazy
    @Scope("prototype")
    FileUploadWindowController fileUploadWindowController() {
        return new FileUploadWindowController();
    }

    @Bean @Lazy
    IdentityDetails identityDetails() {
        return new IdentityDetails();
    }

    @Bean @Lazy @Scope("prototype")
    BundleView localStorageView() {
        return new BundleView();
    }
}
