package eu.heronnet.core.model.rdf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by edoardocausarano on 12-09-14.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public class Triple extends Resource {

    private Resource subject;

    private Predicate predicate;

    private Resource object;

    @JsonCreator
    public Triple(@JsonProperty("subject") Resource subject,
                  @JsonProperty("predicate") Predicate predicate,
                  @JsonProperty("object") Resource object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public static Triple isa(Resource subject, Resource object) {
        return new Triple(subject, Predicate.ISA, object);
    }

    public static Triple has(Resource subject, Resource object) {
        return new Triple(subject, Predicate.HAS, object);
    }

    public Resource getSubject() {
        return subject;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public Resource getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "subject=" + subject +
                ", predicate=" + predicate +
                ", object=" + object +
                '}';
    }


}
