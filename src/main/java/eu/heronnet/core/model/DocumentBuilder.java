package eu.heronnet.core.model;

import static java.security.MessageDigest.getInstance;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edoardocausarano
 */
public class DocumentBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DocumentBuilder.class);
    private final Set<Field> fields = new HashSet<>();
    private String hash;
    private byte[] binaryData;

    private DocumentBuilder() {
    }

    public static DocumentBuilder newInstance() {
        return new DocumentBuilder();
    }

    public DocumentBuilder withBinary(byte[] binaryData) {
        this.binaryData = binaryData;
        return this;
    }

    public DocumentBuilder withField(String key, String value) {
        fields.add(new Field(key, value));
        return this;
    }

    public DocumentBuilder withField(Field field) {
        fields.add(field);
        return this;
    }

    public DocumentBuilder withHash(String hash) {
        this.hash = hash;
        return this;

    }

    public Document build() {
        if ((hash == null && binaryData == null) || (hash != null && binaryData != null)) {
            throw new IllegalStateException("Cannot build Document, either hash or binary must be set (XOR)");
        }

        if (fields.isEmpty()) {
            throw new IllegalStateException("Cannot create document with no metadata fields");
        }

        if (hash == null) {
            MessageDigest digest;
            try {
                digest = getInstance("SHA-256");
                digest.reset();
                hash = Base64.getEncoder().encodeToString(digest.digest(binaryData));
            }
            catch (NoSuchAlgorithmException e) {
                logger.error("Unable to generate hash for document");
            }
        }

        return new Document(hash, binaryData, fields);
    }

}
