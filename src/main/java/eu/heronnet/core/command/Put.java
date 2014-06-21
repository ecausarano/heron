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

import com.google.inject.Inject;
import eu.heronnet.core.model.Binary;
import eu.heronnet.core.module.network.dht.DHTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

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

        Path path = FileSystems.getDefault().getPath(file);
        try {
            byte[] buf = Files.readAllBytes(path);
            final Binary binary;
            try {
                binary = new Binary(buf);
                dhtService.persist(binary);
            } catch (NoSuchAlgorithmException e) {
                logger.error("should never happen, apparently SHA-1 is not supported");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void setArgs(String... varargs) {
        file = varargs[0];
    }
}
