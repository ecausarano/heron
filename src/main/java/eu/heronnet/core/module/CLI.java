package eu.heronnet.core.module;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import eu.heronnet.core.command.Command;
import eu.heronnet.core.command.Invoker;
import jline.console.ConsoleReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

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
public class CLI extends AbstractExecutionThreadService implements UI {

    private static final Logger logger = LoggerFactory.getLogger(CLI.class);

    // TODO - get rid of this crap, commands should register themselves.

    // This class should query the guice context for the list
    private static final List<String> COMMANDS = Arrays.asList(
            "PUT", "GET", "FIND", "EXIT", "JOIN", "PING");
    private List<Command> history = new ArrayList<Command>();

    private boolean run = true;

    @Inject
    private Invoker invoker = null;

    @Inject
    private Injector injector = null;
    private ConsoleReader console;

    @Override
    protected void triggerShutdown() {
        super.triggerShutdown();
        logger.debug("Shutting down the CLI module");
    }

    @Override
    protected void startUp() throws Exception {
        console = new ConsoleReader();
    }

    @Override
    protected void run() throws Exception {
        while (isRunning()) {
            final String line = console.readLine("Heron:\t");
            StringTokenizer stringTokenizer = new StringTokenizer(line);

            while (stringTokenizer.hasMoreTokens()) {
                String key = stringTokenizer.nextToken();
                if (key != null && COMMANDS.contains(key.toUpperCase())) {
                    key = key.toUpperCase();
                    Command command = injector.getInstance(
                            Key.get(Command.class, Names.named(key)));
                    // tutta merda... ma voglio vede' se va...
                    List<String> varargs = new ArrayList<String>();
                    while (stringTokenizer.hasMoreTokens())
                        varargs.add(
                                stringTokenizer.nextToken()
                        );
                    // porcheria
                    command.setArgs(varargs.toArray(new String[]{}));
                    history.add(command);
                    invoker.dispatch(command);

                }
            }
            console.accept();
        }
    }

    @Inject
    private EventBus eventBus;
}
