package eu.heronnet.module.bus.handler;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import eu.heronnet.module.bus.command.Get;
import eu.heronnet.module.network.dht.DHTService;

/**
 * @author edoardocausarano
 */
public class GetHandler {

    private static final Logger logger = LoggerFactory.getLogger(GetHandler.class);

    @Inject
    private EventBus eventBus;

    @Inject
    private DHTService dhtService;

    @Subscribe
    public void handle(Get command) {
        logger.debug("getById={}", command.getId());
        Map<String, byte[]> result = dhtService.findByID(command.getId());
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
        eventBus.register(this);
    }

}
