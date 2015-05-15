package eu.heronnet.module.storage.model;

import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * @author edoardocausarano
 */
@Entity
public class StoredDocument {

    @PrimaryKey
    private String hash;

    private byte[] binaryData;

    @SecondaryKey(relate = Relationship.MANY_TO_MANY, relatedEntity = StoredField.class)
    private Set<String> meta;

    @SuppressWarnings(value = "unused")
    public StoredDocument() {
        // used by BerkeleyDB
    }

    public StoredDocument(String hash, byte[] binaryData, Set<String> meta) {
        this.hash = hash;
        this.binaryData = binaryData;
        this.meta = meta;
    }

    public String getHash() {
        return hash;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public Set<String> getMeta() {
        return meta;
    }

}
