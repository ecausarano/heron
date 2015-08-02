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
 * Discovery of new {@link eu.heronnet.module.kad.model.Node Nodes} occurs during
 * all incoming message handling phases.
 *
 * A Heron node is seldom totally idle as it is always busy handling incoming
 * {@link FindNodeRequest FindNodeRequest} or {@link FindValueRequest}
 * messages, therefore Bucket lists are constantly updated with new {@link eu.heronnet.module.kad.model.Node Node} items.
 *
 * Part of this maintenance includes the expiry of an offline {@link eu.heronnet.module.kad.model.Node Node}
 * and in order to guarantee the periodic of this task, a {@code Ping} message type is
 * also included to guarantee the execution of this process in any circumstance.
 *
 */
public class PingRequest extends KadMessage {
}
