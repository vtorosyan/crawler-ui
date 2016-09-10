package com.vtor.crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

final class MatchesComponent {

    private JTable table;
    private JPanel panel;

    MatchesComponent() {
        build();
    }

    private JPanel build() {
        table = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"URL"}) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Found Matches"));
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    JTable getTable() {
        return table;
    }

    JPanel getPanel() {
        return panel;
    }

    void add(String url) {
        DefaultTableModel model = (DefaultTableModel) getTable().getModel();
        model.addRow(new Object[]{url});
    }

}
