package eu.heronnet.module.storage.rdf;

import com.google.common.util.concurrent.AbstractIdleService;
import com.sleepycat.je.*;
import eu.heronnet.model.Bundle;
import eu.heronnet.model.IdentifierNode;
import eu.heronnet.model.Node;
import eu.heronnet.module.storage.Persistence;
import eu.heronnet.module.storage.binding.BundleBinding;
import eu.heronnet.module.storage.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.sleepycat.je.LockMode.DEFAULT;
import static com.sleepycat.je.OperationStatus.NOTFOUND;
import static com.sleepycat.je.OperationStatus.SUCCESS;

/**
 * @author edoardocausarano
 */
@Component(value = "localStorage")
public class TripleStoreImplV2 extends AbstractIdleService implements Persistence {

    private static final String BUNDLE_STORE = "BundleStore";
    private static final String NODE_ID_INDEX = "NodeIdIndex";
    private static final String SUBJECT_ID_INDEX = "SubjectIdIndex";
    private static final String PREDICATE_INDEX = "PredicateIndex";
    private static final String NGRAM_INDEX = "NgramIndex";
    private static final Logger logger = LoggerFactory.getLogger(TripleStoreImplV2.class);

    @Inject private Environment environment;
    @Inject private DatabaseConfig databaseConfig;
    @Inject private SecondaryConfig subjectIdIndexConfig;
    @Inject private SecondaryConfig predicateIdIndexConfig;
    @Inject private SecondaryConfig stringObjectNgramIndexConfig;

    private Database bundleStore;
    private SecondaryDatabase predicateIdIndex;
    private SecondaryDatabase subjectIdIndex;
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
            subjectIdIndex = environment.openSecondaryDatabase(txn, SUBJECT_ID_INDEX, bundleStore, subjectIdIndexConfig);
            predicateIdIndex = environment.openSecondaryDatabase(txn, PREDICATE_INDEX, bundleStore, predicateIdIndexConfig);
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

        subjectIdIndex.close();
        predicateIdIndex.close();
        stringObjectNgramIndex.close();
        bundleStore.close();

        int logsCleaned = environment.cleanLog();
        environment.close();
        logger.debug("Database shut down. cleaned count={} logs", logsCleaned);

        bundleStore = null;
        environment = null;
    }

    /**
     * persist data to the store.
     *
     * @param bundle  the rawBundle to be persisted
     */
    @Override
    public void put(Bundle bundle) {
        Transaction txn = environment.beginTransaction(null, null);
        try {
            final DatabaseEntry key = new DatabaseEntry(bundle.getNodeId());
            final DatabaseEntry data = new DatabaseEntry();

            bundleBinding.objectToEntry(bundle, data);
            bundleStore.put(txn, key, data);
            txn.commit();
        } catch (RuntimeException e) {
            logger.error("Error in transaction. id={}, message={}", txn.getId(), e.getMessage());
            txn.abort();
        }
    }

    /**
     * Delete all items related to the given subjectId
     *
     * @param subjectId of bundles that need to be deleted
     */
    @Override
    public void deleteBySubjectId(byte[] subjectId) {
        Transaction txn = environment.beginTransaction(null, null);
        try (SecondaryCursor cursor = subjectIdIndex.openCursor(txn, null)) {
            while (cursor.getNext(new DatabaseEntry(), new DatabaseEntry(), DEFAULT) == SUCCESS) {
                cursor.delete();
            }
            txn.commit();
        } catch (RuntimeException e) {
            logger.error("Error in transaction. td_id={}, message={}", txn.getId(), e.getMessage());
            txn.abort();
        }
    }

    @Override
    public List<Bundle> findByPredicate(List<? extends Node> fields) {
        return findByHash(fields.stream().map(Node::getNodeId).collect(Collectors.toList()));
    }


    /**
     * Fetches all {@link Bundle bundles} related to the given search keys (SHA-256 hashes),
     * including bundles having the same {@link IdentifierNode} as those containing the keys
     * (typically {@code Bundles} containing additional metadata related to PGP signatures)
     *
     * @param searchKeys the SHA-256 hashes of the keys to search for
     * @return the bundles found
     */
    @Override
    public List<Bundle> findByHash(List<byte[]> searchKeys) {
        Transaction txn = environment.beginTransaction(null, null);
        ArrayList<SecondaryCursor> cursors = new ArrayList<>();
        DatabaseEntry foundData = new DatabaseEntry();
        try {
            OperationStatus subjectsStatus = NOTFOUND;
            OperationStatus predicatesStatus = NOTFOUND;
            OperationStatus ngramsStatus = NOTFOUND;
            // get a cursor for each search key and check if any data is found
            for (byte[] searchKey : searchKeys) {
                // open a cursor for each key
                final SecondaryCursor subjectIdCursor = subjectIdIndex.openCursor(txn, null);
                final SecondaryCursor predicateIndexCursor = predicateIdIndex.openCursor(txn, null);
                final SecondaryCursor ngramIndexCursor = stringObjectNgramIndex.openCursor(txn, null);

                subjectsStatus = subjectIdCursor.getSearchKey(new DatabaseEntry(searchKey), foundData, LockMode.DEFAULT);
                predicatesStatus = predicateIndexCursor.getSearchKey(new DatabaseEntry(searchKey), foundData, LockMode.DEFAULT);
                ngramsStatus = ngramIndexCursor.getSearchKey(new DatabaseEntry(searchKey), foundData, LockMode.DEFAULT);

                if (SUCCESS.equals(subjectsStatus)) {
                    cursors.add(subjectIdCursor);
                } else {
                    subjectIdCursor.close();
                }
                if (SUCCESS.equals(predicatesStatus)) {
                    cursors.add(predicateIndexCursor);
                } else {
                    predicateIndexCursor.close();
                }

                if (SUCCESS.equals(ngramsStatus)) {
                    cursors.add(ngramIndexCursor);
                } else {
                    ngramIndexCursor.close();
                }
                logger.debug("Operation status for indexes s={}, p={}, o(ng)={}", ngramsStatus, predicatesStatus, ngramsStatus);

                if (cursors.size() == 0) {
                    break;
                }
            }
            // was anything found?
            if (SUCCESS.equals(subjectsStatus) || SUCCESS.equals(predicatesStatus) || SUCCESS.equals(ngramsStatus)) {
                List<Bundle> foundBundles = new ArrayList<>();

                try (JoinCursor join = bundleStore.join(cursors.toArray(new SecondaryCursor[cursors.size()]), null)) {
                    DatabaseEntry key = new DatabaseEntry();
                    while (join.getNext(key, foundData, DEFAULT) == SUCCESS) {
                        Bundle foundBundle = bundleBinding.entryToObject(foundData);
                        // this is a bundle directly related to the search keys
                        byte[] foundSubjectId = foundBundle.getSubject().getNodeId();
                        // TODO - not very efficient, we're re-fetching everything a second time
                        foundBundles.addAll(getBundlesForSubjectId(txn, foundSubjectId));
                        logger.debug("Added bundle={}", HexUtil.bytesToHex(foundBundle.getSubject().getNodeId()));
                    }
                }
                return foundBundles;
            }
        } finally {
            cursors.forEach(Cursor::close);
            txn.commit();
        }
        return Collections.emptyList();
    }

    private Set<Bundle> getBundlesForSubjectId(Transaction txn, final byte[] subjectId) {
        try (SecondaryCursor cursor = subjectIdIndex.openCursor(txn, null)) {
            HashSet<Bundle> bundles = new HashSet<>();
            DatabaseEntry secondaryKey = new DatabaseEntry(subjectId);
            DatabaseEntry foundData = new DatabaseEntry();

            OperationStatus status = cursor.getSearchKey(secondaryKey, foundData, DEFAULT);
            while (SUCCESS.equals(status)) {
                bundles.add(bundleBinding.entryToObject(foundData));
                status = cursor.getNextDup(secondaryKey, foundData, DEFAULT);
            }
            return bundles;
        } catch (Exception e) {
            logger.error("Error occurred while reading database. tx_id={}, message={}", txn.getId(), e.getMessage());
            return Collections.emptySet();
        }
    }

    @Override
    public List<Bundle> getAll() {
        Transaction txn = environment.beginTransaction(null, null);
        try (Cursor cursor = bundleStore.openCursor(txn, null)) {
            ArrayList<Bundle> bundles= new ArrayList<>();
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            while (SUCCESS.equals(cursor.getNext(key, value, DEFAULT))) {
                Bundle bundle = bundleBinding.entryToObject(value);
                bundles.add(bundle);
            }
            return bundles;
        } catch (Exception e) {
            logger.error("Error occurred while reading database. tx_id={}, message={}", txn.getId(), e.getMessage());
            return Collections.emptyList();
        } finally {
            txn.commit();
        }
    }
}
