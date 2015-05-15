package eu.heronnet.module.gui.fx;

import java.io.IOException;
import java.io.InputStream;

import javafx.fxml.FXMLLoader;

import org.springframework.context.ApplicationContext;

/**
 * @author edoardocausarano
 */
public class SpringFxmlLoader {

    private final ApplicationContext context;

    public SpringFxmlLoader(ApplicationContext context) {
        this.context = context;
    }

    public Object load(String url, Class<?> controllerClass) throws IOException {
        try (InputStream fxmlStream = controllerClass.getResourceAsStream(url)) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Object controller = context.getBean(controllerClass);
            fxmlLoader.setController(controller);
            return fxmlLoader.load(fxmlStream);
        }
    }
}
