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

package eu.heronnet.module.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Environment;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityIndex;
import com.sleepycat.persist.EntityJoin;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.ForwardCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;

import eu.heronnet.core.model.Document;
import eu.heronnet.core.model.DocumentBuilder;
import eu.heronnet.core.model.Field;
import eu.heronnet.module.storage.model.StoredDocument;
import eu.heronnet.module.storage.model.StoredField;
import eu.heronnet.module.storage.model.converter.DocumentConverter;
import eu.heronnet.module.storage.model.converter.FieldConverter;

/**
 * BerlekeyDB implementation of the Persistence API
 */
@Singleton
@Resource
public class BerkeleyImpl implements Persistence {

    public static final String DOCUMENT_STORE = "DocumentStore";
    private static final Logger logger = LoggerFactory.getLogger(BerkeleyImpl.class);
    EntityStore documentStore;
    private PrimaryIndex<String, StoredDocument> documentPrimaryIndex;
    private PrimaryIndex<String, StoredField> fieldPrimaryIndex;
    private SecondaryIndex<String, String, StoredField> fieldBynGram;
    private SecondaryIndex<String, String, StoredDocument> documentByField;

    @Inject
    private Environment environment;

    public BerkeleyImpl(Environment environment) {
        this.environment = environment;
    }

    /**
     * Called during application startup, initialized the Berkeley environment and opens primary and secondary DBs
     *
     * @throws Exception
     */
    @PostConstruct
    protected void startUp() throws Exception {

        if (environment == null) {
            throw new RuntimeException("BerkeleyDB JE Environment not configured");
        }

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(true);

        documentStore = new EntityStore(environment, DOCUMENT_STORE, storeConfig);

        documentPrimaryIndex = documentStore.getPrimaryIndex(String.class, StoredDocument.class);
        fieldPrimaryIndex = documentStore.getPrimaryIndex(String.class, StoredField.class);

        documentByField = documentStore.getSecondaryIndex(documentPrimaryIndex, String.class, "meta");
        fieldBynGram = documentStore.getSecondaryIndex(fieldPrimaryIndex, String.class, "nGrams");

        logger.debug("Opened database name={}, count={}", DOCUMENT_STORE, documentPrimaryIndex.count());
    }

    /**
     * Called during application shutdown, performs housekeeping activities
     *
     * @throws Exception
     */
    @PreDestroy
    protected void shutDown() throws Exception {
        logger.debug("Shutting down DocumentStore");

        documentStore.close();
        environment.close();

        documentStore = null;
        environment = null;
    }

    @Override
    public void put(Document document) {
        StoredDocument storedDocument = DocumentConverter.asStoredDocument(document);

        Transaction txn = environment.beginTransaction(null, null);
        for (Field field : document.getMeta()) {
            StoredField storedField = FieldConverter.asStored(field);
            fieldPrimaryIndex.put(storedField);
        }
        documentPrimaryIndex.put(txn, storedDocument);
        txn.commit();
    }

    @Override
    public void deleteByField(Field field) {
        throw new RuntimeException("TODO, anyway the internet doesn't forget anyway :D");
    }

    @Override
    public List<Document> findDocumentByFieldSpec(List<Field> fields) {
        throw new RuntimeException("TODO");
    }

    @Override
    public List<Document> findByStringKey(List<String> searchKeys) {
        if (searchKeys.size() == 1) {
            String key = searchKeys.get(0);
            EntityIndex<String, StoredField> fieldEntityIndex = fieldBynGram.subIndex(key.toLowerCase());

            EntityJoin<String, StoredDocument> join = new EntityJoin<>(documentPrimaryIndex);

            try (EntityCursor<StoredField> storedFields = fieldEntityIndex.entities()) {
                Iterator<StoredField> iterator = storedFields.iterator();
                if (!iterator.hasNext()) {
                    return Collections.emptyList();
                }
                else {
                    while (iterator.hasNext()) {
                        StoredField storedField = iterator.next();
                        join.addCondition(documentByField, storedField.getHash());
                    }
                }
            }
            try (ForwardCursor<StoredDocument> storedDocuments = join.entities()) {
                return buildDocuments(storedDocuments);
            }
        }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Document> getAll() {
        try (EntityCursor<StoredDocument> cursor = documentPrimaryIndex.entities()) {
            return buildDocuments(cursor);
        }
    }

    private List<Document> buildDocuments(ForwardCursor<StoredDocument> storedDocuments) {
        List<Document> documents = new ArrayList<>();
        for (StoredDocument storedDocument : storedDocuments) {
            logger.debug("fetched stored document={}", storedDocument.getHash());

            // creating a builder, no binary data, not necessary at this point
            DocumentBuilder documentBuilder = DocumentBuilder.newInstance();
            documentBuilder.withHash(storedDocument.getHash());
            for (String fieldId : storedDocument.getMeta()) {
                StoredField storedField = fieldPrimaryIndex.get(fieldId);
                Field field = FieldConverter.asField(storedField);
                documentBuilder.withField(field);
            }
            documents.add(documentBuilder.build());
        }
        return documents;
    }

}
