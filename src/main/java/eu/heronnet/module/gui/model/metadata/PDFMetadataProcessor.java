package eu.heronnet.module.gui.model.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import eu.heronnet.model.Statement;
import eu.heronnet.model.builder.StringNodeBuilder;
import eu.heronnet.model.vocabulary.DC;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edoardocausarano
 */
@FieldProcessorStrategy(mimeType = "application/pdf")
public class PDFMetadataProcessor implements MetadataProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PDFMetadataProcessor.class);

    @Override
    public List<Statement> process(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            PDDocumentInformation documentInformation = document.getDocumentInformation();
            List<Statement> fields = new ArrayList<>();
            fields.add(new Statement(DC.TITLE, StringNodeBuilder.withString(documentInformation.getTitle())));
            fields.add(new Statement(DC.CREATOR, StringNodeBuilder.withString(documentInformation.getAuthor())));
            fields.add(new Statement(DC.DATE, StringNodeBuilder.withString(Integer.toString(documentInformation.getCreationDate().get(Calendar.YEAR)))));
            fields.add(new Statement(DC.FORMAT, StringNodeBuilder.withString("application/pdf")));
            return fields;
        }
        catch (IOException e) {
            logger.error("Error processing document path={}", file.getPath());
            throw new RuntimeException(e);
        }
    }
}
