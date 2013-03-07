package eu.heronnet.core.command;

import com.google.common.eventbus.EventBus;
import eu.heronnet.core.module.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * This file is part of heron
 * Copyright (C) 2013-2013 edoardocausarano
 * <p/>
 * heron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * heron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Exit implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Exit.class);

    private static final String KEY = "EXIT";

    @Inject
    private UI cli;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void execute() {
        cli.stop();
        logger.debug("called {}", KEY);
    }

    @Override
    public void setArgs(String... varargs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Inject
    private EventBus eventBus;
}
