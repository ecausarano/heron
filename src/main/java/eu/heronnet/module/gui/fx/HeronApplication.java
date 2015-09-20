package eu.heronnet.module.gui.fx;

import javax.inject.Inject;
import java.io.InputStream;

import com.google.common.util.concurrent.ServiceManager;
import eu.heronnet.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author edoardocausarano
 */

public class HeronApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(HeronApplication.class);

    @Inject
    ServiceManager serviceManager;

    private ApplicationContext applicationContext;

    @Override
    public void start(Stage primaryStage) throws Exception {

        applicationContext = Main.getApplicationContext();
        try (InputStream fxmlStream = HeronApplication.class.getResourceAsStream("/HeronMainWindow.fxml")) {
            Callback controllerFactory = applicationContext.getBean(Callback.class);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setControllerFactory(controllerFactory);
            Parent parent = fxmlLoader.load(fxmlStream);

            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Heron");
            primaryStage.show();
        }
    }

    @Override
    public void stop() {
        logger.debug("Shutting down UIService, threadId={}", Thread.currentThread());
        Runtime.getRuntime().exit(0);
    }
}
