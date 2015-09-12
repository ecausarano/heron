package eu.heronnet.module.gui.model.metadata;

import eu.heronnet.model.Statement;
import eu.heronnet.module.gui.model.FieldRow;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author edoardocausarano
 */
public interface MetadataProcessor {
    List<Statement> process(File file) throws IOException;
}
