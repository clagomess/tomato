package com.github.clagomess.tomato.ui.environment.list;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class EnvironmentListUI extends JFrame {
    private final JPanel rowsPanel;

    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();

    public EnvironmentListUI(Component parent) throws HeadlessException {
        setTitle("Edit Environments");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new MigLayout(
                "insets 10",
                "[grow, fill]"
        ));

        // ### ROWS
        rowsPanel = new JPanel(new MigLayout(
                "insets 0 0 0 0",
                "[grow,fill]"
        ));
        JScrollPane scrollPane = new JScrollPane(
                rowsPanel,
                VERTICAL_SCROLLBAR_AS_NEEDED,
                HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, "width ::100%, height 100%");

        new WaitExecution(this, () -> {
            environmentRepository.list().forEach(this::addRow);
        }).execute();

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void addRow(EnvironmentDto item){
        var row = new RowComponent(this, item);

        rowsPanel.add(row, "wrap");
        rowsPanel.revalidate();
    }
}
