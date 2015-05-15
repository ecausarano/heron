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

    // @Bean
    // @Scope("prototype")
    // FXMLLoader fxmlLoader() {
    // FXMLLoader fxmlLoader = new FXMLLoader();
    // fxmlLoader.setControllerFactory(
    // getControllerFactory()
    // );
    // return fxmlLoader;
    // }

    @Bean
    public Callback<Class<?>, Object> getControllerFactory() {
        return new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return applicationContext.getBean(param);
            }
        };
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
