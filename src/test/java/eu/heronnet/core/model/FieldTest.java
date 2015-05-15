package eu.heronnet.core.model;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author edoardocausarano
 */
public class FieldTest {

    @Test
    public void testFieldConstructor() {
        Field field = new Field("name", "value");
        Assert.assertNotNull(field.getHash());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFailOnNullName() {
        new Field(null, "value");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFailOnNullValue() {
        new Field("name", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFailOnNullNameAndValue() {
        new Field(null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFailOnEmptyName() {
        new Field("", "value");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFailOnEmptyValue() {
        new Field("name", "");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFailOnEmptyNameAndValue() {
        new Field("", "");
    }

}