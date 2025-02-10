package com.github.clagomess.tomato.ui.environment.list;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class EnvironmentListUI extends JFrame {
    private final JPanel rowsPanel;

    private final UUID listenerUuid;
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();
    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();

    public EnvironmentListUI(Component parent) {
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

        refresh();
        listenerUuid = environmentPublisher.getOnChange()
                .addListener(e -> refresh());

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void addRow(EnvironmentDto item){
        var row = new RowComponent(rowsPanel, item);

        rowsPanel.add(row, "wrap");
        rowsPanel.revalidate();
        rowsPanel.repaint();
    }

    private void refresh(){
        new WaitExecution(this, () -> {
            rowsPanel.removeAll();
            environmentRepository.list().forEach(this::addRow);
        }).execute();
    }

    @Override
    public void dispose(){
        environmentPublisher.getOnChange().removeListener(listenerUuid);
        super.dispose();
    }
}
