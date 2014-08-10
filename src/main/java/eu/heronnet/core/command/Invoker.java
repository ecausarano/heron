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
import eu.heronnet.core.module.network.dht.DHTService;
import eu.heronnet.core.module.storage.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Invoker {

    private static final Logger logger = LoggerFactory.getLogger(Invoker.class);

    @Inject
    Persistence persistence;

    @Inject
    DHTService dhtService;

    @Subscribe
    public void handlePut(Put command) {
        logger.debug("called put");
        try {
            persistence.put(command.getPayload());
        } catch (IOException e) {
            logger.error("Error persisting payload");
        }
    }

    @Subscribe
    public void handlePing(Ping command) {
        logger.debug("Called PING command");
        dhtService.ping();
    }
}
