package eu.heronnet.module.storage.rdf.model;

/**
 * Created by edo on 07/08/15.
 */
public class RawStatement {

    private final byte[] predicateId;

    private final byte[] objectId;

    public RawStatement(byte[] predicateId, byte[] objectId) {
        this.predicateId = predicateId;
        this.objectId = objectId;
    }

    public byte[] getPredicateId() {
        return predicateId;
    }

    public byte[] getObjectId() {
        return objectId;
    }
}
