package eu.heronnet;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import eu.heronnet.core.command.*;
import eu.heronnet.core.module.network.simple.NettyClientImpl;
import eu.heronnet.core.module.storage.DBStorage;
import eu.heronnet.core.module.storage.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.heronnet.core.command.Ping;
import eu.heronnet.core.application.bus.EventBusProvider;
import eu.heronnet.core.module.CLI;
import eu.heronnet.core.module.UI;
import eu.heronnet.core.module.network.dht.DHTService;
import eu.heronnet.core.module.network.simple.NettyServerImpl;

/**
 * This file is part of Heron Copyright (C) 2013 Edoardo Causarano
 * <p/>
 * Heron is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p/>
 * Heron is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with Foobar.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

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

    /*
    serialization framework options:

    https://code.google.com/p/kryo/#Quickstart
    http://avro.apache.org/docs/current/
     */
public class Main extends AbstractModule {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        // TODO - jline CLI
        //
        // commands to :bootstrap network
        // debug monitors: listen for messages (possibly filtered by tag)
        // manipulate commands: store index, search, fetch, add/delete DHTs
        // start with ARCHETYPE, PROTOTYPE

        // http://spin.atomicobject.com/2012/01/13/the-guava-eventbus-on-guice/

        final Injector injector = Guice.createInjector(
                new Main()
        );

        Service cli = injector.getInstance(UI.class);
        cli.start();

        Service dht = injector.getInstance(DHTService.class);
        dht.start();

        Service server = injector.getInstance(NettyServerImpl.class);
        server.start();
    }

    @Override
    protected void configure() {

        bind(EventBus.class).toProvider(EventBusProvider.class);
        bind(UI.class).to(CLI.class).in(Scopes.SINGLETON);

        DHTService kadServiceImpl = new NettyClientImpl();
        bind(DHTService.class).toInstance(kadServiceImpl);

        NettyServerImpl server = new NettyServerImpl();
        bind(NettyServerImpl.class).toInstance(server);

        Persistence storage = new DBStorage();
        bind(Persistence.class).toInstance(storage);

        bind(Command.class).annotatedWith(Names.named("EXIT")).to(Exit.class);
        bind(Command.class).annotatedWith(Names.named("FIND")).to(Find.class);
        bind(Command.class).annotatedWith(Names.named("GET")).to(Get.class);
        bind(Command.class).annotatedWith(Names.named("PUT")).to(Put.class);
        bind(Command.class).annotatedWith(Names.named("JOIN")).to(Join.class);
        bind(Command.class).annotatedWith(Names.named("PING")).to(Ping.class);

    }
}
