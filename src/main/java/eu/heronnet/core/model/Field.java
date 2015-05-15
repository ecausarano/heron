package eu.heronnet.core.model;

import static java.security.MessageDigest.getInstance;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author edoardocausarano
 */
public class Field {

    private String hash;

    private String name;

    private String value;

    public Field() {
    }

    public Field(String name, String value) throws IllegalStateException {
        this.name = name;
        this.value = value;

        if (name == null || name.isEmpty() || value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Field name or value cannot be null or empty");
        }

        try {
            byte[] nameBytes = name.getBytes("utf-8");
            byte[] valueBytes = value.getBytes("utf-8");
            ByteBuffer byteBuffer = ByteBuffer.allocate(nameBytes.length + valueBytes.length);
            byteBuffer.put(nameBytes).put(valueBytes);
            MessageDigest digest = getInstance("SHA-256");
            digest.reset();
            this.hash = Base64.getEncoder().encodeToString(digest.digest(byteBuffer.array()));
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available on this platform");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available on this platform.");
        }
    }

    public Field(String hash, String name, String value) {
        this.hash = hash;
        this.name = name;
        this.value = value;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
