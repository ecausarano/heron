package eu.heronnet.module.gui.fx.controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.builder.BundleBuilder;
import eu.heronnet.model.builder.StatementBuilder;
import eu.heronnet.module.bus.command.Find;
import eu.heronnet.module.bus.command.Put;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.gui.fx.views.CreateIdentityWizard;
import eu.heronnet.module.gui.fx.views.IdentityDetails;
import eu.heronnet.module.gui.model.DocumentListCell;
import eu.heronnet.module.gui.model.PublicKeyListCell;
import eu.heronnet.module.pgp.PGPUtils;
import eu.heronnet.module.storage.Persistence;
import eu.heronnet.module.storage.util.HexUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

/**
 * @author edoardocausarano
 */
@Component
@Scope("prototype")
public class MainWindowController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MainWindowController.class);
    @Inject
    EventBus eventBus;
    @Inject
    private Callback controllerFactory;
    @Inject
    private Executor executor;
    @Inject
    private Persistence persistence;
    @Inject
    private PGPUtils pgpUtils;

    @FXML
    private MenuBar menuBar;
    @FXML
    private Tab identityTab;
    @FXML
    private Tab localStorageTab;
    @FXML
    private Pane identityPane;
    @FXML
    private ListView<Bundle> localStorageListView;
    @FXML
    private ListView<Bundle> resultList;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<Bundle> knownPublicKeyList;
    @FXML
    private Button addBtn;

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
    public void search(ActionEvent event) {
        logger.debug("searching for {}", searchField.getText());
        eventBus.post(new Find(searchField.getText()));
    }

    @Subscribe
    public void updateSearchResults(UpdateResults results) {
        Platform.runLater(() -> {
            ObservableList<Bundle> items = resultList.getItems();
            items.clear();
            // TODO - merge bundles with same subjectId
            items.addAll(results.getBundles());
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("Initializing UI controller");

        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }

        eventBus.register(this);

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

        resultList.setCellFactory(param -> {
            DocumentListCell documentListCell = new DocumentListCell();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem signMenuItem = new MenuItem();
            try {
                if (!pgpUtils.hasPrivateKey()) {
                    signMenuItem.setDisable(true);
                }
            } catch (Exception e) {
                logger.error("Error fetching public key, disabling signing feature");
                signMenuItem.setDisable(true);
            }

            signMenuItem.textProperty().bind(Bindings.format("Sign \"%s\"", documentListCell.itemProperty()));
            signMenuItem.setOnAction(event -> {
                Bundle item = documentListCell.getItem();
                byte[] bundleId = item.getNodeId();
                logger.debug("Requested to sign bundle=[{}]", HexUtil.bytesToHex(bundleId));
                try {
                    BundleBuilder signatureBuilder = new BundleBuilder()
                            .withSubject(item.getSubject())
                            .withStatement(StatementBuilder.pgpSignature("signature"));
                    Put put = new Put(signatureBuilder);
                    eventBus.post(put);
                } catch (NoSuchAlgorithmException e) {
                    logger.error("Error signing, message={}", e.getMessage());
                }
            });
            MenuItem downloadMenuItem = new MenuItem();
            downloadMenuItem.textProperty().bind(Bindings.format("Download \"%s\"", documentListCell.itemProperty()));
            downloadMenuItem.setOnAction(event -> {
                Bundle item = documentListCell.getItem();
                logger.debug("Requested to download bundle={}", item.getSubject().toString());
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
        localStorageListView.setCellFactory(param -> new DocumentListCell());
        knownPublicKeyList.setCellFactory(param -> new PublicKeyListCell());

        Task<List<Bundle>> listLocalBundles = new Task<List<Bundle>>() {
            @Override
            protected List<Bundle> call() throws Exception {
                List<Bundle> allBundles = persistence.getAll();
                logger.debug("Found {} local bundles", allBundles.size());
                return allBundles;
            }
        };
        listLocalBundles.setOnSucceeded(event -> {
            ObservableList<Bundle> items = localStorageListView.getItems();
            items.addAll(listLocalBundles.getValue());
        });
        executor.execute(listLocalBundles);

        Task<List<Bundle>> listPublicKeysTask = new Task<List<Bundle>>() {
            @Override
            protected List<Bundle> call() throws Exception {
                List<Bundle> bundles = persistence.findByPredicate(Collections.singletonList(PGPUtils.getPublicKeyClass()));
                logger.debug("Found {} public keys", bundles.size());
                return bundles;
            }
        };
        listPublicKeysTask.setOnSucceeded(event -> {
            ObservableList<Bundle> items = knownPublicKeyList.getItems();
            items.addAll(listPublicKeysTask.getValue());
        });
        executor.execute(listPublicKeysTask);
    }

}
