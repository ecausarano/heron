package eu.heronnet.module.kad.net;

import javax.inject.Inject;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.AbstractScheduledService;
import eu.heronnet.module.storage.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author edoardocausarano
 */
@Component
public class NetworkMaintenanceService extends AbstractScheduledService {

    private static final Logger logger = LoggerFactory.getLogger(NetworkMaintenanceService.class);

    @Inject
    private Persistence clientImpl;

    @Inject
    private SelfNodeProvider selfNodeProvider;

    @Override
    protected void runOneIteration() throws Exception {
        final Date when = new Date();
        logger.debug("running network maintenance at={}", when);

//        PingRequest pingRequest = new PingRequest(selfNodeProvider.getSelf());

//        clientImpl.broadcast(pingRequest);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(15, 5, TimeUnit.MINUTES);
    }
}
