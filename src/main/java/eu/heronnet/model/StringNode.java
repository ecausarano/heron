package eu.heronnet.model;

/**
 * Created by edo on 07/08/15.
 */
public class StringNode extends Node<String> {

    private final String data;

    public StringNode(
            byte[] nodeId,
            String data)
    {
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
