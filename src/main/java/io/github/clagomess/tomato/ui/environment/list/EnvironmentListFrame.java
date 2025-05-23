package io.github.clagomess.tomato.ui.environment.list;

import io.github.clagomess.tomato.controller.environment.list.EnvironmentListController;
import io.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import io.github.clagomess.tomato.ui.component.EmptyPane;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class EnvironmentListFrame
        extends JFrame
        implements EnvironmentListInterface {
    private final JPanel rowsPanel;

    private final EnvironmentListController controller;

    public EnvironmentListFrame(Component parent) {
        controller = new EnvironmentListController(this);

        setTitle("Edit Environments");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 400));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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

        refresh();
        controller.addOnChangeListner();

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public void refresh() {
        new WaitExecution(
                this,
                () -> refresh(controller.list())
        ).execute();
    }

    private void refresh(List<EnvironmentHeadDto> items){
        new WaitExecution(this, () -> {
            rowsPanel.removeAll();

            for(var item : items){
                var row = new Row(rowsPanel, item);
                rowsPanel.add(row, "wrap");
            }

            if(items.isEmpty()){
                rowsPanel.add(new EmptyPane(), "wrap");
            }

            rowsPanel.revalidate();
            rowsPanel.repaint();
        }).execute();
    }

    @Override
    public void dispose(){
        controller.dispose();
        super.dispose();
    }
}
