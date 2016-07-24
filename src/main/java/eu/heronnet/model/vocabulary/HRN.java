package eu.heronnet.model.vocabulary;

import eu.heronnet.model.IRI;
import eu.heronnet.model.IRIBuilder;

import static eu.heronnet.model.vocabulary.HRN.Constants.NAMESPACE;

/**
 * @author edoardocausarano
 */
public enum HRN {

    BINARY (IRIBuilder.withString(NAMESPACE +"binary")),
    GENERIC (IRIBuilder.withString(NAMESPACE +"generic")),
    PUBLIC_KEY (IRIBuilder.withString(NAMESPACE +"publicKey")),
    SIGNATURE (IRIBuilder.withString(NAMESPACE +"signature"));

    public String toString() {
        return iri.toString();
    }

    public IRI getIri() {
        return iri;
    }

    private IRI iri;

    HRN(IRI iri) {
        this.iri = iri;
    }

    protected static class Constants {
        /**
         * Heron elements namespace: http://heronnet.eu/0.1/
         */
        static final String NAMESPACE = "http://heronnet.eu/0.1/";

        /**
         * Recommend prefix for the Heron elements namespace: "hrn"
         */
        static final String PREFIX = "hrn";
    }

}
