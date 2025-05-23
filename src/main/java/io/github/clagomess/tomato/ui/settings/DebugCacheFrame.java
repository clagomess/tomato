package io.github.clagomess.tomato.ui.settings;

import io.github.clagomess.tomato.ui.MainFrame;
import io.github.clagomess.tomato.ui.component.RawTextArea;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import io.github.clagomess.tomato.util.CacheManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class DebugCacheFrame extends JFrame {
    private final RawTextArea console = new RawTextArea();
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnReset = new JButton("Reset");

    public DebugCacheFrame(MainFrame mainFrame) {
        setTitle("Debug -> Cache");
        setIconImages(FaviconImage.getFrameIconImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));

        setLayout(new MigLayout(
                "insets 10",
                "[grow, fill]"
        ));

        add(new JScrollPane(console), "height 100%, wrap");

        var panelButton = new JPanel(new MigLayout("insets 2"));
        panelButton.add(btnRefresh);
        panelButton.add(btnReset);
        add(panelButton);

        btnRefresh.addActionListener(e -> refresh());
        btnReset.addActionListener(e -> reset());

        pack();
        setLocationRelativeTo(mainFrame);
        setVisible(true);

        refresh();
    }

    private void refresh() {
        new WaitExecution(this, btnRefresh, () -> {
            console.reset();
            console.setText(CacheManager.debug());
        }).execute();
    }

    private void reset(){
        new WaitExecution(this, btnReset, () -> {
            CacheManager.reset();
            console.reset();
            console.setText(CacheManager.debug());
        }).execute();
    }
}
