package eu.heronnet.module.gui;

import javax.inject.Inject;

import eu.heronnet.module.gui.fx.controller.FileUploadWindowController;
import eu.heronnet.module.gui.fx.controller.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    @Scope("prototype")
    FXMLLoader fxmlLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(getControllerFactory());
        return fxmlLoader;
    }

    @Bean
    @Scope("prototype")
    MainWindowController mainWindowController() {
        return new MainWindowController();
    }

    @Bean
    @Scope("prototype")
    FileUploadWindowController fileUploadWindowController() {
        return new FileUploadWindowController();
    }
}
