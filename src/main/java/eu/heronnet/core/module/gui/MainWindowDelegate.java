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

import com.google.common.collect.ImmutableList;
import eu.heronnet.core.model.BinaryItem;
import eu.heronnet.core.model.MetadataCollection;
import eu.heronnet.core.module.storage.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindowDelegate implements ActionListener, DocumentListener {

    @Inject
    private Persistence persistence;

    private static final Logger logger = LoggerFactory.getLogger(MainWindowDelegate.class);

    ExecutorService executorService = Executors.newCachedThreadPool();

    private File selectedFile;
    private String searchTerms;

    private AbstractTableModel resultsTable = new AbstractTableModel() {

        private String[] columnNames = {"Filename"};

        @Override
        public int getRowCount() {
            return persistence.fetchAllMedatadaItems().size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final List<MetadataCollection> metadataCollections = persistence.fetchAllMedatadaItems();
            final MetadataCollection item = metadataCollections.get(rowIndex);
            return item.get("name");
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    };

    public MainWindowDelegate() {
        logger.debug("ctor {}", this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        logger.debug("received command {}", actionCommand);

        SwingWorker worker = null;
        switch (actionCommand) {
            case "Search":
                logger.debug("Invoked Search");
                worker = new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        logger.debug("Invoked in background, searching for {}", searchTerms);
                        return null;
                    }
                };
                break;
            case "ApproveSelection":
                JFileChooser source = (JFileChooser) e.getSource();
                selectedFile = source.getSelectedFile();
                worker = new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        logger.debug("Selected file {}", selectedFile);
                        final BinaryItem binaryItem = new BinaryItem();

                        final ByteArrayOutputStream binaryOutputStream = new ByteArrayOutputStream(1048576);
                        byte[] allBytes = Files.readAllBytes(Paths.get(selectedFile.toURI()));
                        binaryItem.setData(allBytes);

                        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
                        messageDigest.reset();
                        messageDigest.update(allBytes);
                        final byte[] digest = messageDigest.digest();
                        binaryItem.setId(digest);

                        final MetadataCollection metadataCollection = new MetadataCollection();
                        metadataCollection.put("filename", selectedFile.getPath());
                        metadataCollection.setReferencedBinary(digest);
                        binaryItem.putMetadataItem(metadataCollection);

                        persistence.persistBinary(binaryItem);
                        persistence.persistMetadata(ImmutableList.of(metadataCollection));
                        return null;
                    }
                };
                break;
            default:
                logger.debug("unhandled command {} from {}", actionCommand, e.getSource());
        }
        executorService.submit(worker);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        update(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        update(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        update(e);
    }

    private void update(DocumentEvent e) {
        Document document = e.getDocument();
        try {
            searchTerms = document.getText(0, document.getLength());
            logger.debug("Updated searchTerms: {}", searchTerms);
        } catch (BadLocationException e1) {
            logger.debug("nope, this cannot happen");
        }
    }

    public AbstractTableModel getResultsTable() {
        return resultsTable;
    }
}
