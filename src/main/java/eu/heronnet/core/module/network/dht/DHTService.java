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

package eu.heronnet.core.module.network.dht;

import java.util.Map;

/**
 * Represents the essential API of a distributed hash table as described in the Kad paper
 */
public abstract class DHTService {

    public abstract byte[] persist(Map<String, byte[]> data);

    public abstract Map<String, byte[]> findByID(byte[] id);

    public abstract void deleteByID(byte[] id);

    public abstract void ping();
}
