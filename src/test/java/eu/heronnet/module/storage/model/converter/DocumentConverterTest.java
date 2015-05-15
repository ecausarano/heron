package eu.heronnet.module.storage.model.converter;

import java.util.Random;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import eu.heronnet.core.model.Document;
import eu.heronnet.core.model.DocumentBuilder;
import eu.heronnet.core.model.Field;
import eu.heronnet.module.storage.model.StoredDocument;

/**
 * @author edoardocausarano
 */
public class DocumentConverterTest {

    @Test
    public void testAsStoredDocument() throws Exception {
        Random random = new Random();
        byte[] randomData = new byte[4096];
        random.nextBytes(randomData);

        Field author = new Field("author", "Edoardo Causarano");
        Document document = DocumentBuilder.newInstance().withBinary(randomData).withField(author).build();

        StoredDocument storedDocument = DocumentConverter.asStoredDocument(document);

        Set<String> storedDocumentMeta = storedDocument.getMeta();
        Assert.assertFalse(storedDocumentMeta.isEmpty(), "Generated StoredDocument has empty StoredFields member");

        Assert.assertTrue(storedDocumentMeta.contains(author.getHash()), "Field hash in generated StoreDocument does not match expected value");
    }
}