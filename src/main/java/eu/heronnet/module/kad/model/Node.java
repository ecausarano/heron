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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Represents a node of the Heron network
 */
public class Node implements Identifiable<byte[]>, Comparable<Node> {

    private byte[] id;

    private List<byte[]> addresses;
    private Date lastSeen;
    private int RTT;

    /**
     * Node constructor.
     *
     * @param id the random ID of a node
     * @param addresses a list of known IP of the node
     */
    public Node(byte[] id, List<byte[]> addresses) {
        this.id = Arrays.copyOf(id, id.length);
        this.addresses = Collections.unmodifiableList(new ArrayList<>(addresses));
    }

    public Node(byte[] id, List<byte[]> addresses, Date lastSeen) {
        this(id, addresses);
        this.lastSeen = new Date(lastSeen.getTime());
    }

    @Override
    public byte[] getId() {
        return id;
    }

    public List<byte[]> getAddresses() {
        return addresses;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public int getRTT() {
        return RTT;
    }

    public void setRTT(int RTT) {
        this.RTT = RTT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (RTT != node.RTT) return false;
        if (!Arrays.equals(id, node.id)) return false;
        if (!addresses.equals(node.addresses)) return false;
        return !(lastSeen != null ? !lastSeen.equals(node.lastSeen) : node.lastSeen != null);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(id);
        result = 31 * result + addresses.hashCode();
        result = 31 * result + (lastSeen != null ? lastSeen.hashCode() : 0);
        result = 31 * result + (RTT ^ (RTT >>> 32));
        return result;
    }

    @Override
    public int compareTo(Node o) {
        BigInteger thisNode = id.length == 0 ? BigInteger.ZERO : new BigInteger(1, id);
        BigInteger thatNode = o.getId().length == 0 ? BigInteger.ZERO : new BigInteger(1, o.getId());
        return thisNode.compareTo(thatNode);
    }
}
