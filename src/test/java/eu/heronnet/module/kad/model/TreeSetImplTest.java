package eu.heronnet.module.kad.model;

import static org.testng.Assert.assertFalse;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.testng.annotations.Test;

/**
 * @author edoardocausarano
 */
public class TreeSetImplTest {

    final Random random = new Random();
    final byte[] id = new byte[20];

    @Test
    public void testInsert() {
        random.nextBytes(id);

        final Node self = new Node(id, Collections.emptyList());
        final TreeSetImpl routingTable = new TreeSetImpl(self, 20);

        for (int i = 0; i < 1024; i++) {
            random.nextBytes(id);
            routingTable.insert(new Node(id, Collections.emptyList()));
        }

        random.nextBytes(id);
        final List<Node> nodes = routingTable.find(id);
        assertFalse(nodes.isEmpty());

    }
}
