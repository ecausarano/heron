package eu.heronnet.module.gui.fx.views;

import eu.heronnet.model.BundleBuilder;
import eu.heronnet.model.IRIBuilder;
import eu.heronnet.model.Statement;
import eu.heronnet.model.StringNodeBuilder;
import eu.heronnet.module.bus.command.Put;
import eu.heronnet.module.gui.fx.controller.DelegateAware;
import eu.heronnet.module.gui.fx.controller.UIController;
import eu.heronnet.module.gui.model.FieldRow;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author edoardocausarano
 */
public class FileUploadView extends VBox implements DelegateAware<UIController> {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadView.class);
    private final Function<Class, ?> delegateFactory;

    @FXML
    private Button chooseBtn;
    @FXML
    private Label filePathLabel;
    private FileChooser fileChooser;
    @FXML
    private Button confirmBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private TableView<FieldRow> metaTableView;
    @FXML
    private TableColumn<FieldRow, String> nameColumn;
    @FXML
    private TableColumn<FieldRow, String> valueColumn;
    @FXML
    private TextField newKey;
    @FXML
    private TextField newValue;
    @FXML
    private Button addMetaButton;

    private Path path;

    private UIController delegate;

    public FileUploadView(Function<Class, ?> delegateFactory) {
        try (InputStream fxmlStream = this.getClass().getResourceAsStream("/FileUpload.fxml")) {
            this.delegateFactory = delegateFactory;
            this.delegate = (UIController) delegateFactory.apply(UIController.class);
            delegate.setFileUploadView(this);

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load(fxmlStream);

            fileChooser = new FileChooser();
            fileChooser.setTitle("Choose File");

            metaTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
            nameColumn.setOnEditCommit(this::onFieldEdit);
            valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
            valueColumn.setOnEditCommit(this::onFieldEdit);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void onFieldEdit(TableColumn.CellEditEvent<FieldRow, String> event) {
        String newValue = event.getNewValue();

        ObservableList<FieldRow> items = event.getTableView().getItems();
        TablePosition<FieldRow, String> tablePosition = event.getTablePosition();

        FieldRow fieldRow = items.get(tablePosition.getRow());
        fieldRow.valueProperty().set(newValue);
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
    private void chooseFile(ActionEvent event) throws Exception {
        logger.debug("called file chooser");
        File file = fileChooser.showOpenDialog(this.getScene().getWindow());
        if (file == null)
            return;
        filePathLabel.setText(file.getAbsolutePath());
        path = Paths.get(file.toURI());
        delegate.addFile(file);
    }

    @FXML
    private void addMetaItem(ActionEvent event) {
        logger.debug("Adding metadata item: {}={}", newKey.getText(), newValue.getText());
        FieldRow field;
        try {
            field = new FieldRow(newKey.getText(), newValue.getText());
            metaTableView.getItems().add(field);
        } catch (Exception e) {
            logger.error("Error creating field with key={} value={}", newKey.getText(), newValue.getText());
        }
    }

    @FXML
    private void confirm(ActionEvent event) throws IOException, NoSuchAlgorithmException {
        logger.debug("confirming file publish");

        BundleBuilder builder = new BundleBuilder();
        metaTableView.getItems().forEach(fieldRow -> builder.withStatement(new Statement(
                IRIBuilder.withString(fieldRow.nameProperty().getValue()),
                StringNodeBuilder.withString(fieldRow.valueProperty().getValue()))));

        Put put = new Put(builder, path);
        delegate.putFile(put);

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage window = (Stage) this.getScene().getWindow();
        window.close();

    }

    public ObservableList<FieldRow> getFieldRows() {
        return metaTableView.getItems();
    }

    public void setFields(List<Statement> statements) {
        final List<FieldRow> fieldRows = statements.stream().map(statement -> new FieldRow(
                statement.getPredicate().toString(),
                statement.getObject().toString())).collect(Collectors.toList());
        metaTableView.getItems().setAll(fieldRows);

    }
}
