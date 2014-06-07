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

package eu.heronnet.core.command;

import com.google.inject.Inject;
import eu.heronnet.core.module.network.dht.DHTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Join implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Join.class);

    private static final String key = "join";


    @Inject
    private DHTService dhtService;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void execute() {
    }

    @Override
    public void setArgs(String... varargs) {
//        KeybasedRouting keybasedRouting = dhtService.getKbr();
//        List<URI> uriList = new ArrayList<URI>();
//        for (String arg : varargs) {
//            try {
//                uriList.add(new URI(arg));
//            } catch (URISyntaxException e) {
//                logger.error(e.getMessage());
//            }
//        }
//        keybasedRouting.join(uriList);
    }
}
