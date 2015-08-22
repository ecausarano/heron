package eu.heronnet.module.kad.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * An implementation of Kad using standard library classes
 *
 * The class consists of two sorted sets of {@code Node} objects, one for the
 * bucket heads, and another for the known nodes themselves.
 *
 * The same lookup occurs when searching for or adding a new Node.
 *
 * When inserting a new Node, the lookup table will iterate over the ordered set
 * of bucket heads, starting from the furthest one. If the Node is further away
 * (or equal) than a head it falls within that bucket and an attempt to either
 * retrieve the bucket or add the node to that bucket is attempted.
 *
 * If the Node is closer it is checked against the next head until one that
 * is closer than the Node is found.
 *
 * When adding a Node, the size of bucket must be maintained, if it is smaller
 * (or equal) than k the node is added, otherwise the bucket is "split"
 * by adding the k/2th Node in the bucket to the set of bucket heads.
 *
 *
 *
 * @author edoardocausarano
 *
 */
public class TreeSetImpl implements RadixTree {

    private static final Logger logger = LoggerFactory.getLogger(TreeSetImpl.class);
    private final Node self;
    private final TreeSet<Node> bucketHeads;
    private final TreeSet<Node> nodes;
    private int bucketSize = 20;

    public TreeSetImpl(Node self, int bucketSize) {
        this(self);
        this.bucketSize = bucketSize;
    }

    public TreeSetImpl(Node self) {
        this.self = self;
        Comparator<Node> comparator = new DistanceComparator(self);
        this.bucketHeads = new TreeSet<>(comparator);
        this.nodes = new TreeSet<>(comparator);
    }


    @Override
    public void insert(Node node) {

        // prevent the cast to int, make it look hex (at least)
        logger.debug("Inserting node id={}", node.getId());

        if (bucketHeads.isEmpty()) {
            nodes.add(node);
            bucketHeads.add(node);
            return;
        }

        Node floor = bucketHeads.floor(node);
        if (floor != null) { // found a bucket
            Node upperBound = bucketHeads.lower(floor); // the head of the next bucket

            NavigableSet<Node> bucket;
            if (upperBound == null) { // there's no upper bucket, there's only one bucket
                bucket = nodes.tailSet(floor, true);
            } else {
                bucket = nodes.subSet(floor, true, upperBound, false);
            }

            boolean didSplit = splitBucketIfFull(bucket);
            if (didSplit) {
                insert(node);
            } else {
                nodes.add(node);
            }
        } else { // no floor bucketHead? We're adding the closest Node ever seen
            bucketHeads.pollFirst(); // remove the head that is to be replaced
            NavigableSet<Node> bucket;
            if (bucketHeads.isEmpty()) { // there was only 1 bucket
                bucket = nodes;
            } else {
                Node upperBound = bucketHeads.first();
                bucket = nodes.headSet(upperBound, false);
            }

            splitBucketIfFull(bucket);
            nodes.add(node);
            bucketHeads.add(node);
        }
    }

    private boolean splitBucketIfFull(NavigableSet<Node> bucket) {
        if (bucket.size() >= bucketSize) { // bucket is full, "split" it
            int i = 0;
            for (Node newMiddle : bucket) {
                if (i == bucketSize / 2 - 1) {
                    bucketHeads.add(newMiddle);
                    return true;
                } else {
                    i++;
                }
            }
        }
        return false;
    }

    @Override
    public List<Node> find(byte[] key) {
        if (nodes.isEmpty()) {
            return Collections.singletonList(self);
        }

        Node node = new Node(key, null);
        Node higher = bucketHeads.higher(node);
        if (higher == null) {
            higher = bucketHeads.last();
        }
        Node lower = bucketHeads.lower(node);
        if (lower == null) {
            lower = bucketHeads.first();
        }
        return new ArrayList<>(nodes.subSet(lower, false, higher, true));
    }

    @Override
    public void delete(byte[] key) {
        throw new RuntimeException("TODO");
    }
}
