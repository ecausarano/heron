package eu.heronnet.model.vocabulary;

import eu.heronnet.model.IRI;
import eu.heronnet.model.IRIBuilder;

import static eu.heronnet.model.vocabulary.DC.Constants.NAMESPACE;
import static eu.heronnet.model.vocabulary.DC.Constants.PREFIX;

/**
 * Vocabulary constants for the Dublin Core Metadata Element Set, version 1.1
 *
 * @author Edoardo Causarano
 * @see <a href="http://dublincore.org/documents/dces/">Dublin Core Metadata Element Set, Version 1.1</a>
 */
public enum DC {
        CONTRIBUTOR   (IRIBuilder.withString(NAMESPACE + "contributor")),
        COVERAGE     (IRIBuilder.withString(NAMESPACE + "coverage")),
        CREATOR      (IRIBuilder.withString(NAMESPACE + "creator")),
        DATE         (IRIBuilder.withString(NAMESPACE + "date")),
        DESCRIPTION   (IRIBuilder.withString(NAMESPACE + "description")),
        FORMAT       (IRIBuilder.withString(NAMESPACE + "format")),
        IDENTIFIER   (IRIBuilder.withString(NAMESPACE + "identifier")),
        LANGUAGE    (IRIBuilder.withString(NAMESPACE + "language")),
        PUBLISHER   (IRIBuilder.withString(NAMESPACE + "publisher")),
        RELATION     (IRIBuilder.withString(NAMESPACE + "relation")),
        RIGHTS      (IRIBuilder.withString(NAMESPACE + "rights")),
        SOURCE      (IRIBuilder.withString(NAMESPACE + "source")),
        SUBJECT      (IRIBuilder.withString(NAMESPACE + "subject")),
        TITLE       (IRIBuilder.withString(NAMESPACE + "title")),
        TYPE        (IRIBuilder.withString(NAMESPACE + "type"));

    protected static class Constants {
        static final String NAMESPACE = "http://purl.org/dc/elements/1.1/";
        static final String PREFIX = "dc";
    }

    private IRI iri;

    public IRI getIri() {
        return iri;
    }

    public static String getNAMESPACE() {
        return NAMESPACE;
    }

    public static String getPREFIX() {
        return Constants.PREFIX;
    }

    public String toString() {
        return iri.toString();
    }

    public static DC fromString(String string) {
        if (string.startsWith(NAMESPACE)) {
            String substring = string.substring(NAMESPACE.length());
            return DC.valueOf(substring.toUpperCase());
        } else if (string.startsWith(PREFIX)){
            String substring = string.substring(PREFIX.length());
            return DC.valueOf(substring.toUpperCase());
        } else throw new IllegalArgumentException("bad prefix=" + string);
    }

    DC(IRI iri) {
        this.iri = iri;
    }
}
