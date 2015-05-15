package eu.heronnet.module.storage.model.converter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import eu.heronnet.core.model.Document;
import eu.heronnet.core.model.Field;
import eu.heronnet.module.storage.model.StoredDocument;

/**
 * @author edoardocausarano
 */
public class DocumentConverter {

    public static StoredDocument asStoredDocument(Document document) {
        Set<String> metaSetHashes = new HashSet<>(document.getMeta().size());
        Set<Field> meta = document.getMeta();
        metaSetHashes.addAll(meta.stream().map(Field::getHash).collect(Collectors.toSet()));

        return new StoredDocument(document.getHash(), document.getBinaryData(), metaSetHashes);
    }

}
