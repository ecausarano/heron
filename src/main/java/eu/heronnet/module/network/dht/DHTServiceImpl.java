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

package eu.heronnet.module.network.dht;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.heronnet.kad.net.Client;

@Singleton
public class DHTServiceImpl extends DHTService {

    private static Logger logger = LoggerFactory.getLogger(DHTServiceImpl.class);

    @Inject
    Client client;

    // @Override
    // // public void persist(byte[] binary, Triple triple) {
    // // final StoreValueRequest storeValueRequest = new StoreValueRequest(binary, triple);
    // // client.send(storeValueRequest);
    // }

    @Override
    public Map<String, byte[]> findByID(byte[] id) {
        return null;
    }

    @Override
    public void deleteByID(byte[] id) {

    }

    @Override
    public void ping() {

    }
}
