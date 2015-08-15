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

import java.util.Arrays;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.heronnet.module.kad.model.Node;

/**
 * The superclass of all message types
 *
 * Heron Kad messages are BSON objects containing an origin {@link Node Node} and
 * a randomly generated {@code} messageId byte array 20 bytes long (length compatible
 * with SHA1 hash
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class KadMessage {

    private static final Random randomGenerator = new Random();
    private final byte[] messageId = new byte[20];
    private Node origin;

    {
        randomGenerator.nextBytes(messageId);
    }

    public Node getOrigin() {
        return origin;
    }

    public void setOrigin(Node origin) {
        this.origin = origin;
    }

    public byte[] getMessageId() {
        return messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KadMessage that = (KadMessage) o;
        return Arrays.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return messageId[3] & 0xFF |
                (messageId[2] & 0xFF) << 8 |
                (messageId[1] & 0xFF) << 16 |
                (messageId[0] & 0xFF) << 24;
    }
}
