package eu.heronnet.core.command;

import com.google.inject.Inject;
import eu.heronnet.core.module.network.dht.DHTService;
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
public class Ping implements Command {

    private static final String KEY = "PING";
    private final Logger logger = LoggerFactory.getLogger(Ping.class);

    @Inject
    private DHTService dhtService;


    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void execute() {
        logger.debug("Called PING command");
        dhtService.ping();
    }

    @Override
    public void setArgs(final String... varargs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
