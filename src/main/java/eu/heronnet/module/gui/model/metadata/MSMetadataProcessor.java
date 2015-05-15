package eu.heronnet.module.gui.model.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.heronnet.module.gui.model.FieldRow;

/**
 * @author edoardocausarano
 */
@FieldProcessorStrategy(mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
public class MSMetadataProcessor implements MetadataProcessor {

    Logger logger = LoggerFactory.getLogger(MSMetadataProcessor.class);

    @Override
    public List<FieldRow> process(File file) {
        try (OPCPackage opcPackage = OPCPackage.open(file)) {
            List<FieldRow> fields = new ArrayList<>();

            PackageProperties properties = opcPackage.getPackageProperties();
            fields.add(new FieldRow("title", properties.getTitleProperty().getValue()));
            fields.add(new FieldRow("author", properties.getCreatorProperty().getValue()));

            return fields;
        }
        catch (InvalidFormatException e) {
            logger.debug("Invalid OOXML file path={}", file.getPath());
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            logger.error("Error processing document path={}", file.getPath());
            throw new RuntimeException(e);
        }
    }
}