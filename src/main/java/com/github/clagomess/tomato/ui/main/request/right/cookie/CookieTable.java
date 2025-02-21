package com.github.clagomess.tomato.ui.main.request.right.cookie;

import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.ui.component.ColorConstant;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.util.Map;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingUtilities.invokeLater;

public class CookieTable extends JPanel {
    private final TabKey tabKey;
    private final JPanel rowsPanel;

    public CookieTable(TabKey tabKey) {
        this.tabKey = tabKey;

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]"
        ));

        setBorder(new MatteBorder(0, 1, 1, 1, ColorConstant.GRAY));

        JPanel header = new JPanel(new MigLayout(
                "insets 5",
                "[][][grow, fill]"
        ));
        header.setBorder(new MatteBorder(0, 0, 1, 0, ColorConstant.GRAY));
        header.add(new JLabel("Key"), "width 150!");
        header.add(new JLabel("Value"), "width 100%");

        add(header, "width 100%, wrap 0");

        // ### ROWS
        rowsPanel = new JPanel(new MigLayout(
                "insets 5",
                "[grow,fill]"
        ));
        JScrollPane scrollPane = new JScrollPane(
                rowsPanel,
                VERTICAL_SCROLLBAR_AS_NEEDED,
                HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, "width ::100%, height 100%");
    }

    public void refresh(Map<String, String> cookies){
        invokeLater(() -> {
            rowsPanel.removeAll();

            for (Map.Entry<String, String> cookie : cookies.entrySet()) {
                var row = new Row(rowsPanel, tabKey, cookie);

                rowsPanel.add(row, "wrap 4");
            }

            rowsPanel.revalidate();
            rowsPanel.repaint();
        });
    }
}
