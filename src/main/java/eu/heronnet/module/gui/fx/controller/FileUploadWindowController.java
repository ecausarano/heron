package eu.heronnet.module.gui.fx.controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import com.google.common.eventbus.EventBus;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.Statement;
import eu.heronnet.model.builder.BundleBuilder;
import eu.heronnet.model.builder.IRIBuilder;
import eu.heronnet.model.builder.StringNodeBuilder;
import eu.heronnet.module.bus.command.Put;
import eu.heronnet.module.gui.model.FieldRow;
import eu.heronnet.module.gui.model.metadata.FieldProcessorFactory;
import eu.heronnet.module.pgp.PGPUtils;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Upload window
 *
 * @author edoardocausarano
 */
public class FileUploadWindowController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadWindowController.class);

    @Inject
    private EventBus eventBus;
    @Inject
    private Executor executor;
    @Inject
    FXMLLoader fxmlLoader;
    @Inject
    private FieldProcessorFactory processorFactory;

    @FXML
    private Button chooseBtn;
    @FXML
    private Label filePathLabel;
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

    @FXML
    private void chooseFile(ActionEvent event) throws Exception {
        logger.debug("called file chooser");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        File file = fileChooser.showOpenDialog(chooseBtn.getScene().getWindow());
        if (file == null)
            return;

        path = Paths.get(file.toURI());
        logger.debug("Chose file={}", file.getAbsolutePath());
        filePathLabel.setText(file.getName());

        String contentType = Files.probeContentType(path);
        Task<List<FieldRow>> extractMetadataTask = new Task<List<FieldRow>>() {
            @Override
            protected List<FieldRow> call() throws Exception {
                List<Statement> statements = processorFactory.getProcessor(contentType).process(file);
                return statements.stream().map(
                        statement -> new FieldRow(
                                statement.getPredicate().toString(),
                                statement.getObject().toString())).collect(Collectors.toList());
            }
        };
        extractMetadataTask.setOnSucceeded(workerStateEvent -> {
            ObservableList<FieldRow> metaItems = metaTableView.getItems();
            metaItems.addAll(extractMetadataTask.getValue());
        });
        executor.execute(extractMetadataTask);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        nameColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        nameColumn.setOnEditCommit(t -> {
            String newName = t.getNewValue();

            ObservableList<FieldRow> items = t.getTableView().getItems();
            TablePosition<FieldRow, String> tablePosition = t.getTablePosition();

            FieldRow fieldRow = items.get(tablePosition.getRow());
            fieldRow.setName(newName);
        });

        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit(t -> {
            String newValue = t.getNewValue();

            ObservableList<FieldRow> items = t.getTableView().getItems();
            TablePosition<FieldRow, String> tablePosition = t.getTablePosition();

            FieldRow fieldRow = items.get(tablePosition.getRow());
            fieldRow.setValue(newValue);
        });

    }

    @FXML
    private void addMetaItem(ActionEvent event) {
        logger.debug("Adding metadata item: {}={}", newKey.getText(), newValue.getText());
        FieldRow field;
        try {
            field = new FieldRow(newKey.getText(), newValue.getText());
            metaTableView.getItems().addAll(field);
        }
        catch (Exception e) {
            logger.error("Error creating field with key={} value={}", newKey.getText(), newValue.getText());
        }
    }

    @FXML
    private void confirm(ActionEvent event) throws IOException, NoSuchAlgorithmException {
        logger.debug("confirming file publish");

        BundleBuilder builder = new BundleBuilder();
        metaTableView.getItems().forEach(fieldRow -> builder.withStatement(new Statement(
                    IRIBuilder.withString(fieldRow.getName()),
                    StringNodeBuilder.withString(fieldRow.getValue()))));

        Put put = new Put(builder, path);
        eventBus.post(put);

//        Task<Statement> signBundleTask = new Task<Statement>() {
//            @Override
//            protected Statement call() throws Exception {
//                items.forEach(fieldRow -> builder.withStatement(new Statement(
//                        IRIBuilder.withString(fieldRow.getName()),
//                        StringNodeBuilder.withString(fieldRow.getValue()))));
//                Bundle bundle = builder.build();
//                return pgpUtils.createSignature(bundle, "password".toCharArray());
//            }
//        };

//        signBundleTask.setOnSucceeded(workerStateEvent ->{
//            builder.withStatement(signBundleTask.getValue()) ;
//            Put put = new Put(builder, path);
//            eventBus.post(put);
//        });
//        executor.execute(signBundleTask);

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage window = (Stage) cancelBtn.getScene().getWindow();
        window.close();
    }
}
