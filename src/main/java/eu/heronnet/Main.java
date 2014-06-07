/*
 * Copyright (C) 2014 edoardocausarano
 *
 * heron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * heron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with heron. If not, see http://www.gnu.org/licenses
 */

package eu.heronnet;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import eu.heronnet.core.application.bus.EventBusProvider;
import eu.heronnet.core.command.*;
import eu.heronnet.core.module.GUI;
import eu.heronnet.core.module.UI;
import eu.heronnet.core.module.gui.MainWindow;
import eu.heronnet.core.module.gui.MainWindowDelegate;
import eu.heronnet.core.module.network.dht.DHTService;
import eu.heronnet.core.module.network.dht.DHTServiceImpl;
import eu.heronnet.core.module.storage.BerkeleyImpl;
import eu.heronnet.core.module.storage.Persistence;
import eu.heronnet.kad.model.Node;
import eu.heronnet.kad.model.RadixTree;
import eu.heronnet.kad.model.RadixTreeImpl;
import eu.heronnet.kad.model.Self;
import eu.heronnet.kad.net.Client;
import eu.heronnet.kad.net.ClientImpl;
import eu.heronnet.kad.net.ServerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Random;

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

public class Main extends AbstractModule {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        final Injector injector = Guice.createInjector(new Main());
        final HashSet<Service> services = new HashSet<Service>();
        final ServerImpl server = injector.getInstance(ServerImpl.class);
        services.add(server);
        final ClientImpl client = injector.getInstance(ClientImpl.class);
        services.add(client);
        final Service persistence = injector.getInstance(Persistence.class);
        services.add(persistence);
        UI ui = injector.getInstance(UI.class);
        services.add(ui);
        ServiceManager manager = new ServiceManager(services);
        manager.addListener(new ServiceManager.Listener() {
            @Override
            public void failure(Service service) {
                logger.error("bootstrap failure");
            }


            @Override
            public void healthy() {
                logger.debug("all services boostrapped correctly");
            }

            @Override
            public void stopped() {
                logger.debug("all services stopped");
            }
        });

        manager.startAsync();
    }

    @Override
    protected void configure() {

        bind(EventBus.class).toProvider(EventBusProvider.class);

        final Random random = new Random();
        byte[] selfId = new byte[20];
        random.nextBytes(selfId);

        Node self = new Node();
        self.setId(selfId);
        bind(Node.class).annotatedWith(Self.class).toInstance(self);

        RadixTreeImpl network = new RadixTreeImpl();
        network.setSelf(self);

        bind(RadixTree.class).toInstance(network);

        bind(DHTService.class).toInstance(new DHTServiceImpl());

        ServerImpl server = new ServerImpl();
        bind(ServerImpl.class).toInstance(server);

        Client client = new ClientImpl();
        bind(Client.class).toInstance(client);

        Persistence storage = new BerkeleyImpl();
        bind(Persistence.class).toInstance(storage);

        MainWindowDelegate mainWindowDelegate = new MainWindowDelegate();
        bind(MainWindowDelegate.class).toInstance(mainWindowDelegate);

        bind(UI.class).to(GUI.class).in(Scopes.SINGLETON);

        MainWindow mainWindow = new MainWindow();
        bind(MainWindow.class).toInstance(mainWindow);

        bind(Command.class).annotatedWith(Names.named("FIND")).to(Find.class);
        bind(Command.class).annotatedWith(Names.named("GET")).to(Get.class);
        bind(Command.class).annotatedWith(Names.named("PUT")).to(Put.class);
        bind(Command.class).annotatedWith(Names.named("JOIN")).to(Join.class);
        bind(Command.class).annotatedWith(Names.named("PING")).to(Ping.class);
    }
}
