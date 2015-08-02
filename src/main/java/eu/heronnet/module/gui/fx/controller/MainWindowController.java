package eu.heronnet.module.gui.fx.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import eu.heronnet.core.model.Bundle;
import eu.heronnet.module.bus.command.Find;
import eu.heronnet.module.bus.command.UpdateLocalResults;
import eu.heronnet.module.bus.command.UpdateResults;
import eu.heronnet.module.gui.model.DocumentListCell;

/**
 * @author edoardocausarano
 */

public class MainWindowController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MainWindowController.class);

    @Inject
    private EventBus eventBus;

    @Inject
    private Callback controllerFactory;

    @FXML
    private MenuBar menuBar;
    @FXML
    private ListView<Bundle> localStorageListView;
    @FXML
    private ListView<Bundle> resultList;
    @FXML
    private TextField searchField;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("Initializing UI controller");

        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }

        resultList.setCellFactory(param -> new DocumentListCell());
        localStorageListView.setCellFactory(param -> new DocumentListCell());

        eventBus.register(this);
        eventBus.post(new Find(Bundle.emptyBundle(), true));
    }

    @Subscribe
    public void updateSearchResults(UpdateResults results) {
        Platform.runLater(() -> {
            ObservableList<Bundle> items = resultList.getItems();
            items.clear();
            items.addAll(results.getBundles());
        });
    }

    @Subscribe
    public void updateLocalStorageView(UpdateLocalResults updateResults) {
        Platform.runLater(() -> {
            ObservableList<Bundle> items = localStorageListView.getItems();
            items.addAll(updateResults.getBundles());
        });
    }
}
