package eu.heronnet.module.gui.fx.task;

import eu.heronnet.model.BinaryDataNode;
import eu.heronnet.model.BundleBuilder;
import eu.heronnet.model.IdentifierNode;
import eu.heronnet.model.Statement;
import eu.heronnet.model.vocabulary.HRN;
import eu.heronnet.module.storage.Persistence;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.List;

/**
 * @author edoardocausarano
 */
public class PutFileService extends Service<Void> {

    private static final Logger logger = LoggerFactory.getLogger(PutFileService.class);

    @Inject
    @Qualifier(value = "distributedStorage")
    private Persistence persistence;

    private List<Statement> statements;

    private Path path;

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (path == null || statements == null || statements.size() == 0) {
                    throw new RuntimeException("invalid service configuration");
                }

                BundleBuilder bundleBuilder = new BundleBuilder();
                bundleBuilder.addAllStatements(statements);
                try {
                    try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
                        final byte[] buffer = new byte[4096];
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        while (inputStream.read(buffer) != -1) {
                            digest.update(buffer);
                        }
                        final byte[] fileHash = digest.digest();
                        bundleBuilder.withSubject(new IdentifierNode(fileHash));
                        bundleBuilder.withStatement(new Statement(
                                HRN.BINARY,
                                // TODO - introduce a type that doesn't contain the bytes but a reference to the file to be streamed
                                new BinaryDataNode(fileHash, Files.readAllBytes(path))));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                persistence.put(bundleBuilder.build());

                if (logger.isDebugEnabled()) {
                    logger.debug("put file path={}", path);
                }

                return null;
            }
        };
    }
}
