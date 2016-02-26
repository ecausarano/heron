package eu.heronnet.module.gui.fx.views;

import java.io.InputStream;
import java.util.function.Function;

import eu.heronnet.module.gui.fx.controller.DelegateAware;
import eu.heronnet.module.gui.fx.controller.UIController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edoardocausarano
 */
public class FileUploadView extends VBox implements DelegateAware<UIController> {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadView.class);
    private final Function<Class, ?> delegateFactory;

    private UIController delegate;

    public FileUploadView(Function<Class, ?> delegateFactory) {
        try (InputStream fxmlStream = this.getClass().getResourceAsStream("/FileUpload.fxml")) {
            this.delegate = (UIController) delegateFactory.apply(UIController.class);
            this.delegateFactory = delegateFactory;

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            Parent loader = fxmlLoader.load(fxmlStream);
            stage.setScene(new Scene(loader));
            stage.setTitle("Add file");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            logger.error(e.getCause().getMessage());
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
}
