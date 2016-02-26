package eu.heronnet.module.gui.fx.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import eu.heronnet.module.gui.fx.controller.UIController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edoardocausarano
 */
public class CreateIdentityWizard extends GridPane {

    private static final Logger logger = LoggerFactory.getLogger(CreateIdentityWizard.class);
    private final Function<Class, ?> delegateFactory;
    private final UIController delegate;

    @FXML
    private Button createButton;

    public CreateIdentityWizard(Function<Class, ?> delegateFactory) {
        this.delegateFactory = delegateFactory;
        this.delegate = (UIController) delegateFactory.apply(UIController.class);
        try (InputStream fxmlStream = this.getClass().getResourceAsStream("/no_pkey.fxml")) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.load(fxmlStream);
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
        } catch (IOException e) {
            logger.error("Failed to initialize \"CreateIdentity\" view. Error={}", e.getMessage());
        }
    }

    @FXML
    private void createNewKeyPair() {
        logger.debug("create button pressed");
    }
}
