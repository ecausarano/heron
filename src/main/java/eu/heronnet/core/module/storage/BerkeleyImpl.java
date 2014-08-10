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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.AbstractIdleService;
import com.sleepycat.je.*;
import de.undercouch.bson4jackson.BsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.sleepycat.je.LockMode.DEFAULT;
import static com.sleepycat.je.OperationStatus.SUCCESS;

/**
 * BerlekeyDB implementation of the Persistence API
 * <p/>
 * Opens the primary BSON object store and secondary index(es) during the startUp phase of the AbstractIdleService.
 * Closes the databases and cleans up the Berkeley environment during shutdown.
 */
public class BerkeleyImpl extends AbstractIdleService implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(BerkeleyImpl.class);

    String path = "herondb";
    Environment environment;
    Database primaryData;
    SecondaryDatabase filenameIndex;
    ObjectMapper mapper = new ObjectMapper(new BsonFactory());
    private List<Map<String, String>> metadataCache;

    /**
     * Called during application startup, initialized the Berkeley environment and opens primary and secondary DBs
     *
     * @throws Exception
     */
    @Override
    protected void startUp() throws Exception {

        final EnvironmentConfig config = new EnvironmentConfig();
        config.setAllowCreate(true);
        config.setTransactional(false);

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
        final SecondaryConfig indexConfig = new SecondaryConfig();
        indexConfig.setAllowCreate(true);
        indexConfig.setSortedDuplicates(true);
        indexConfig.setKeyCreator(new FilenameIndexKeyCreator());

        filenameIndex = environment.openSecondaryDatabase(null, "FilenameIndex", primaryData, indexConfig);
        logger.debug("Opened secondary name=FilenameIndex, count={}", filenameIndex.count());

        // initialize cache

        // ADD FILENAMES
        // integer overflow?
        logger.debug("Found count={} items in filenameIndex", filenameIndex.count());
        metadataCache = new ArrayList<>((int) filenameIndex.count());
        try (DiskOrderedCursor cursor = filenameIndex.openCursor(null)) {
            final DatabaseEntry key = new DatabaseEntry();
            final DatabaseEntry value = new DatabaseEntry();
            while (SUCCESS.equals(cursor.getNext(key, value, null))) {
                final Map<String, String> map = new HashMap<>();
                map.put("filename", new String(key.getData()));
                metadataCache.add(map);
            }
        }
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

    /**
     * Fetches all entries from the primary local store, should never be called
     *
     * @return List of all entries stored locally
     */
    @Deprecated
    @Override
    public List<Map<String, byte[]>> getAll() {

        try (final DiskOrderedCursor cursor = primaryData.openCursor(null)) {
            final DatabaseEntry key = new DatabaseEntry();
            final DatabaseEntry value = new DatabaseEntry();

            final List<Map<String, byte[]>> items = new ArrayList<>();
            while (SUCCESS.equals(cursor.getNext(key, value, DEFAULT))) {
                final JsonFactory factory = mapper.getFactory();
                final JsonParser parser = factory.createParser(value.getData());
                final Map<String, byte[]> map = new HashMap<>();
                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    map.put(parser.getCurrentName(), parser.getBinaryValue());
                }
                items.add(map);
            }
            ;
            return items;
        } catch (IOException e) {
            logger.error("Error fetching data entries: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, byte[]> getById(byte[] id) throws IOException {
        final DatabaseEntry entry = new DatabaseEntry();
        final OperationStatus operationStatus = primaryData.get(null, new DatabaseEntry(id), entry, null);
        if (operationStatus.equals(SUCCESS)) {
            final JsonFactory factory = mapper.getFactory();
            final JsonParser parser = factory.createParser(entry.getData());

            final HashMap<String, byte[]> map = new HashMap<>();
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                map.put(parser.getCurrentName(), parser.getBinaryValue());
            }
            return map;
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public void put(Map<String, byte[]> item) throws IOException {
        logger.debug("called put for item={}", new String(item.get("filename")));
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();

            final byte[] keyValue = digest.digest(item.get("data"));
            final DatabaseEntry key = new DatabaseEntry(keyValue);
            final DatabaseEntry value = new DatabaseEntry(mapper.writeValueAsBytes(item));
            final OperationStatus status = primaryData.put(null, key, value);
            if (SUCCESS.equals(status)) {
                final HashMap<String, String> metadata = new HashMap<>();
                metadata.put("filename", new String(item.get("filename")));
                metadataCache.add(metadata);
            }
            logger.debug("inserted item with key={}", new String(keyValue));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot happen, ever!", e);
        }
    }

    /**
     * Deletes a database entry
     *
     * @param id
     * @throws IOException
     */
    @Override
    public void delete(byte[] id) throws IOException {
        try {
            final OperationStatus status = primaryData.delete(null, new DatabaseEntry(id));
            if (SUCCESS.equals(status)) {
                throw new RuntimeException("handle the cache!");
            }
        } catch (DatabaseException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Map<String, String> getMetadataForID(byte[] id) {
        return null;
    }

    @Override
    public List<Map<String, String>> getAllMetadata() throws IOException {
        return metadataCache;
    }

    @Override
    public long count() {
        return primaryData.count();
    }
}
