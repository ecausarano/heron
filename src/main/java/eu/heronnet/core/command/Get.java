package eu.heronnet.core.command;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.RandomAccess;
import java.util.UUID;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.heronnet.core.model.BinaryItem;
import eu.heronnet.core.module.network.dht.DHTService;

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
public class Get implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Get.class);

    private static final String key = "GET";

    @Inject
    private DHTService dhtService;
    private String file;

    private UUID uuid;


    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void execute() {
        logger.debug("called {}", key);
        BinaryItem result = dhtService.findByID(uuid);

        try {
            File tempFile = new File(file);
            final RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rw");
            final FileChannel fileChannel = randomAccessFile.getChannel();
            fileChannel.write(ByteBuffer.wrap(result.getData()));
            fileChannel.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void setArgs(String ... varargs) {
        this.file = varargs[0];
        this.uuid = UUID.fromString(varargs[1]);
    }

    @Inject
    private EventBus eventBus;
}
