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

package eu.heronnet.core.module.storage;

import com.google.common.util.concurrent.AbstractIdleService;
import eu.heronnet.core.model.BinaryItem;
import eu.heronnet.core.model.MetadataCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class BerkeleyImpl extends AbstractIdleService implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(BerkeleyImpl.class);

    @Override
    protected void startUp() throws Exception {

    }

    @Override
    protected void shutDown() throws Exception {

    }

    @Override
    public void persistBinary(BinaryItem item) throws IOException {

    }

    @Override
    public MetadataCollection findByID(byte[] id) {
        return null;
    }

    @Override
    public List<MetadataCollection> fetchAllMedatadaItems() {
        return null;
    }

    @Override
    public void persistMetadata(List<MetadataCollection> metadataCollections) {

    }

    @Override
    public void deleteByID(byte[] id) {

    }
}
