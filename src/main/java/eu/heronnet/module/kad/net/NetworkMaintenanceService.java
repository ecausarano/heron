package eu.heronnet.module.kad.net;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AbstractScheduledService;

import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.kad.model.rpc.message.PingRequest;

/**
 * @author edoardocausarano
 */
@Component
public class NetworkMaintenanceService extends AbstractScheduledService {

    private static final Logger logger = LoggerFactory.getLogger(NetworkMaintenanceService.class);

    @Inject
    private Client client;

    @Inject
    @Named("self")
    private Node self;

    @Override
    protected void runOneIteration() throws Exception {
        logger.debug("running network maintenance at={}", new Date());
        PingRequest pingRequest = new PingRequest();
        pingRequest.setOrigin(self);

        client.broadcast(pingRequest);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(15, 5, TimeUnit.SECONDS);
    }
}
