package eu.heronnet.core.model;

import java.util.Set;

/**
 * @author edoardocausarano
 */
public class Document {

    private String hash;
    private byte[] binaryData;
    private Set<Field> meta;

    public Document() {
    }

    public Document(String hash, byte[] binaryData, Set<Field> meta) {
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

    public Set<Field> getMeta() {
        return meta;
    }

}
