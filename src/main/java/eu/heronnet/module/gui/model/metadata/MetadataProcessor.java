package eu.heronnet.module.gui.model.metadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eu.heronnet.module.gui.model.FieldRow;

/**
 * @author edoardocausarano
 */
public interface MetadataProcessor {
    List<FieldRow> process(File file) throws IOException;
}
