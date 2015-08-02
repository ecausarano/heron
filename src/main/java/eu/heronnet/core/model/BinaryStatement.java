package eu.heronnet.core.model;

/**
 * @author edoardocausarano
 */
public class BinaryStatement extends Statement<Byte[]> {

    private byte[] binary;

    public BinaryStatement(String predicate, byte[] bytes) {
        super(predicate, null);
        this.binary = bytes;
    }

    @Override
    public Byte[] getObject() {
        throw new UnsupportedOperationException("binary statements should not be accessed from getObject");
    }

    public byte[] getBinary() {
        return binary;
    }
}
