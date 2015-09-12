package eu.heronnet.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by edo on 07/08/15.
 */
public class IRI extends Node<String> {

    private final String data;

    public IRI(@JsonProperty("nodeId") byte[] nodeId, @JsonProperty("data") String data) {
        super(nodeId, NodeType.STRING);
        this.data = data;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return data;
    }
}
