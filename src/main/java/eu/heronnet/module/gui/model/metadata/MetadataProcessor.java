package eu.heronnet.module.gui.model.metadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eu.heronnet.model.Statement;

/**
 * @author edoardocausarano
 */
public interface MetadataProcessor {
    List<Statement> process(File file) throws IOException;
}
