package eu.heronnet.module.gui.model.metadata;

import eu.heronnet.module.gui.model.FieldRow;
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
    public List<FieldRow> process(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            PDDocumentInformation documentInformation = document.getDocumentInformation();
            List<FieldRow> fields = new ArrayList<>();
            fields.add(new FieldRow("title", documentInformation.getTitle()));
            fields.add(new FieldRow("author", documentInformation.getAuthor()));
            fields.add(new FieldRow("year", Integer.toString(documentInformation.getCreationDate().get(Calendar.YEAR))));
            return fields;
        }
        catch (IOException e) {
            logger.error("Error processing document path={}", file.getPath());
            throw new RuntimeException(e);
        }
    }
}
