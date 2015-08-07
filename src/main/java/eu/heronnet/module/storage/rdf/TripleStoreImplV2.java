package eu.heronnet.module.storage.rdf;

import com.google.common.util.concurrent.AbstractIdleService;
import com.sleepycat.je.*;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.Node;
import eu.heronnet.model.StringNode;
import eu.heronnet.module.storage.Persistence;
import eu.heronnet.module.storage.binding.BundleBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author edoardocausarano
 */
@Component
public class TripleStoreImplV2 extends AbstractIdleService implements Persistence {

    private static final String BUNDLE_STORE = "BundleStore";
    private static final String NGRAM_INDEX = "NgramIndex";
    private static final String NODE_ID_INDEX = "NodeIdIndex";
    private static final Logger logger = LoggerFactory.getLogger(TripleStoreImplV2.class);

    @Inject private Environment environment;
    @Inject private DatabaseConfig databaseConfig;
    @Inject private SecondaryConfig nodeIdIndexConfig;
    @Inject private SecondaryConfig stringObjectNgramIndexConfig;

    private Database bundleStore;
    private SecondaryDatabase nodeIdIndex;
    private SecondaryDatabase stringObjectNgramIndex;
    private BundleBinding bundleBinding = new BundleBinding();


    public TripleStoreImplV2() {
    }

    public TripleStoreImplV2(Environment environment) {
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
            bundleStore = environment.openDatabase(txn, BUNDLE_STORE, databaseConfig);
            nodeIdIndex = environment.openSecondaryDatabase(txn, NODE_ID_INDEX, bundleStore, nodeIdIndexConfig);
            stringObjectNgramIndex = environment.openSecondaryDatabase(txn, NGRAM_INDEX, bundleStore, stringObjectNgramIndexConfig);
            txn.commit();
        } catch (RuntimeException e) {
            logger.error("error while opening databases, message={}", e.getMessage());
            txn.abort();
        }

        logger.debug("Opened {}, count={}", BUNDLE_STORE, bundleStore.count());
    }


    /**
     * Called during application shutdown, performs housekeeping activities
     *
     * @throws Exception
     */
    @Override
    protected void shutDown() throws Exception {

        nodeIdIndex.close();
        bundleStore.close();

        int logsCleaned = environment.cleanLog();
        environment.close();
        logger.debug("Database shut down. cleaned count={} logs", logsCleaned);

        bundleStore = null;
        environment = null;
    }

    /**
     * persist data to the store. for now assume all statements are strings
     *
     * @param bundle  the rawBundle to be persisted
     */
    @Override
    public void put(Bundle bundle) {
        Transaction txn = environment.beginTransaction(null, null);
        try {
            final DatabaseEntry key = new DatabaseEntry(bundle.getSubject().getNodeId());
            final DatabaseEntry data = new DatabaseEntry();

            bundleBinding.objectToEntry(bundle, data);
            bundleStore.put(txn, key, data);
        } catch (RuntimeException e) {
            logger.error("Error in transaction. id={}, message={}", txn.getId(), e.getMessage());
            txn.abort();
        }
    }

//    @Override
//    public void put(RawBundle bundle) {
//        Transaction txn = environment.beginTransaction(null, null);
//        try (Cursor cursor = bundleStore.openCursor(txn, null)) {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//
//            for (RawStatement statement : bundle.getRawStatements()) {
//                if (statement instanceof BinaryStatement) {
//                    final DatabaseEntry key = new DatabaseEntry(bundle.getSubject());
//                    final DatabaseEntry data = new DatabaseEntry();
//
//                    data.setData(((BinaryStatement) statement).getBinary());
//                    nodeStore.put(txn, key, data);
//                } else {
//                    // TODO - rubbish
//                    RawStatement<String> stringStatement = (RawStatement) statement;
//                    final DatabaseEntry key = new DatabaseEntry();
//                    final DatabaseEntry data = new DatabaseEntry();
//
//                    digest.reset();
//                    // the subject
//                    digest.update(bundle.getSubject());
//                    // the predicate
//                    digest.update(stringStatement.getPredicate().getBytes());
//                    // the object
//                    digest.update(stringStatement.getObject().getBytes());
//                    key.setData(digest.digest());
//
//                    Triple<String> stringTriple = new Triple<>(bundle.getSubject(), stringStatement);
//                    stringTripleBinding.objectToEntry(stringTriple, data);
//                    cursor.put(key, data);
//                }
//            }
//            cursor.close();
//            txn.commit();
//        } catch (RuntimeException e) {
//            logger.error("Error in transaction. tx_id={}, message={}", txn.getId(), e.getMessage());
//            txn.abort();
//        } catch (NoSuchAlgorithmException e) {
//            logger.error("Error, SHA-256 not available on platform");
//            txn.abort();
//        }
//    }
//
    @Override
    public void deleteBySubjectId(byte[] subjectId) {
        Transaction txn = environment.beginTransaction(null, null);
        try {
            bundleStore.delete(txn, new DatabaseEntry(subjectId));
        } catch (RuntimeException e) {
            logger.error("Error in transaction. td_id={}, message={}", txn.getId(), e.getMessage());
            txn.abort();
        }
    }

    @Override
    public List<Bundle> findByPredicate(List<StringNode> fields) {
        return findByHash(fields.stream().map(Node::getNodeId).collect(Collectors.toList()));
    }


    @Override
    public List<Bundle> findByHash(List<byte[]> searchKeys) {
//        Transaction txn = environment.beginTransaction(null, null);
//        ArrayList<SecondaryCursor> cursors = new ArrayList<>();
//        DatabaseEntry foundData = new DatabaseEntry();
//        try {
//            // get a cursor for each search key and check if any data is found
//            OperationStatus operationStatus = OperationStatus.NOTFOUND;
//            for (byte[] searchKey : searchKeys) {
//                // open a cursor for each key
//                SecondaryCursor openCursor = tripleNGramIndex.openCursor(txn, null);
//                cursors.add(openCursor);
//                operationStatus = openCursor.getSearchKey(new DatabaseEntry(searchKey), foundData, LockMode.DEFAULT);
//                logger.debug("Operation status={}", operationStatus);
//                if (!OperationStatus.SUCCESS.equals(operationStatus)) {
//                    break; // forget it, a key was not found. No use going further
//                }
//            }
//            // was anything found?
//            if (OperationStatus.SUCCESS.equals(operationStatus)) {
//                try (JoinCursor join = bundleStore.join(cursors.toArray(new SecondaryCursor[cursors.size()]), null)) {
//                    DatabaseEntry key = new DatabaseEntry();
//                    while (join.getNext(key, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
//                        Triple<String> foundTriple = stringTripleBinding.entryToObject(foundData);
//                        RawBundle rawBundleForSubjectId = getBundleForSubjectId(txn, foundTriple.getSubject());
//                        rawBundles.add(rawBundleForSubjectId);
//                        logger.debug("Added bundle={}", rawBundleForSubjectId);
//                    }
//                }
//            }
//        } finally {
//            cursors.forEach(Cursor::close);
//        }
        return Collections.emptyList();
    }

    private Bundle getBundleForSubjectId(Transaction txn, final byte[] subjectId) {

//
//
//        boolean autoCommit = false;
//        if (txn == null) {
//            txn = environment.beginTransaction(null, null);
//            autoCommit = true;
//        }
//        try (SecondaryCursor cursor = tripleIndex.openCursor(txn, null)) {
//            BundleBuilder bundleBuilder = RawBundle.builder();
//            bundleBuilder.withSubject(subjectId);
//            DatabaseEntry key = new DatabaseEntry(subjectId);
//            DatabaseEntry foundData = new DatabaseEntry();
//
//            OperationStatus operationStatus = cursor.getSearchKey(key, foundData, LockMode.DEFAULT);
//            if (!OperationStatus.SUCCESS.equals(operationStatus)) return RawBundle.emptyBundle();
//
//            while (operationStatus == OperationStatus.SUCCESS) {
//                addStatement(foundData, bundleBuilder);
//                operationStatus = cursor.getNextDup(key, foundData, LockMode.DEFAULT);
//            }
//            RawBundle rawBundle = bundleBuilder.build();
//            logger.debug(rawBundle.toString());
//            return rawBundle;
//        } catch (Exception e) {
//            logger.error("Error occurred while reading database. tx_id={}, message={}", txn.getId(), e.getMessage());
//            return RawBundle.emptyBundle();
//        } finally {
//            if (autoCommit) txn.commit();
//        }
        return null;
    }

    @Override
    public List<Bundle> getAll() {
        ArrayList<Bundle> bundles= new ArrayList<>();
        Transaction txn = environment.beginTransaction(null, null);
        try {
            try (Cursor cursor = bundleStore.openCursor(txn, null)) {
                DatabaseEntry key = new DatabaseEntry();
                DatabaseEntry value = new DatabaseEntry();
                OperationStatus operationStatus = cursor.getSearchKey(key, value, LockMode.DEFAULT);
                while (OperationStatus.SUCCESS.equals(operationStatus)) {
                    Bundle bundle = bundleBinding.entryToObject(value);
                    bundles.add(bundle);
                }
            }
            return bundles;
        } catch (Exception e) {
            logger.error("Error occurred while reading database. tx_id={}, message={}", txn.getId(), e.getMessage());
            return Collections.emptyList();
        }
    }
}
