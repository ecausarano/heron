package eu.heronnet.module.gui.fx.views;

import java.util.function.Function;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edoardocausarano
 */
public class IdentityDetailsView extends GridPane {

    private static final Logger logger = LoggerFactory.getLogger(IdentityDetailsView.class);
    private final Function<Class, ?> delegateFactory;


    @FXML
    private Label userIdLabel;
    @FXML
    private Label fingerprintLabel;
    @FXML
    private Button advancedDetailsButton;
    @FXML
    private ImageView idImage;

//    @Inject
//    private PGPUtils pgpUtils;

    public IdentityDetailsView(Function<Class, ?> delegateFactory) {
        this.delegateFactory = delegateFactory;

    }
    public void postConstruct() {
//        if (pgpUtils.hasPrivateKey()) {
//            try (InputStream fxmlStream = this.getClass().getResourceAsStream("/identityDetails.fxml")) {
//                fxmlLoader.setController(this);
//                fxmlLoader.setRoot(this);
//                fxmlLoader.load(fxmlStream);
//            } catch (IOException e) {
//                logger.error("Failed to initialize \"Identity Details\" view. Error={}", e.getMessage());
//            }
//            try {
//                final PGPPublicKey publicKey = pgpUtils.getPublicKey();
//                setUserIdLabel(publicKey.getUserIDs().next().toString());
//                setFingerprintLabel(HexUtil.bytesToHex(publicKey.getFingerprint()));
//            } catch (Exception e) {
//                logger.error("Error fetching private key");
//            }
//
//        } else {
//             logger.debug("no PGP key, requesting wizard to start");
//        }
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
