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
import com.google.inject.Singleton;
import com.sleepycat.je.*;
import eu.heronnet.core.model.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * BerlekeyDB implementation of the Persistence API
 */
@Singleton
public class BerkeleyImpl extends AbstractIdleService implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(BerkeleyImpl.class);

    String path = "herondb";
    Environment environment;
    Database primaryData;
    SecondaryDatabase filenameIndex;

    public BerkeleyImpl() {
        logger.debug("BerkeleyImpl ctor={}", this);
    }

    /**
     * Called during application startup, initialized the Berkeley environment and opens primary and secondary DBs
     *
     * @throws Exception
     */
    @Override
    protected void startUp() throws Exception {

        final EnvironmentConfig config = new EnvironmentConfig();
        config.setAllowCreate(true);
        config.setTransactional(true);

        final File envHome = new File(path);
        if (!envHome.exists()) {
            envHome.mkdir();
        }
        environment = new Environment(envHome, config);

        // the primary object store
        final DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        databaseConfig.setTransactional(false);
        primaryData = environment.openDatabase(null, "PrimaryData", databaseConfig);
        logger.debug("Opened primary name=PrimaryData, count={}", primaryData.count());


        // indexes
//        final SecondaryConfig indexConfig = new SecondaryConfig();
//        indexConfig.setAllowCreate(true);
//        indexConfig.setSortedDuplicates(true);
//        indexConfig.setKeyCreator(new FilenameIndexKeyCreator());
//
//        filenameIndex = environment.openSecondaryDatabase(null, "FilenameIndex", primaryData, indexConfig);
//        logger.debug("Opened secondary name=FilenameIndex, count={}", filenameIndex.count());

        // initialize cache

        // ADD FILENAMES
        // integer overflow?
//        logger.debug("Found count={} items in filenameIndex", filenameIndex.count());
//        metadataCache = new ArrayList<>((int) filenameIndex.count());
//        try (DiskOrderedCursor cursor = filenameIndex.openCursor(null)) {
//            final DatabaseEntry key = new DatabaseEntry();
//            final DatabaseEntry value = new DatabaseEntry();
//            while (SUCCESS.equals(cursor.getNext(key, value, null))) {
//                final Map<String, String> map = new HashMap<>();
//                map.put("filename", new String(key.getData()));
//                metadataCache.add(map);
//            }
//        }
    }

    /**
     * Called during application shutdown, performs housekeeping activities
     *
     * @throws Exception
     */
    @Override
    protected void shutDown() throws Exception {
        logger.debug("Shutting down BerkeleyDB");
        filenameIndex.close();
        primaryData.close();

        environment.close();

        filenameIndex = null;
        primaryData = null;
        environment = null;
    }

    @Override
    public void put(Map<String, byte[]> item) throws IOException {
        logger.debug("Persisting item with id={}", item.get(Keys.ID));
        final byte[] dataBytes = item.get(Keys.DATA);
        final DatabaseEntry key = new DatabaseEntry(item.get(Keys.ID));
        final DatabaseEntry value = new DatabaseEntry(dataBytes);
        primaryData.put(null, key, value);
    }
}
