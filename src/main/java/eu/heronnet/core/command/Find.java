package eu.heronnet.core.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class Find implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Find.class);

    private static final String key = "FIND";

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void execute(String... arguments) {
        logger.debug("called {}", key);
    }
}
