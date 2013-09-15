package eu.heronnet.core.command;

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.heronnet.core.model.BinaryItem;
import eu.heronnet.core.module.network.dht.DHTService;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

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
    private DHTService dhtService;
    private String file;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void execute() {
        logger.debug("called {}", key);
        BinaryItem binary = new BinaryItem();

        binary.setUuid(UUID.randomUUID());

        Path path = FileSystems.getDefault().getPath(file);
        try {
            byte[] buf = Files.readAllBytes(path);
            binary.setData(buf);
            dhtService.persist(binary);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    @Override
    public void setArgs(String... varargs) {
        file = varargs[0];
    }
}
