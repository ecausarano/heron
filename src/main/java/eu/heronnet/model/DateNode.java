package eu.heronnet.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by edo on 07/08/15.
 */
public class DateNode extends Node<Date> {

    private final Date date;

    public DateNode(@JsonProperty("nodeId") byte[] nodeId, @JsonProperty("date") Date date) {
        super(nodeId, NodeType.DATE);
        this.date = date;
    }

    @Override
    public Date getData() {
        return new Date(date.getTime());
    }

    @Override
    public String toString() {
        return date.toString();
    }
}
