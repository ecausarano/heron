package eu.heronnet.module.gui.fx.task;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import eu.heronnet.model.Statement;
import eu.heronnet.module.gui.model.metadata.FieldProcessorFactory;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edoardocausarano
 */
public class ExtractDocumentMetadataService extends Service<List<Statement>> {

    private static final Logger logger = LoggerFactory.getLogger(ExtractDocumentMetadataService.class);
    @Inject
    private FieldProcessorFactory processorFactory;

    private File file;

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    protected Task<List<Statement>> createTask() {
        final Task<List<Statement>> task = new Task<List<Statement>>() {
            @Override
            protected List<Statement> call() throws Exception {
                String contentType = Files.probeContentType(Paths.get(file.toURI()));
                return processorFactory.getProcessor(contentType).process(file);
            }
        };
        task.setOnFailed(event -> {
            final Throwable exception = task.getException();
            logger.error("An error occurred while processing file \"{}\": {}", file, exception.getMessage());
            throw new RuntimeException(exception);
        });
        return task;
    }
}
