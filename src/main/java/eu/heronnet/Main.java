package eu.heronnet;

import javafx.application.Application;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import eu.heronnet.module.gui.fx.HeronApplication;

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

    private static final ApplicationContext applicationContext;

    static {
        applicationContext = new AnnotationConfigApplicationContext("eu.heronnet");
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String[] args) {
        Application.launch(HeronApplication.class, args);
    }
}
