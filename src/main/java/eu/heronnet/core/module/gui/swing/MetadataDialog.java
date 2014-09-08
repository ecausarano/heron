package eu.heronnet.core.module.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

/**
 * Created by edoardocausarano on 07-09-14.
 */
public class MetadataDialog extends JDialog {

    private JPanel panel;
    private JButton addButton;

    public MetadataDialog(Frame owner, boolean modal) {
        super(owner, "Add Metadata", modal);
        init();
    }

    private void init() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        this.add(panel);
        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Row row = new Row();
                panel.add(row);
                panel.updateUI();
            }
        });
        this.add(addButton);

        JButton done = new JButton("Done");
        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        this.add(done);
        this.pack();
    }

    public class UploadEvent extends EventObject {

        /**
         * Constructs a prototypical Event.
         *
         * @param source The object on which the Event initially occurred.
         * @throws IllegalArgumentException if source is null.
         */
        public UploadEvent(Object source) {
            super(source);
        }
    }

    private class Row extends JPanel {

        private final JLabel nameLabel;
        private final JTextField name;
        private final JLabel valueLabel;
        private final JTextField value;

        public Row() {
            this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            nameLabel = new JLabel("name");
            this.add(nameLabel);
            name = new JTextField();
            this.add(name);

            valueLabel = new JLabel("value");
            this.add(valueLabel);
            value = new JTextField();
            this.add(value);
        }
    }
}
