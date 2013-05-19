package eu.heronnet.core.module.network.dht;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This file is part of heron Copyright (C) 2013-2013 edoardocausarano
 * <p/>
 * heron is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p/>
 * heron is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with Foobar.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
public class KadServiceImpl extends Service {

    private static Logger logger = LoggerFactory.getLogger(KadServiceImpl.class);

//    private KeybasedRouting kbr = null;
//    private DHTStorage storage = null;
//
//    List<URI> netBootStrap = null;
//    private DHT dht;

    @Override
    public void run() {
//        Injector injector = Guice.createInjector(
//                new KadNetModule().setProperty("openkad.net.udp.port", "5555"),
//                new SimpleDHTModule()
//        );
//        kbr = injector.getInstance(KeybasedRouting.class);
//        try {
//            kbr.create();
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//
//        storage = injector.getInstance(AgeLimitedDHTStorage.class).create();
//
//        dht = injector.getInstance(DHT.class)
//                .setName("schema")
//                .setStorage(storage)
//                .create();
    }

    @Override
    public void put(Serializable data, UUID uuid) {
//        dht.put(data, uuid.toString());
    }

    @Override
    public List<Serializable> get(String UUID) {
//        return dht.get(UUID);
        return null;
    }

//    public KeybasedRouting getKbr() {
//        return kbr;
//    }

}
