/*
 * Copyright (C) 2014 edoardocausarano
 *
 * heron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * heron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with heron. If not, see http://www.gnu.org/licenses
 */

package eu.heronnet.module.kad.model;

import java.net.InetSocketAddress;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

public class Node implements Comparable<Node> {

    private byte[] id;

    private List<InetSocketAddress> addresses;
    private Date lastSeen;
    private long RTT;

    private Node() {}

    public Node(byte[] id, List<InetSocketAddress> addresses) {
        this.id = id;
        this.addresses = addresses;
    }

    public Node(byte[] id, List<InetSocketAddress> addresses, Date lastSeen) {
        this.id = id;
        this.addresses = addresses;
        this.lastSeen = lastSeen;
    }

    public byte[] getId() {
        return id;
    }

    public List<InetSocketAddress> getAddresses() {
        return addresses;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getRTT() {
        return RTT;
    }

    public void setRTT(long RTT) {
        this.RTT = RTT;
    }

    @Override
    public int compareTo(Node that) {
        BitSet thatBitSet = BitSet.valueOf(that.getId());
        final BitSet thisBitSet = BitSet.valueOf(id);

        thatBitSet.xor(thisBitSet);

        // first bit that differs
        int offset = thatBitSet.nextSetBit(0);
        // identical
        if (offset == -1) {
            return 0;
        }
        else {
            // this node has the larger index
            if (thisBitSet.get(offset)) {
                return 1;
            }
            else {
                // this node has the smaller index
                return -1;
            }
        }
    }
}
