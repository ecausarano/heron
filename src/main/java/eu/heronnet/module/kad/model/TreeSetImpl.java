package eu.heronnet.module.kad.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author edoardocausarano
 *
 */
public class TreeSetImpl implements RoutingTable<Node, byte[]> {

    private static final Logger logger = LoggerFactory.getLogger(TreeSetImpl.class);

    private final Node self;
    private final TreeSet<Node> nodes;

    private int bucketSize = 20;

    public TreeSetImpl(Node self, int bucketSize) {
        this.self = self;
        this.bucketSize = bucketSize;
        this.nodes = new TreeSet<>();
    }


    @Override
    public void insert(Node node) {
        final Set<Node> bucket = getBucketForNode(node);

        if (bucket.size() == 0) {
            logger.debug("Inserting first node in routing table");
            nodes.add(node);
            return;
        }

        if (bucket.size() <= bucketSize) {
            logger.debug("Inserting node {} in bucket size {}", node.getId(), bucketSize);
            bucket.add(node);
        } else {
            logger.debug("max bucket size reached, randomly pruning bucket");
            final Random random = new Random();
            // TODO - check with ping on separate thread
            bucket.removeIf(node1 -> random.nextBoolean());
        }
    }

    @Override
    public List<Node> find(byte[] key) {
        return new ArrayList<>(getBucketForNode(new Node(key, Collections.emptyList())));
    }

    @Override
    public void delete(byte[] key) {
        nodes.removeIf(node -> node.getId() == key);
    }

    private int getCommonPrefixIndex(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new RuntimeException("don't XOR byte[]'s with different lenght");
        }

        BitSet xor = BitSet.valueOf(a);
        xor.xor(BitSet.valueOf(b));

        return xor.previousSetBit(xor.length());
    }

    private Set<Node> getBucketForNode(Node node) {
        final int commonPrefix = getCommonPrefixIndex(self.getId(), node.getId());
        final BitSet maskedBitset = BitSet.valueOf(node.getId());
        maskedBitset.clear(0, commonPrefix + 1);
        final Node min = new Node(maskedBitset.toByteArray(), Collections.emptyList());

        maskedBitset.set(0, commonPrefix + 1);
        final Node max = new Node(maskedBitset.toByteArray(), Collections.emptyList());

        return nodes.subSet(min, true, max, true);
    }
}
