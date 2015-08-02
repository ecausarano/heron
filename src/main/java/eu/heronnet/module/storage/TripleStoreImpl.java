package eu.heronnet.module.storage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AbstractIdleService;
import com.sleepycat.je.*;

import eu.heronnet.core.model.BinaryStatement;
import eu.heronnet.core.model.Bundle;
import eu.heronnet.core.model.Bundle.BundleBuilder;
import eu.heronnet.core.model.Statement;
import eu.heronnet.core.model.Triple;
import eu.heronnet.module.storage.binding.StringTripleBinding;

/**
 * @author edoardocausarano
 */
@Component
public class TripleStoreImpl extends AbstractIdleService implements Persistence {

    private static final String TRIPLE_INDEX = "TripleIndex";
    private static final String TRIPLE_NGRAMS = "TripleNGrams";
    private static final String TRIPLE_STORE = "TripleStore";
    private static final String BINARY_STORE = "BinaryStore";

    private static final Logger logger = LoggerFactory.getLogger(TripleStoreImpl.class);

    @Inject
    private Environment environment;
    @Inject
    private DatabaseConfig databaseConfig;
    @Inject @Named("tripleStoreIndexConfig")
    private SecondaryConfig indexConfig;

    @Inject @Named("tripleStoreNGramConfig")
    private SecondaryConfig nGramConfig;

    @Inject
    private StringTripleBinding stringTripleBinding;

    private Database tripleStore;
    private Database binaryStore;
    private SecondaryDatabase tripleIndex;
    private SecondaryDatabase tripleNGramIndex;

    public TripleStoreImpl() {
    }

    public TripleStoreImpl(Environment environment) {
        this.environment = environment;
    }

    /**
     * Called during application startup, initialized the Berkeley environment and opens primary and secondary DBs
     *
     * @throws Exception
     */
    @Override
    protected void startUp() throws Exception {

        if (environment == null) {
            throw new RuntimeException("BerkeleyDB JE Environment not configured");
        }


        Transaction txn = environment.beginTransaction(null, null);
        try {
            binaryStore = environment.openDatabase(txn, BINARY_STORE, databaseConfig);
            tripleStore = environment.openDatabase(txn, TRIPLE_STORE, databaseConfig);
            tripleIndex = environment.openSecondaryDatabase(txn, TRIPLE_INDEX, tripleStore, indexConfig);
            tripleNGramIndex = environment.openSecondaryDatabase(txn, TRIPLE_NGRAMS, tripleStore, nGramConfig);
            txn.commit();
        } catch (RuntimeException e) {
            logger.error("error while opening databases, message={}", e.getMessage());
            txn.abort();
        }

        logger.debug("Opened {}, count={}", BINARY_STORE, binaryStore.count());
        logger.debug("Opened {}, count={}", TRIPLE_STORE, tripleStore.count());
        logger.debug("Opened {}, count={}", TRIPLE_INDEX, tripleIndex.count());
        logger.debug("Opened {}, count={}", TRIPLE_NGRAMS, tripleNGramIndex.count());
    }


    /**
     * Called during application shutdown, performs housekeeping activities
     *
     * @throws Exception
     */
    @Override
    protected void shutDown() throws Exception {

        tripleIndex.close();
        tripleNGramIndex.close();

        tripleStore.close();
        binaryStore.close();

        int logsCleaned = environment.cleanLog();
        environment.close();
        logger.debug("Database shut down. cleaned count={} logs", logsCleaned);

        tripleIndex = null;
        tripleStore = null;
        environment = null;
    }

    /**
     * persist data to the store. for now assume all statements are strings
     *
     * @param triple  the triple to be persisted
     */
    @Override
    public void put(Triple triple) {
        Transaction txn = environment.beginTransaction(null, null);
        try {
            final DatabaseEntry key = new DatabaseEntry(triple.getSubject());
            final DatabaseEntry data = new DatabaseEntry();

            Statement statement = triple.getStatement();
            if (statement instanceof BinaryStatement) {
                data.setData(((BinaryStatement) statement).getBinary());
                binaryStore.put(txn, key, data);
            } else {
                // TODO - rubbish
                stringTripleBinding.objectToEntry(triple, data);
                tripleStore.put(txn, key, data);
            }
        } catch (RuntimeException e) {
            logger.error("Error in transaction. id={}, message={}", txn.getId(), e.getMessage());
            txn.abort();
        }
    }

    @Override
    public void put(Bundle bundle) {
        Transaction txn = environment.beginTransaction(null, null);
        try (Cursor cursor = tripleStore.openCursor(txn, null)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            for (Statement statement : bundle.getStatements()) {
                if (statement instanceof BinaryStatement) {
                    final DatabaseEntry key = new DatabaseEntry(bundle.getSubject());
                    final DatabaseEntry data = new DatabaseEntry();

                    data.setData(((BinaryStatement) statement).getBinary());
                    binaryStore.put(txn, key, data);
                } else {
                    // TODO - rubbish
                    Statement<String> stringStatement = (Statement) statement;
                    final DatabaseEntry key = new DatabaseEntry();
                    final DatabaseEntry data = new DatabaseEntry();

                    digest.reset();
                    // the subject
                    digest.update(bundle.getSubject());
                    // the predicate
                    digest.update(stringStatement.getPredicate().getBytes());
                    // the object
                    digest.update(stringStatement.getObject().getBytes());
                    key.setData(digest.digest());

                    Triple<String> stringTriple = new Triple<>(bundle.getSubject(), stringStatement);
                    stringTripleBinding.objectToEntry(stringTriple, data);
                    cursor.put(key, data);
                }
            }
            cursor.close();
            txn.commit();
        } catch (RuntimeException e) {
            logger.error("Error in transaction. tx_id={}, message={}", txn.getId(), e.getMessage());
            txn.abort();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error, SHA-256 not available on platform");
            txn.abort();
        }
    }

    @Override
    public void deleteBySubjectId(byte[] subjectId) {
        Transaction txn = environment.beginTransaction(null, null);
        try {
            binaryStore.delete(txn, new DatabaseEntry(subjectId));

            try (Cursor cursor = tripleStore.openCursor(txn, null)) {
                DatabaseEntry key = new DatabaseEntry(subjectId);
                DatabaseEntry ignoredData = new DatabaseEntry();

                cursor.getSearchKey(key, ignoredData, LockMode.DEFAULT);
                logger.debug("Deleting {} entries for key={}", cursor.count(), key);
                cursor.delete();
                cursor.close();
            }
            txn.commit();
        } catch (RuntimeException e) {
            logger.error("Error in transaction. td_id={}, message={}", txn.getId(), e.getMessage());
            txn.abort();
        }
    }

    @Override
    public List<Bundle> findByTriples(List<Triple> triples) {
        ArrayList<Bundle> bundles = new ArrayList<>();
        for (Triple triple : triples) {
            bundles.add(getBundleForSubjectId(null, triple.getSubject()));
        }
        return bundles;
    }


    @Override
    public List<Bundle> findByHash(List<byte[]> searchKeys) {
        Transaction txn = environment.beginTransaction(null, null);
        ArrayList<Bundle> bundles = new ArrayList<>();
        ArrayList<SecondaryCursor> cursors = new ArrayList<>();
        DatabaseEntry foundData = new DatabaseEntry();
        try {
            // get a cursor for each search key and check if any data is found
            OperationStatus operationStatus = OperationStatus.NOTFOUND;
            for (byte[] searchKey : searchKeys) {
                // open a cursor for each key
                SecondaryCursor openCursor = tripleNGramIndex.openCursor(txn, null);
                cursors.add(openCursor);
                operationStatus = openCursor.getSearchKey(new DatabaseEntry(searchKey), foundData, LockMode.DEFAULT);
                logger.debug("Operation status={}", operationStatus);
                if (!OperationStatus.SUCCESS.equals(operationStatus)) {
                    break; // forget it, a key was not found. No use going further
                }
            }
            // was anything found?
            if (OperationStatus.SUCCESS.equals(operationStatus)) {
                try (JoinCursor join = tripleStore.join(cursors.toArray(new SecondaryCursor[cursors.size()]), null)) {
                    DatabaseEntry key = new DatabaseEntry();
                    while (join.getNext(key, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                        Triple<String> foundTriple = stringTripleBinding.entryToObject(foundData);
                        Bundle bundleForSubjectId = getBundleForSubjectId(txn, foundTriple.getSubject());
                        bundles.add(bundleForSubjectId);
                        logger.debug("Added bundle={}", bundleForSubjectId);
                    }
                }
            }
        } finally {
            cursors.forEach(Cursor::close);
        }
        return bundles;
    }

    private Bundle getBundleForSubjectId(Transaction txn, final byte[] subjectId) {
        boolean autoCommit = false;
        if (txn == null) {
            txn = environment.beginTransaction(null, null);
            autoCommit = true;
        }
        try (SecondaryCursor cursor = tripleIndex.openCursor(txn, null)) {
            BundleBuilder bundleBuilder = Bundle.builder();
            bundleBuilder.withSubject(subjectId);
            DatabaseEntry key = new DatabaseEntry(subjectId);
            DatabaseEntry foundData = new DatabaseEntry();

            OperationStatus operationStatus = cursor.getSearchKey(key, foundData, LockMode.DEFAULT);
            if (!OperationStatus.SUCCESS.equals(operationStatus)) return Bundle.emptyBundle();

            while (operationStatus == OperationStatus.SUCCESS) {
                addStatement(foundData, bundleBuilder);
                operationStatus = cursor.getNextDup(key, foundData, LockMode.DEFAULT);
            }
            Bundle bundle = bundleBuilder.build();
            logger.debug(bundle.toString());
            return bundle;
        } catch (Exception e) {
            logger.error("Error occurred while reading database. tx_id={}, message={}", txn.getId(), e.getMessage());
            return Bundle.emptyBundle();
        } finally {
            if (autoCommit) txn.commit();
        }
    }

    public List<Bundle> getAll() {
        ArrayList<Bundle> bundles = new ArrayList<>();

        Transaction txn = environment.beginTransaction(null, null);
        try (SecondaryCursor cursor = tripleIndex.openCursor(txn, null)) {

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();

            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                BundleBuilder bundleBuilder = Bundle.builder();
                bundleBuilder.withSubject(foundKey.getData());
                addStatement(foundData, bundleBuilder);

                while (cursor.getNextDup(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                    addStatement(foundData, bundleBuilder);
                }
                Bundle bundle = bundleBuilder.build();
                logger.debug(bundle.toString());
                bundles.add(bundle);
            }
        }  catch (Exception e) {
            logger.error("Error occurred while reading database. tx_id={}, message={}", txn.getId(), e.getMessage());
        }
        return bundles;
    }

    private void addStatement(DatabaseEntry data, BundleBuilder bundleBuilder) {
        // TODO - rubbish
        Triple<String> triple = stringTripleBinding.entryToObject(data);
        Statement<String> statement = triple.getStatement();
        bundleBuilder.withStatement(statement);
        logger.debug("Adding {}", statement);
    }


}
