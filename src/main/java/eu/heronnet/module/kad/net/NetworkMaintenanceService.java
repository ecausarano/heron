package eu.heronnet.module.kad.net;

import javax.inject.Inject;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author edoardocausarano
 */
@Component
public class NetworkMaintenanceService extends AbstractScheduledService {

    private static final Logger logger = LoggerFactory.getLogger(NetworkMaintenanceService.class);

    @Inject
    @Qualifier(value = "distributedStorage")
    private ClientImpl clientImpl;

    @Inject
    private SelfNodeProvider selfNodeProvider;

    @Override
    protected void runOneIteration() throws Exception {
        final Date when = new Date();
        logger.debug("running network maintenance at={}", when);
        clientImpl.broadcast();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(1, 5, TimeUnit.SECONDS);
    }
}
