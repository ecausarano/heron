package eu.heronnet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.heronnet.core.cli.CLI;
import eu.heronnet.core.dht.Tables;
import il.technion.ewolf.dht.DHT;
import il.technion.ewolf.dht.DHTStorage;
import il.technion.ewolf.dht.SimpleDHTModule;
import il.technion.ewolf.dht.storage.AgeLimitedDHTStorage;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.openkad.KadNetModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of Heron
 * Copyright (C) 2013 Edoardo Causarano
 * <p/>
 * Heron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * Heron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
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
        * therefore they are indexes. If Tables does not allow for * queries, a root archetype is necessary.
        *
        * (we need a meta Tables to track everything and add stuff manually)
        *
        * query the predefined "archetype" Tables to bootstrap the archetype table
        * archetypes such as version, user, data
        *
        * archetype -> A_UUID, name (the data)
        *
        * hit the prototype Tables to bootstrap known prototypes, and classify them by archetype
        *
        * prototype -> P_UUID, A_UUID, name (the data)
        *
        */

public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        // TODO - jline CLI
        //
        // commands to :bootstrap network
        // debug monitors: listen for messages (possibly filtered by tag)
        // manipulate commands: store index, search, fetch, add/delete DHTs
        // start with ARCHETYPE, PROTOTYPE

        Tables tables = new Tables();
        tables.init();
        tables.start();

        CLI cli = new CLI();
        cli.init();
        cli.start();

    }

}
