package eu.heronnet.module.bus.handler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.heronnet.module.bus.command.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author edoardocausarano
 */
public class GetHandler {

    private static final Logger logger = LoggerFactory.getLogger(GetHandler.class);

    @Inject
    @Qualifier(value = "mainBus")
    private EventBus mainBus;

    @Subscribe
    public void handle(Get command) {
        logger.debug("getById={}", command.getId());
        //
        // try {
        // File tempFile = new File(file);
        // final RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rw");
        // final FileChannel fileChannel = randomAccessFile.getChannel();
        // // fileChannel.write(ByteBuffer.wrap(result.getData()));
        // fileChannel.close();
        // } catch (IOException e) {
        // logger.error(e.getMessage());
        // }
        // }
    }

    @PostConstruct
    void register() {
        mainBus.register(this);
    }

}
