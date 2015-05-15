package eu.heronnet.module.storage.model.converter;

import eu.heronnet.core.model.Field;
import eu.heronnet.module.storage.model.StoredField;

/**
 * @author edoardocausarano
 */
public class FieldConverter {

    public static StoredField asStored(Field field) {
        return new StoredField(field.getHash(), field.getName(), field.getValue());
    }

    public static Field asField(StoredField storedField) {
        return new Field(storedField.getHash(), storedField.getName(), storedField.getValue());
    }

}
