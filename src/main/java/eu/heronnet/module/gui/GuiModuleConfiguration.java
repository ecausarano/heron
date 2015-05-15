package eu.heronnet.module.gui;

import javafx.util.Callback;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import eu.heronnet.module.gui.fx.controller.FileUploadWindowController;
import eu.heronnet.module.gui.fx.controller.MainWindowController;

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
    MainWindowController mainWindowController() {
        return new MainWindowController();
    }

    @Bean
    @Scope("prototype")
    FileUploadWindowController fileUploadWindowController() {
        return new FileUploadWindowController();
    }
}
