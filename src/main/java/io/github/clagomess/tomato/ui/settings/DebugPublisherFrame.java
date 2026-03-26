package io.github.clagomess.tomato.ui.settings;

import io.github.clagomess.tomato.publisher.base.KeyPublisher;
import io.github.clagomess.tomato.ui.BaseFrame;
import io.github.clagomess.tomato.ui.MainFrame;
import io.github.clagomess.tomato.ui.component.RawTextArea;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class DebugPublisherFrame extends BaseFrame {
    private final RawTextArea console = new RawTextArea();
    private final JButton btnRefresh = new JButton("Refresh");

    public DebugPublisherFrame(MainFrame mainFrame) {
        setTitle("Debug -> Publisher");
        setMinimumSize(new Dimension(600, 400));

        setLayout(new MigLayout(
                "insets 10",
                "[grow, fill]"
        ));

        add(new JScrollPane(console), "height 100%, wrap");
        add(btnRefresh);

        btnRefresh.addActionListener(e -> refresh());

        pack();
        setLocationRelativeTo(mainFrame);
        setVisible(true);

        refresh();
    }

    private void refresh() {
        new WaitExecution(this, btnRefresh, () -> {
            console.reset();
            console.setText(KeyPublisher.debug());
        }).execute();
    }
}
