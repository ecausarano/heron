package eu.heronnet.module.kad.model;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

/**
 * @author edoardocausarano
 */
public class TreeSetImplTest {

    private Random random = new Random();
    private Node thisNode;

    @BeforeClass
    public void init() {
        byte[] nodeId = new byte[1];
        nodeId[0] = 0b00000000;
        thisNode = new Node(nodeId, null);
    }


    @Test
    public void testInsert() throws Exception {
        TreeSetImpl treeSet = new TreeSetImpl(thisNode);

        byte[] nodeId = new byte[1];
        nodeId[0] = 0b00001000;
        Node node = new Node(nodeId, null);

        treeSet.insert(node);

        Assert.assertNotNull(treeSet.find(nodeId));
    }

    @Test
    public void testFind() throws Exception {
        TreeSetImpl treeSet = new TreeSetImpl(thisNode, 4);

        for (int i = 0; i<256; i++) {
            Node node = new Node(new byte[]{ (byte) i }, null);
            treeSet.insert(node);
        }

        byte[] key = {(byte) 0x0A};
        List<Node> nodeList = treeSet.find(key);
        Assert.assertEquals(nodeList.size(), 4);
    }
}
