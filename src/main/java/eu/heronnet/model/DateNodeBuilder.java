package eu.heronnet.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * @author edoardocausarano
 */
public class DateNodeBuilder {

    private DateNodeBuilder(){}

    /**
     * Builds a {@link DateNode} with the given literal value
     *
     * @param date the value to represent
     * @return a {@link DateNode} for the given value
     * @throws RuntimeException if SHA-256 message digest algorithm is not available on the platform
     */
    public static DateNode withDate(Date date) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new DateNode(digest.digest(date.toString().getBytes()), date);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static DateNode withDate(long date) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final Date dateObject = new Date(date);
            return new DateNode(digest.digest(dateObject.toString().getBytes()), dateObject);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
