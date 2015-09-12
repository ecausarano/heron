package eu.heronnet.model.vocabulary;

import eu.heronnet.model.IRI;
import eu.heronnet.model.builder.IRIBuilder;

/**
 * Vocabulary constants for the Dublin Core Metadata Element Set, version 1.1
 *
 * @author Edoardo Causarano
 * @see <a href="http://dublincore.org/documents/dces/">Dublin Core Metadata Element Set, Version 1.1</a>
 */
public class DC {

    /**
     * Dublin Core elements namespace: http://purl.org/dc/elements/1.1/
     */
    public static final String NAMESPACE = "http://purl.org/dc/elements/1.1/";

    /**
     * Recommend prefix for the Dublin Core elements namespace: "dc"
     */
    public static final String PREFIX = "dc";


    /**
     * dc:title
     */
    public static final IRI TITLE;

    /**
     * dc:source
     */
    public static final IRI SOURCE;

    /**
     * dc:contributor
     */
    public static final IRI CONTRIBUTOR;

    /**
     * dc:coverage
     */
    public static final IRI COVERAGE;

    /**
     * dc:creator
     */
    public static final IRI CREATOR;

    /**
     * dc:date
     */
    public static final IRI DATE;

    /**
     * dc:description
     */
    public static final IRI DESCRIPTION;

    /**
     * dc:format
     */
    public static final IRI FORMAT;

    /**
     * dc:identifier
     */
    public static final IRI IDENTIFIER;

    /**
     * dc:language
     */
    public static final IRI LANGUAGE;

    /**
     * dc:publisher
     */
    public static final IRI PUBLISHER;

    /**
     * dc:relation
     */
    public static final IRI RELATION;

    /**
     * dc:rights
     */
    public static final IRI RIGHTS;

    /**
     * dc:subject
     */
    public static final IRI SUBJECT;

    /**
     * dc:type
     */
    public static final IRI TYPE;

    static {
        CONTRIBUTOR = IRIBuilder.withString(NAMESPACE + "contributor");
        COVERAGE = IRIBuilder.withString(NAMESPACE + "coverage");
        CREATOR = IRIBuilder.withString(NAMESPACE + "creator");
        DATE = IRIBuilder.withString(NAMESPACE + "date");
        DESCRIPTION = IRIBuilder.withString(NAMESPACE +"description");
        FORMAT = IRIBuilder.withString(NAMESPACE + "format");
        IDENTIFIER = IRIBuilder.withString(NAMESPACE + "identifier");
        LANGUAGE = IRIBuilder.withString(NAMESPACE + "language");
        PUBLISHER = IRIBuilder.withString(NAMESPACE + "publisher");
        RELATION = IRIBuilder.withString(NAMESPACE + "relation");
        RIGHTS = IRIBuilder.withString(NAMESPACE + "rights");
        SOURCE = IRIBuilder.withString(NAMESPACE + "source");
        SUBJECT = IRIBuilder.withString(NAMESPACE + "subject");
        TITLE = IRIBuilder.withString(NAMESPACE + "title");
        TYPE = IRIBuilder.withString(NAMESPACE + "type");
    }
}
