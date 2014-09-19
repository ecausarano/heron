package eu.heronnet.core.model.rdf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * Created by edoardocausarano on 12-09-14.
 */
@JsonInclude(NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public class Resource {


    public static Resource PROTOTYPE = new Resource("prototype");
    public static Resource METADATA_ITEM = new Resource("metadataItem");
    public static Resource FIELD = new Resource("field");
    public static Resource SIGNATURE = new Resource("signature");
    public static Resource BINARY = new Resource("binary");


    private Object value;

    private Class clazz;

    /**
     * Default connstructor, only used by the {@see Triple} constructor
     */
    Resource() {
        this.clazz = Triple.class;
    }

    @JsonCreator
    public Resource(@JsonProperty("string") String value) {
        this.value = value;
        clazz = String.class;
    }

    public Resource(Integer value) {
        this.value = value;
        clazz = Integer.class;
    }

    public Resource(Date value) {
        this.value = value;
        clazz = Date.class;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return (String) value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;

        Resource resource = (Resource) o;

        if (!clazz.equals(resource.clazz)) return false;
        if (value != null ? !value.equals(resource.value) : resource.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + clazz.hashCode();
        return result;
    }
}
