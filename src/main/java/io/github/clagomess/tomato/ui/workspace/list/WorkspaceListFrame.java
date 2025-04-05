package io.github.clagomess.tomato.ui.workspace.list;

import io.github.clagomess.tomato.controller.workspace.list.WorkspaceListFrameController;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingUtilities.invokeLater;

public class WorkspaceListFrame extends JFrame {
    private final JPanel rowsPanel;
    private final WorkspaceListFrameController controller = new WorkspaceListFrameController();

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
            controller.refresh(this::refresh);
        }).execute();

        controller.addOnChangeListener(this::refresh);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void refresh(List<WorkspaceDto> rows){
        invokeLater(() -> {
            rowsPanel.removeAll();
            rows.forEach(this::addRow);
        });
    }

    private void addRow(WorkspaceDto item){
        var row = new Row(this, item);

        rowsPanel.add(row, "wrap");
        rowsPanel.revalidate();
        rowsPanel.repaint();
    }

    @Override
    public void dispose(){
        controller.dispose();
        super.dispose();
    }
}
