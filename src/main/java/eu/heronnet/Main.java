package eu.heronnet;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/*
 * The Heron network bootstraps from the "archetype" table where <em>fundamental</em> UUIDs are given a name;
 * these names are important as they describe archetypal concepts that exists before Heron is bootstrapped
 *
 * archetype UUIDs are used to define prototypes
 * prototype UUIDs are used to define prototype fields
 *
 * at which point, we can construct a taxonomy of fundamental archetypes (jeez, I'm being massively obtuse about
 * all this: we want a taxonomy archetype, an archetype_taxonomy prototype and finally archetype_taxonomy instances)
 *
 * then, each client can bootstrap itself by querying the network for the existing fundamental archetypes,
 * and their prototypes.
 *
 * Technically archetypes are the names of the DHTs available on the network, Archetypes are used to run queries,
 * therefore they are indexes. If KadDHTServiceImpl does not allow for * queries, a root archetype is necessary.
 *
 * (we need a meta KadDHTServiceImpl to track everything and add stuff manually)
 *
 * query the predefined "archetype" KadDHTServiceImpl to bootstrap the archetype table
 * archetypes such as version, user, data
 *
 * archetype -> A_UUID, name (the data)
 *
 * hit the prototype KadDHTServiceImpl to bootstrap known prototypes, and classify them by archetype
 *
 * prototype -> P_UUID, A_UUID, name (the data)
 *
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final ApplicationContext applicationContext;

    static {
        applicationContext = new AnnotationConfigApplicationContext("eu.heronnet");
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String[] args) {

        ServiceManager manager = applicationContext.getBean(ServiceManager.class);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    manager.stopAsync().awaitStopped(5, TimeUnit.SECONDS);
                }
                catch (TimeoutException timeout) {
                    logger.error("Failed to stop services");
                }
            }
        });
        try {
            manager.startAsync().awaitHealthy(15, TimeUnit.SECONDS);
        }
        catch (TimeoutException e) {
            logger.error("Failed to start Services within 15 seconds.");
        }
    }
}
