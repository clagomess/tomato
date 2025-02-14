package com.github.clagomess.tomato.ui.settings;

import com.github.clagomess.tomato.publisher.base.KeyPublisher;
import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.component.RawTextArea;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class DebugPublisherFrame extends JFrame {
    private final RawTextArea console = new RawTextArea();
    private final JButton btnRefresh = new JButton("Refresh");

    public DebugPublisherFrame(MainFrame mainFrame) {
        setTitle("Debug -> Publisher");
        setIconImages(FaviconImage.getFrameIconImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
