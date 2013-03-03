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
import jline.console.completer.StringsCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
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
    private static final String DEFAULT_PROMPT = "heron> ";

    private ConsoleReader consoleReader = null;
    private List<Command> history = null;

    private boolean run = true;

    @Inject
    private Invoker invoker = null;

    @Inject
    private Injector injector = null;

    public CLI() {
        logger.debug("Creating new CLI object: {}", this);

        try {
            consoleReader = new ConsoleReader();
        } catch (IOException e) {
            logger.error("Error creating CLI");
        }
        consoleReader.setPrompt(DEFAULT_PROMPT);

        // here get List<String>commandKeys...
        consoleReader.addCompleter(
                new StringsCompleter("put", "get", "find", "exit")
        );

        history = new ArrayList<Command>();
    }

    @Override
    public void run() {
        while (run) {
            try {
                String line = consoleReader.readLine(DEFAULT_PROMPT);
                StringTokenizer stringTokenizer = new StringTokenizer(line, "\\s");
                String key = stringTokenizer.nextToken();
                if (key != null) {
                    key = key.toUpperCase();
                    Command command = injector.getInstance(
                            Key.get(Command.class, Names.named(key)));
                    history.add(command);
                    invoker.dispatch(command);
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    protected void triggerShutdown() {
        logger.debug("Shutting down the CLI module");
        run = false;
    }

    @Inject
    private EventBus eventBus;
}
