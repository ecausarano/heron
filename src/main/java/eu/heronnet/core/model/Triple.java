package eu.heronnet.core.model;

import eu.heronnet.module.storage.util.HexUtil;

/**
 * @author edoardocausarano
 */
public class Triple<T> {

    private byte[] subject;

    private Statement<T> statement;

    public Triple(byte[] subject, Statement<T> statement) throws IllegalStateException {
        if (subject == null || statement == null || statement.getPredicate() == null || statement.getPredicate().isEmpty() || statement.getObject() == null) {
            throw new IllegalArgumentException("invalid statement or missing subject");
        }
        this.subject = subject;
        this.statement = statement;
    }

    public byte[] getSubject() {
        return subject;
    }

    public Statement<T> getStatement() {
        return statement;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Triple{");
        sb.append("subject=");
        if (subject == null) sb.append("null");
        else {
            sb.append('[').append(HexUtil.bytesToHex(subject)).append(']');
        }
        sb.append(", statement=").append(statement);
        sb.append('}');
        return sb.toString();
    }
}
