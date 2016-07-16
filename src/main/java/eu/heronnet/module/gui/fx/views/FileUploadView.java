package eu.heronnet.module.gui.fx.views;

import eu.heronnet.model.IRI;
import eu.heronnet.model.IRIBuilder;
import eu.heronnet.model.Statement;
import eu.heronnet.model.StringNodeBuilder;
import eu.heronnet.module.gui.fx.controller.DelegateAware;
import eu.heronnet.module.gui.fx.controller.UIController;
import eu.heronnet.module.gui.model.FieldRow;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
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
    private TableColumn<FieldRow, IRI> nameColumn;
    @FXML
    private TableColumn<FieldRow, eu.heronnet.model.Node> valueColumn;
    @FXML
    private TextField newKey;
    @FXML
    private TextField newValue;
    @FXML
    private Button addMetaButton;

    private Path path;
    private List<Statement> statementList;

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

            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            // TODO - provide a list of IRIs
            nameColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
            nameColumn.setOnEditCommit(t -> {
                IRI newName = t.getNewValue();

                ObservableList<FieldRow> items = t.getTableView().getItems();
                TablePosition<FieldRow, IRI> tablePosition = t.getTablePosition();

                FieldRow fieldRow = items.get(tablePosition.getRow());
                fieldRow.nameProperty().set(newName);
            });

            valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

            valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<eu.heronnet.model.Node>() {
                @Override
                public String toString(eu.heronnet.model.Node object) {
                    return object.getData().toString();
                }

                @Override
                public eu.heronnet.model.Node fromString(String string) {
                    return StringNodeBuilder.withString(string);
                }
            }));
            valueColumn.setOnEditCommit(t -> {
                eu.heronnet.model.Node newValue = t.getNewValue();

                ObservableList<FieldRow> items = t.getTableView().getItems();
                TablePosition<FieldRow, eu.heronnet.model.Node> tablePosition = t.getTablePosition();

                FieldRow fieldRow = items.get(tablePosition.getRow());
                fieldRow.valueProperty().set(newValue);
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
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
        try {
            Statement statement = new Statement(IRIBuilder.withString(newKey.getText()), StringNodeBuilder.withString(newValue.getText()));
            metaTableView.getItems().add(new FieldRow(statement));
        } catch (Exception e) {
            logger.error("Error creating field with key={} value={}", newKey.getText(), newValue.getText());
        }
    }

    @FXML
    private void confirm(ActionEvent event) throws IOException, NoSuchAlgorithmException {
        logger.debug("confirming file publish");

        delegate.putFile(statementList, path);

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
        statementList = statements;
        ObservableList<FieldRow> items = metaTableView.getItems();
        statements.forEach(statement -> items.add(new FieldRow(statement)));
    }
}
