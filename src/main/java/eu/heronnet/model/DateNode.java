package eu.heronnet.model;

import java.util.Date;

/**
 * Created by edo on 07/08/15.
 */
public class DateNode extends Node<Date> {

    private final Date date;

    public DateNode(byte[] nodeId, Date date) {
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
