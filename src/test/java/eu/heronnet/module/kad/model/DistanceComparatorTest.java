package eu.heronnet.module.kad.model;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * @author edoardocausarano
 */
public class DistanceComparatorTest {

    @Test
    public void testLessThan() {
        Node self = new Node(new byte[]{(byte) 0x00}, null);
        DistanceComparator comparator = new DistanceComparator(self);

        Node closer = new Node(new byte[]{(byte) (0x02 & 0xff)}, null);
        Node further = new Node(new byte[]{(byte) (0xF0 & 0xff)}, null);

        assertEquals(comparator.compare(closer, further), -1);
    }

    @Test
    public void testGreaterThan() {
        Node self = new Node(new byte[]{(byte) 0x00}, null);
        DistanceComparator comparator = new DistanceComparator(self);

        Node closer = new Node(new byte[]{(byte) 0xfe}, null);
        Node further = new Node(new byte[]{(byte) 0xff}, null);

        assertEquals(comparator.compare(closer, further), 1);
    }

    @Test
    public void testEquals() {
        Node self = new Node(new byte[]{(byte) 0x00}, null);
        DistanceComparator comparator = new DistanceComparator(self);

        Node closer = new Node(new byte[]{(byte) 0x10}, null);
        Node further = new Node(new byte[]{(byte) 0x10}, null);

        assertEquals(comparator.compare(closer, further), 0);
    }

//    @Test
//    public void testCommonPrefixCalculator() {
//
//        Node self = new Node(new byte[]{(byte) 0x0f, (byte) 0xff}, null);
//        DistanceComparator comparator = new DistanceComparator(self);
//
//        Node firstByte = new Node(new byte[]{(byte) 0x0a, (byte) 0xaa}, null);
//        assertEquals(comparator.commonPrefixLength(firstByte), 4);
//
//        Node secondByte = new Node(new byte[]{(byte) 0x0f, (byte) 0x01}, null);
//        assertEquals(comparator.commonPrefixLength(secondByte), 8);
//    }

}
