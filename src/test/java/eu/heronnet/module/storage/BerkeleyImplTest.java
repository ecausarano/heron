package eu.heronnet.module.storage;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import eu.heronnet.core.model.Document;
import eu.heronnet.core.model.DocumentBuilder;

/**
 * @author edoardocausarano
 */
public class BerkeleyImplTest {

    private BerkeleyImpl persistence;

    @BeforeMethod
    public void setUp() throws Exception {

        Properties properties = new Properties();
        properties.put(EnvironmentConfig.LOG_MEM_ONLY, "true");
        EnvironmentConfig configuration = new EnvironmentConfig(properties);
        configuration.setAllowCreate(true);
        configuration.setTransactional(true);
        Environment environment = new Environment(new File("/tmp"), configuration);

        persistence = new BerkeleyImpl(environment);
        persistence.startUp();
    }

    @AfterMethod
    public void cleanup() throws Exception {
        persistence.shutDown();
    }

    @Test
    public void testStore() throws NoSuchAlgorithmException {

        Document document = createDocument();
        persistence.put(document);

    }

    @Test
    public void testFetchBySecondary() throws Exception {

        List<Document> documents = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            Document document = createDocument();
            documents.add(document);
            persistence.put(document);
        }

        List<Document> foundDocuments = persistence.findByStringKey(Collections.singletonList("Edoa"));
        Assert.assertEquals(100, foundDocuments.size());
    }

    @Test(enabled = false)
    public void testFetchMany() throws Exception {

        Random random = new Random();
        byte[] randomData = new byte[4096];
        List<Document> documents = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            random.nextBytes(randomData);
            DocumentBuilder documentBuilder = DocumentBuilder.newInstance();
            Document document = documentBuilder.withBinary(randomData).withField("title", "Random Stuff" + i).withField("author", "Eddy").withField("type", "txt").build();
            documents.add(document);
        }
    }

    private Document createDocument() throws NoSuchAlgorithmException {
        Random random = new Random();
        byte[] randomData = new byte[4096];
        random.nextBytes(randomData);

        DocumentBuilder documentBuilder = DocumentBuilder.newInstance();
        return documentBuilder.withBinary(randomData).withField("title", "Random Stuff").withField("author", "Edoardo Causarano").withField("type", "text/plain").build();
    }

}