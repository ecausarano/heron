package eu.heronnet.module.gui.fx.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import eu.heronnet.core.model.DocumentBuilder;
import eu.heronnet.module.bus.command.Put;
import eu.heronnet.module.gui.model.FieldRow;
import eu.heronnet.module.gui.model.metadata.FieldProcessorFactory;

/**
 * @author edoardocausarano
 */
public class FileUploadWindowController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadWindowController.class);

    @Inject
    private EventBus eventBus;

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

        ObservableList<FieldRow> metaItems = metaTableView.getItems();
        // TODO - move this metadata extractor part to a background worker thread
        List<FieldRow> fields = processorFactory.getProcessor(contentType).process(file);
        metaItems.addAll(fields);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setName(t.getNewValue()));

        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setValue(t.getNewValue()));

    }

    @FXML
    private void addMetaItem(ActionEvent event) {
        logger.debug("Adding metadata item: {}={}", newKey.getText(), newValue.getText());
        FieldRow field = null;
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
        logger.debug("confirming");
        DocumentBuilder documentBuilder = DocumentBuilder.newInstance();
        ObservableList<FieldRow> items = metaTableView.getItems();
        for (FieldRow item : items) {
            documentBuilder.withField(item.getName(), item.getValue());
        }

        byte[] allBytes = Files.readAllBytes(path);
        documentBuilder.withBinary(allBytes);

        Put put = new Put(documentBuilder);
        eventBus.post(put);
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
