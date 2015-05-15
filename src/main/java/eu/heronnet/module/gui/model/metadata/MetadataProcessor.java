package eu.heronnet.module.gui.model.metadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eu.heronnet.core.model.Field;

/**
 * @author edoardocausarano
 */
public interface MetadataProcessor {
    List<Field> process(File file) throws IOException;
}
