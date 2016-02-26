package eu.heronnet.module.gui.fx;

import javax.inject.Inject;

import com.google.common.util.concurrent.ServiceManager;
import eu.heronnet.Main;
import eu.heronnet.module.gui.fx.views.MainWindowView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
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

    @Override
    public void start(Stage primaryStage) throws Exception {

        ApplicationContext applicationContext = Main.getApplicationContext();

        try {
            final MainWindowView mainWindowView = new MainWindowView(applicationContext::getBean);

            Scene scene = new Scene(mainWindowView);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Heron");
            primaryStage.show();
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void stop() {
        logger.debug("Shutting down UIService, threadId={}", Thread.currentThread());
        Runtime.getRuntime().exit(0);
    }
}
