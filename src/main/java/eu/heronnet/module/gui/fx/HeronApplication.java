package eu.heronnet.module.gui.fx;

import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.springframework.context.ApplicationContext;

import eu.heronnet.Main;

/**
 * @author edoardocausarano
 */
public class HeronApplication extends Application {

    private ApplicationContext applicationContext;

    @Override
    public void start(Stage primaryStage) throws Exception {

        applicationContext = Main.getApplicationContext();
        try (InputStream fxmlStream = HeronApplication.class.getResourceAsStream("/HeronMainWindowV2.fxml")) {
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
}
