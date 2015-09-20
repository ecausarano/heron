package eu.heronnet.module.pgp;

import org.testng.annotations.Test;

/**
 * @author edoardocausarano
 */
public class PGPUtilsTest {

    @Test
    public void generate() throws Exception {
        PGPUtils.createKeys("alice@example.com", "password");
    }

}
