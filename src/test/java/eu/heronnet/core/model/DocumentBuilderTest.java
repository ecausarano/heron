package eu.heronnet.core.model;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author edoardocausarano
 */
public class DocumentBuilderTest {

    private Field field;

    @Test
    public void testBuild() {
        DocumentBuilder documentBuilder = DocumentBuilder.newInstance();

        field = new Field("name", "value");
        documentBuilder.withField(field);

        Random random = new Random();
        byte[] randomBytes = new byte[4096];
        random.nextBytes(randomBytes);
        documentBuilder.withBinary(randomBytes);

        Document document = documentBuilder.build();
        Assert.assertTrue(document.getMeta().contains(field));
        Assert.assertEquals(randomBytes, document.getBinaryData());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testThrowOnNullBinaryAndNoHashSet() {
        DocumentBuilder documentBuilder = DocumentBuilder.newInstance();
        documentBuilder.withField(field);

        documentBuilder.build();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testThrowOnBinaryAndHashBothSet() {
        DocumentBuilder documentBuilder = DocumentBuilder.newInstance();
        documentBuilder.withField(field);

        Random random = new Random();
        byte[] randomBytes = new byte[4096];
        random.nextBytes(randomBytes);
        documentBuilder.withBinary(randomBytes);

        documentBuilder.withHash("some hash");

        documentBuilder.build();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testThrowOnNoFieldSet() {
        DocumentBuilder documentBuilder = DocumentBuilder.newInstance();

        Random random = new Random();
        byte[] randomBytes = new byte[4096];
        random.nextBytes(randomBytes);
        documentBuilder.withBinary(randomBytes);

        documentBuilder.build();

    }
}
