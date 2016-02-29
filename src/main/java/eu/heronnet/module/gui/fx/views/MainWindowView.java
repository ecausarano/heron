package eu.heronnet.module.gui.fx.views;


import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

import eu.heronnet.model.Bundle;
import eu.heronnet.module.gui.fx.controller.DelegateAware;
import eu.heronnet.module.gui.fx.controller.UIController;
import eu.heronnet.module.gui.model.DocumentListCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edoardocausarano
 */
public class MainWindowView extends VBox implements DelegateAware<UIController> {

    private static final Logger logger = LoggerFactory.getLogger(MainWindowView.class);
    private final Function<Class, ?> delegateFactory;

    private UIController delegate;

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

    public MainWindowView(Function<Class, ?> delegateFactory) throws RuntimeException {
        try (InputStream fxmlStream = MainWindowView.class.getResourceAsStream("/HeronMainWindow.fxml")) {
            this.delegateFactory = delegateFactory;
            delegate = (UIController) delegateFactory.apply(UIController.class);
            delegate.setMainWindowView(this);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load(fxmlStream);

            resultList.setCellFactory(param -> new DocumentListCell());

        } catch (Exception e) {
            logger.error("Error in MainWindowView ctor: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public UIController getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(UIController delegate) {
        this.delegate = delegate;
    }

    @FXML
    private void addFile(ActionEvent event) {
        final FileUploadView fileUploadView = new FileUploadView(delegateFactory);
        fileUploadView.showAndWait();
    }

    @FXML
    private void search(ActionEvent event) {
        delegate.distributedSearch(searchField.getText());
    }

    @FXML
    public void openLocalStoreView() {
        logger.debug("called openLocalStoreView");
        final Stage stage = new Stage();
        stage.setTitle("Local Storage");
        stage.initModality(Modality.NONE);
        stage.initOwner(addBtn.getScene().getWindow());
        final VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setMaxWidth(Double.MAX_VALUE);
        vBox.getChildren().add(new BundleView(delegateFactory));

        final Scene scene = new Scene(vBox);

        stage.setScene(scene);

        delegate.localSearch("");
        stage.show();
    }

    @FXML
    public void revealIdPopover() {
        if (idPopover == null) {
            final IdentityDetailsView identityDetailsView = new IdentityDetailsView(delegateFactory);
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
    }

    public void setResultView(List<Bundle> resultView) {
        resultList.getItems().addAll(resultView);
    }
}
