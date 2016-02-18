package eu.heronnet.module.gui.fx.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eu.heronnet.model.Bundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edoardocausarano
 */
public class BundleView extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(BundleView.class);

    @Inject
    FXMLLoader fxmlLoader;

    @FXML
    ListView<Bundle> itemListView;

    @PostConstruct
    public void postConstruct() {
        try (InputStream fxmlStream = this.getClass().getResourceAsStream("/LocalStorageView.fxml")) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.load(fxmlStream);
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
        } catch (IOException e) {
            logger.error("Failed to initialize \"LocalStorageView\" view. Error={}", e.getMessage());
        }
    }

    public void update(List<Bundle> items) {
        itemListView.getItems().addAll(items);
    }

    public void set(List<Bundle> items) {
        itemListView.getItems().setAll(items);
    }
}
