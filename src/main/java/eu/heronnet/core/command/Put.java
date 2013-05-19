package eu.heronnet.core.command;

import java.util.UUID;

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.heronnet.core.model.FileStreamBinary;
import eu.heronnet.core.module.network.dht.KadServiceImpl;

/**
 * This file is part of heron Copyright (C) 2013-2013 edoardocausarano
 * <p/>
 * heron is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p/>
 * heron is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with Foobar.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
public class Put implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Put.class);
    private static final String key = "PUT";

    @Inject
    private KadServiceImpl dhtService;
    private String file;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void execute() {
        logger.debug("called {}", key);
        FileStreamBinary binary = new FileStreamBinary();
        binary.setPath(file);
        binary.setUuid(UUID.randomUUID());

        binary.loadData();
        dhtService.put(binary, binary.getUuid());

        logger.debug("PUT {} with UUID: {}", file, binary.getUuid());

    }

    @Override
    public void setArgs(String... varargs) {
        file = varargs[0];
    }
}
