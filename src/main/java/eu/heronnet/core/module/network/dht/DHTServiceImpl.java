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

import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.heronnet.core.model.BinaryItem;
import eu.heronnet.core.model.MetadataItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public class DHTServiceImpl extends DHTService {

    private static Logger logger = LoggerFactory.getLogger(DHTServiceImpl.class);

    List<URI> netBootStrap = null;

    public void run() {
        Injector injector = Guice.createInjector(
        );

    }


    @Override
    public UUID persist(BinaryItem data) {
        return null;
    }

    @Override
    public MetadataItem findByID(UUID id) {
        return null;
    }


    @Override
    public void deleteByID(final UUID id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void ping() {

    }
}
