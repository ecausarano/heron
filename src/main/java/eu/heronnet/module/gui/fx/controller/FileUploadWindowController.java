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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import eu.heronnet.core.model.DocumentBuilder;
import eu.heronnet.core.model.Field;
import eu.heronnet.module.bus.command.Put;
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
    private TableView<Field> metaTableView;
    @FXML
    private TableColumn<Field, String> nameColumn;
    @FXML
    private TableColumn<Field, String> valueColumn;
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

        path = Paths.get(file.toURI());
        logger.debug("Chose file={}", file.getAbsolutePath());
        filePathLabel.setText(file.getName());

        String contentType = Files.probeContentType(path);

        // TODO - replace with Spring strategy factory
        ObservableList<Field> metaItems = metaTableView.getItems();
        List<Field> fields = processorFactory.getProcessor(contentType).process(file);
        metaItems.addAll(fields);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    @FXML
    private void addMetaItem(ActionEvent event) {
        logger.debug("Adding metadata item: {}={}", newKey.getText(), newValue.getText());
        Field field = null;
        try {
            field = new Field(newKey.getText(), newValue.getText());
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
        ObservableList<Field> items = metaTableView.getItems();
        items.forEach(documentBuilder::withField);

        byte[] allBytes = Files.readAllBytes(path);
        documentBuilder.withBinary(allBytes);

        Put put = new Put(documentBuilder.build());
        eventBus.post(put);
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
