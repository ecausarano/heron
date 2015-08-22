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

package eu.heronnet.module.kad.model.rpc.message;

import eu.heronnet.module.kad.model.Node;

import java.util.List;

public class FindNodeResponse extends KadMessage {

    private List<Node> foundNodes;

    public List<Node> getFoundNodes() {
        return foundNodes;
    }

    public void setFoundNodes(List<Node> foundNodes) {
        this.foundNodes = foundNodes;
    }
}
