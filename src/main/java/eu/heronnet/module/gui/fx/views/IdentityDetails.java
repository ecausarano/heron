package eu.heronnet.module.gui.fx.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author edoardocausarano
 */
public class IdentityDetails extends GridPane {

    private static final Logger logger = LoggerFactory.getLogger(IdentityDetails.class);

    @FXML
    private Label userIdLabel;
    @FXML
    private Label fingerprintLabel;
    @FXML
    private Button advancedDetailsButton;

    public IdentityDetails() {
        try (InputStream fxmlStream = this.getClass().getResourceAsStream("/identityDetails.fxml")) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load(fxmlStream);
        } catch (IOException e) {
            logger.error("Failed to initialize \"Identity Details\" view. Error={}", e.getMessage());
        }
    }

    public void setUserIdLabel(String userIdLabel) {
        this.userIdLabel.setText(userIdLabel);
    }

    public void setFingerprintLabel(String fingerprintLabel) {
        this.fingerprintLabel.setText(fingerprintLabel);
    }

    @FXML
    private void viewAdvancedDetailsAction() {
        logger.debug("\"Advanced\" button pressed");
    }

}
