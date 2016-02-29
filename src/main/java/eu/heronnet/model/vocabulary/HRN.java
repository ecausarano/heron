package eu.heronnet.model.vocabulary;

import eu.heronnet.model.IRI;
import eu.heronnet.model.IRIBuilder;

/**
 * @author edoardocausarano
 */
public class HRN {

    /**
     * Heron elements namespace: http://heronnet.eu/0.1/
     */
    public static final String NAMESPACE = "http://heronnet.eu/0.1/";

    /**
     * Recommend prefix for the Heron elements namespace: "hrn"
     */
    public static final String PREFIX = "hrn";



    /**
     * hrn:binary
     */
    public static final IRI BINARY;

    /**
     * hrn:generic
     */
    public static final IRI GENERIC;

    /**
     * hrn:publickey
     */
    public static final IRI PUBLIC_KEY;

    /**
     * hrn:signature
     */
    public static final IRI SIGNATURE;

    static {
        BINARY = IRIBuilder.withString(NAMESPACE + "binary");
        GENERIC = IRIBuilder.withString(NAMESPACE + "generic");
        PUBLIC_KEY = IRIBuilder.withString(NAMESPACE + "publicKey");
        SIGNATURE = IRIBuilder.withString(NAMESPACE + "signature");

    }


}
