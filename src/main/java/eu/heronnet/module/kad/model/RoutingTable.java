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

import java.util.List;

/**
 * Routing table maintained by all nodes participating to the Kad network used to define a virtual network
 * based on a distance topology, where distance is defined as similarity of the network Id: the longer the prefix of
 * the integerId is, the closer the node is.
 *
 * Therefore two nodes can be compared and one be considered "closer" compared to another, depending on how much
 * of the network prefix is the same.
 *
 * (Interestingly enough, could the IPv6 network address === the node ID ?)
 *
 */
public interface RoutingTable<T extends Identifiable<ID>, ID> {
    void insert(T node);

    List<T> find(ID key);

    void delete(ID key);
}
