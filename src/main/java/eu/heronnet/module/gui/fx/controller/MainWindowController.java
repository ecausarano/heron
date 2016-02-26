package eu.heronnet.module.gui.fx.controller;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

import eu.heronnet.model.Bundle;
import eu.heronnet.module.gui.fx.views.IdentityDetailsView;
import eu.heronnet.module.gui.model.DocumentListCell;
import eu.heronnet.module.storage.util.HexUtil;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Is responsible of the coordination of all events deriving from user interaction,
 * dispatching service requests and updating model objects and synchronize them with
 * views
 *
 * @author edoardocausarano
 */
public class MainWindowController {

    private static final Logger logger = LoggerFactory.getLogger(MainWindowController.class);

    @Inject
    private UIController uiController;
    @Inject
    private Callback controllerFactory;
    @Inject
    private IdentityDetailsView identityDetailsView;

    @FXML
    private MenuBar menuBar;
    @FXML
    private ListView<Bundle> resultList;
    @FXML
    private TextField searchField;
    @FXML
    private Button addBtn;
    @FXML
    private Button idBtn;
    @FXML
    private PopOver idPopover;

    @FXML
    private void addFile(ActionEvent event) {
        logger.debug("Adding file");

        try (InputStream fxmlStream = this.getClass().getResourceAsStream("/FileUpload.fxml")) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setControllerFactory(controllerFactory);
            Parent loader = fxmlLoader.load(fxmlStream);
            stage.setScene(new Scene(loader));
            stage.setTitle("Add file");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(addBtn.getScene().getWindow());
            stage.showAndWait();

        }
        catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }
    }

    @FXML
    public void revealIdPopover() {
        if (idPopover == null) {
            idPopover = new PopOver(identityDetailsView);
            idPopover.setArrowLocation(PopOver.ArrowLocation.RIGHT_TOP);
            idPopover.setDetachable(true);
        }
        if (idPopover.isShowing()) {
            idPopover.hide();
            idPopover = null;
        } else {
            idPopover.show(idBtn);
        }
    }


    @FXML
    public void initialize() {
        logger.debug("Initializing UI controller");

        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }


/*
        try {
            PGPPublicKey publicKey = pgpUtils.getPublicKey();
            if (publicKey == null) {
                CreateIdentityWizard identityWizard = new CreateIdentityWizard();
                identityPane.getChildren().add(identityWizard);
            } else {
                IdentityDetails details = new IdentityDetails();
                identityPane.getChildren().add(details);
                details.setUserIdLabel(publicKey.getUserIDs().next().toString());
                details.setFingerprintLabel(HexUtil.bytesToHex(publicKey.getFingerprint()));
            }
        } catch (Exception e) {
            logger.error("Error", e.getCause());
        }
*/

        resultList.setCellFactory(param -> {
            DocumentListCell documentListCell = new DocumentListCell();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem signMenuItem = new MenuItem();

            if (uiController.isUserSigningEnabled())  {
                    signMenuItem.setDisable(false);
            }
            signMenuItem.textProperty().bind(Bindings.format("Sign \"%s\"", documentListCell.itemProperty()));
            signMenuItem.setOnAction(event -> {
                Bundle item = documentListCell.getItem();
                logger.debug("Requested to sign bundle=[{}]", HexUtil.bytesToHex(item.getNodeId()));
                uiController.signBundle(item);
            });

            MenuItem downloadMenuItem = new MenuItem();
            downloadMenuItem.textProperty().bind(Bindings.format("Download \"%s\"", documentListCell.itemProperty()));
            downloadMenuItem.setOnAction(event -> {
                Bundle item = documentListCell.getItem();
                logger.debug("Requested to download bundle={}", item.getSubject().toString());
                uiController.downloadBundle(item);
            });
            contextMenu.getItems().addAll(signMenuItem, downloadMenuItem);

            documentListCell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    documentListCell.setContextMenu(null);
                } else {
                    documentListCell.setContextMenu(contextMenu);
                }
            });
            return documentListCell;
        });

    }

    ObservableList<Bundle> getResultItems() {
         return resultList.getItems();
    }
}
