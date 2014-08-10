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

package eu.heronnet.core.module;

import com.google.common.util.concurrent.AbstractIdleService;
import eu.heronnet.core.module.gui.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.swing.*;


public class GUI extends AbstractIdleService implements UI {

    private static final Logger logger = LoggerFactory.getLogger(GUI.class);

    @Inject
    private MainWindow mainWindow;

    @Override
    protected void startUp() throws Exception {
        logger.debug("Starting up GUI component service");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                mainWindow.init();
            }
        });
    }

    @Override
    protected void shutDown() throws Exception {

    }

    public MainWindow getMainWindow() {
        return mainWindow;
    }


}
