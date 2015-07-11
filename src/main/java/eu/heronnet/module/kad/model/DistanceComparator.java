package eu.heronnet.module.kad.model;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;

/**
 * @author edoardocausarano
 */
class DistanceComparator implements Comparator<Node> {

    private final Node selfNode;

    public DistanceComparator(Node selfNode) {
        this.selfNode = selfNode;
    }

    private static byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[Math.min(a.length, b.length)];

        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (((int) a[i]) ^ ((int) b[i]));
        }

        return result;
    }

    @Override
    public int compare(Node o1, Node o2) {
        if (Arrays.equals(o1.getId(), o2.getId())) {
            return 0;
        } else {
         return commonPrefixLength(o1) < commonPrefixLength(o2) ? -1 : 1;
        }
    }

    private long commonPrefixLength(Node node) {
        // bigendian ids
        BitSet bitSet = BitSet.valueOf(xor(selfNode.getId(), node.getId()));
        return bitSet.nextSetBit(0);
    }
}
