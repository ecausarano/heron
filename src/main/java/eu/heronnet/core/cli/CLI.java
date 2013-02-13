package eu.heronnet.core.cli;

import eu.heronnet.core.command.Command;
import jline.console.ConsoleReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
public class CLI {

    private static final String DEFAULT_PROMPT =  "heron> ";
    private static final Logger logger = LoggerFactory.getLogger(CLI.class);

    private Thread thread = null;
    private ConsoleReader consoleReader = null;
    private List<Command> history = null;


    public void init() throws IOException {

        consoleReader = new ConsoleReader();
        consoleReader.setPrompt(DEFAULT_PROMPT);
        history = new ArrayList<Command>();

        thread = new Thread(new Runnable() {

            private boolean run = true;

            @Override
            public void run() {
                String line = null;
                while (run) {
                    try {
                        line = consoleReader.readLine(DEFAULT_PROMPT);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                    Command command = null;
                    history.add(command);
                    if ("exit".equals(line)) {
                        run = false;
                    }
                }
            }
        });
    }

    public void start() {
        thread.start();
    }
}
