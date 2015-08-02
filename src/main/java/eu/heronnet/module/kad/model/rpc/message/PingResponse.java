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

/**
 * A {@code PingResponse} in returned in response to a {@link PingRequest}.<br/>
 *
 * As with all {@code KadMessage} types it contains the origin {@link eu.heronnet.module.kad.model.Node Node}
 * as well as the {@code messageId} of the {@code PingRequest} that the current {@link eu.heronnet.module.kad.model.Node Node}
 * is responding to.
 */
public class PingResponse extends KadMessage {

    private byte[] response;

    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }
}
