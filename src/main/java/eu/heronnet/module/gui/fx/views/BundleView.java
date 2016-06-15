package eu.heronnet.module.gui.fx.views;

import eu.heronnet.model.Bundle;
import eu.heronnet.module.gui.fx.controller.UIController;
import eu.heronnet.module.gui.model.DocumentListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

/**
 * @author edoardocausarano
 */
public class BundleView extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(BundleView.class);

    private final Function<Class, ?> delegateFactory;

    private UIController delegate;

    @FXML
    private ListView<Bundle> listView;
    @FXML
    private TextField searchField;

    public BundleView(Function<Class, ?> delegateFactory) {
        this.delegateFactory = delegateFactory;
        try (InputStream fxmlStream = this.getClass().getResourceAsStream("/BundleView.fxml")) {
            this.delegate = (UIController) delegateFactory.apply(UIController.class);
            this.delegate.setBundleView(this);
            final FXMLLoader loader = new FXMLLoader();
            loader.setRoot(this);
            loader.setController(this);
            loader.load(fxmlStream);

            listView.setCellFactory(param -> new DocumentListCell());
            } catch (IOException e) {
            logger.error("Failed to initialize \"BundleView\" view. Error={}", e.getMessage());
        }
    }



    @FXML
    public void updateSearchTerms(KeyEvent event) {
        final String terms = searchField.getText();
        if (terms.length() > 2) {
            logger.debug("Typed: {}", terms);
            delegate.localSearch(terms);
        }
    }

    public void update(List<Bundle> items) {
        listView.getItems().addAll(items);
    }

    public void add(Bundle item) {
        listView.getItems().add(item);
    }

    public void set(List<Bundle> items) {
        listView.getItems().setAll(items);
    }
}
