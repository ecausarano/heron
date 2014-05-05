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

package eu.heronnet.kad.model;

import com.sun.istack.internal.NotNull;

import java.net.InetAddress;
import java.util.BitSet;

public class Node implements Comparable<Node> {

    private byte[] id;

    private InetAddress address;

    private int port;

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int compareTo(@NotNull Node that) {
        BitSet thatBitSet = BitSet.valueOf(that.getId());
        final BitSet thisBitSet = BitSet.valueOf(id);

        thatBitSet.xor(thisBitSet);

        // first bit that differs
        int offset = thatBitSet.nextSetBit(0);
        // identical
        if (offset == -1) {
            return 0;
        } else {
            // this node has the larger index
            if (thisBitSet.get(offset)) {
                return 1;
            } else {
                // this node has the smaller index
                return -1;
            }
        }
    }
}
