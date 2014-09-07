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

package eu.heronnet.core.command;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.heronnet.core.model.IDGenerator;
import eu.heronnet.core.model.Keys;
import eu.heronnet.core.module.network.dht.DHTService;
import eu.heronnet.core.module.storage.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Singleton
public class Invoker {

    private static final Logger logger = LoggerFactory.getLogger(Invoker.class);

    @Inject
    Persistence persistence;

    @Inject
    DHTService dhtService;

    @Inject
    IDGenerator idGenerator;

    public Invoker() {
        logger.debug("Invoker ctor");
    }

    @Subscribe
    public void handlePut(Put command) {
        final Map<String, byte[]> payload = command.getPayload();

        logger.debug("Handling 'put' event for item with id={}", payload.get(Keys.ID));

        // I guess this should always happen...
        byte[] id = payload.get(Keys.ID);
        if (id == null) {
            id = idGenerator.generateId(payload);
            payload.put(Keys.ID, id);
        }
        dhtService.persist(payload);
    }

    @Subscribe
    public void handlePing(Ping command) {
        logger.debug("Called PING command");
        dhtService.ping();
    }
}
