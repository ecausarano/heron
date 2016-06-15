package eu.heronnet.module.gui.model.metadata;

import eu.heronnet.model.DateNodeBuilder;
import eu.heronnet.model.Statement;
import eu.heronnet.model.StringNodeBuilder;
import eu.heronnet.model.vocabulary.DC;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

            final String title = documentInformation.getTitle();
            if (title != null) {
                fields.add(new Statement(DC.TITLE, StringNodeBuilder.withString(title)));
            }

            final String author = documentInformation.getAuthor();
            if (author != null) {
                fields.add(new Statement(DC.CREATOR, StringNodeBuilder.withString(author)));
            }

            final Calendar creationDate = documentInformation.getCreationDate();
            if (creationDate != null) {
                fields.add(new Statement(DC.DATE, DateNodeBuilder.withDate(creationDate.get(Calendar.YEAR))));
            }

            fields.add(new Statement(DC.FORMAT, StringNodeBuilder.withString("application/pdf")));
            return fields;
        }
        catch (IOException e) {
            logger.error("Error processing document path={}", file.getPath());
            throw new RuntimeException(e);
        }
    }
}
