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

package eu.heronnet.core.module.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class MainWindow {

    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    private JTabbedPane tabbedPane;

    private JTextField searchText;
    private JButton searchButton;
    private JTable resultsTable;
    private JTable localItemsTable;
    private JFrame mainWindow;
    private JMenuBar menuBar;

    @Inject
    private MainWindowDelegate delegate;

    private JMenuItem addFileMenuItem;
    private JFileChooser addFileChooser;

    public MainWindow() {
        logger.debug("ctor {}", this);
    }

    public void init() {
        createUIComponents();
        setEventHandlers();
    }

    public void createUIComponents() {

        mainWindow = new JFrame();
        mainWindow.setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        mainWindow.getContentPane().add(tabbedPane);

        JPanel searchPanel = new JPanel();
        JPanel localStorage = new JPanel();
        JPanel identityPanel = new JPanel();

        tabbedPane.addTab("Search Panel", searchPanel);
        tabbedPane.addTab("Local Storage", localStorage);
        tabbedPane.addTab("Identity", identityPanel);

        searchText = new JTextField("search terms...");
        searchText.setName("searchText");
        searchPanel.add(searchText);

        searchButton = new JButton("Search");
        searchButton.setName("searchButton");
        searchPanel.add(searchButton);

        resultsTable = new JTable();
        JScrollPane resultsTableScrollPane = new JScrollPane(resultsTable);
        resultsTable.setFillsViewportHeight(true);
        searchPanel.add(resultsTableScrollPane);

        localItemsTable = new JTable();
        localItemsTable.setModel(delegate.getResultsTable());
        JScrollPane localItemsTableScrollPane = new JScrollPane(localItemsTable);
        localItemsTable.setFillsViewportHeight(true);
        localStorage.add(localItemsTableScrollPane);

        createMenu();
        mainWindow.setJMenuBar(menuBar);

        mainWindow.pack();
        mainWindow.setVisible(true);
    }

    private void createMenu() {
        menuBar = new JMenuBar();

        final JMenu fileMenu = new JMenu("File");

        addFileMenuItem = new JMenuItem("Add...");
        addFileChooser = new JFileChooser();
        fileMenu.add(addFileMenuItem);

        menuBar.add(fileMenu);
    }

    private void setEventHandlers() {
        searchButton.addActionListener(delegate);
        searchText.getDocument().addDocumentListener(delegate);
        addFileMenuItem.addActionListener(new ActionListener() {
            {
                addFileChooser.addActionListener(delegate);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                addFileChooser.showOpenDialog(mainWindow);
            }
        });
    }

    public MainWindowDelegate getDelegate() {
        return delegate;
    }
}
