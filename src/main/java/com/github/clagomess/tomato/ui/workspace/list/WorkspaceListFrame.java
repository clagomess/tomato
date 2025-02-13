package com.github.clagomess.tomato.ui.workspace.list;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class WorkspaceListFrame extends JFrame {
    private final JPanel rowsPanel;

    private final WorkspaceRepository workspaceRepository = new WorkspaceRepository();

    public WorkspaceListFrame(Component parent){
        setTitle("Edit Workspaces");
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
            workspaceRepository.list().forEach(this::addRow);
        }).execute();

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void addRow(WorkspaceDto item){
        var row = new RowComponent(this, item);

        rowsPanel.add(row, "wrap");
        rowsPanel.revalidate();
        rowsPanel.repaint();
    }
}
