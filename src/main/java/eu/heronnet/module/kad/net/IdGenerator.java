package eu.heronnet.module.kad.net;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author edoardocausarano
 */
@Component
public class IdGenerator {

    private final Random random = new Random();

    public byte[] getId() {
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return bytes;
    }
}
