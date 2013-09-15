package eu.heronnet.core.module.storage;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import eu.heronnet.core.model.BinaryItem;
import io.netty.channel.*;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.UUID;
import java.util.prefs.Preferences;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

/**
 * This file is part of heron
 * Copyright (C) 2013-2013 edoardocausarano
 * <p/>
 * heron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * heron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
public class DBStorage implements Persistence {


    private static final Logger logger = LoggerFactory.getLogger(DBStorage.class);

    private DB database;

    private Service service = new AbstractIdleService() {
        @Override
        protected void startUp() throws Exception {
            final Preferences preferences = Preferences.userNodeForPackage(this.getClass());
            String storagePath = preferences.get("store",
                    System.getProperty("user.home") + File.separator + "heron");

            logger.debug("opening database at path: {}", storagePath);

            Options options = new Options();
            options.createIfMissing(true);

            database = factory.open(new File(storagePath), options);
        }

        @Override
        protected void shutDown() throws Exception {
            logger.debug("shutting down database...");
            database.close();
        }
    };

    private ChannelHandler handler = new ChannelInboundHandlerAdapter() {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object e) throws Exception {
            if (e instanceof BinaryItem) {
                BinaryItem item = (BinaryItem)e;
                final DBStorage dbStorage = new DBStorage();
                dbStorage.persist(item);
                logger.debug("storing binary: {}", item.getUUID());
            }
            super.channelRead(ctx, e);
        }
    };

    @Override
    public UUID persist(final BinaryItem data) {
        final UUID uuid = data.getUUID();

        logger.debug("persisting {}", data.getUUID());
        database.put(uuid.toString().getBytes(), data.getData());
        return uuid;
    }

    @Override
    public BinaryItem findByID(final UUID id) {
        final byte[] bytes = database.get(id.toString().getBytes());
        final BinaryItem binaryItem = new BinaryItem();
        binaryItem.setUuid(id);
        binaryItem.setData(bytes);
        return  binaryItem;
    }

    @Override
    public void deleteByID(final UUID id) {
        database.delete(id.toString().getBytes());
    }

    @Override
    public ChannelHandler getHandler() {
        return handler;
    }

    @Override
    public Service getService() {
        return service;
    }
}
