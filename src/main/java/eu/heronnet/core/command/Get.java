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

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import eu.heronnet.core.model.MetadataCollection;
import eu.heronnet.core.module.network.dht.DHTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class Get implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Get.class);

    private static final String key = "GET";

    @Inject
    private DHTService dhtService;
    private String file;

    private byte[] id;
    @Inject
    private EventBus eventBus;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void execute() {
        logger.debug("called {}", key);
        MetadataCollection result = dhtService.findByID(id);

        try {
            File tempFile = new File(file);
            final RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rw");
            final FileChannel fileChannel = randomAccessFile.getChannel();
//            fileChannel.write(ByteBuffer.wrap(result.getData()));
            fileChannel.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void setArgs(String... varargs) {
        this.file = varargs[0];
        this.id = varargs[1].getBytes();
    }
}
