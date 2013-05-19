package eu.heronnet.core.command;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.heronnet.core.module.network.dht.KadServiceImpl;
import il.technion.ewolf.kbr.KeybasedRouting;

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
public class Join implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Join.class);

    private static final String key = "join";


    @Inject
    private KadServiceImpl dhtService;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void execute() {
    }

    @Override
    public void setArgs(String... varargs) {
        KeybasedRouting keybasedRouting = dhtService.getKbr();
        List<URI> uriList = new ArrayList<URI>();
        for (String arg : varargs) {
            try {
                uriList.add(new URI(arg));
            } catch (URISyntaxException e) {
                logger.error(e.getMessage());
            }
        }
        keybasedRouting.join(uriList);
    }
}
