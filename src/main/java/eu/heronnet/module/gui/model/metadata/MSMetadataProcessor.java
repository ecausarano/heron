package eu.heronnet.module.gui.model.metadata;

import eu.heronnet.model.Statement;
import eu.heronnet.model.builder.StatementBuilder;
import eu.heronnet.model.builder.StringNodeBuilder;
import eu.heronnet.model.vocabulary.DC;
import eu.heronnet.module.gui.model.FieldRow;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author edoardocausarano
 */
@FieldProcessorStrategy(mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
public class MSMetadataProcessor implements MetadataProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MSMetadataProcessor.class);

    @Override
    public List<Statement> process(File file) {
        try (OPCPackage opcPackage = OPCPackage.open(file)) {
            List<Statement> fields = new ArrayList<>();

            PackageProperties properties = opcPackage.getPackageProperties();

            fields.add(new Statement(DC.TITLE, StringNodeBuilder.withString(properties.getTitleProperty().getValue())));
            fields.add(new Statement(DC.CREATOR, StringNodeBuilder.withString(properties.getCreatorProperty().getValue())));
            fields.add(new Statement(DC.FORMAT, StringNodeBuilder.withString("application/vnd.openxmlformats-officedocument.wordprocessingml.document")));
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
