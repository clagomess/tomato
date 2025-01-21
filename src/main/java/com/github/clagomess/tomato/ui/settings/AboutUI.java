package com.github.clagomess.tomato.ui.settings;

import com.github.clagomess.tomato.ui.MainUI;
import com.github.clagomess.tomato.ui.component.favicon.FaviconIcon;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.util.RevisionUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AboutUI extends JFrame {
    public AboutUI() {
        setTitle("About");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[center, grow]"
        ));

        // icon
        add(new JLabel(new FaviconIcon()), "wrap");

        // title
        var title = new JLabel("Tomato");
        title.putClientProperty("FlatLaf.styleClass", "h1");
        add(title, "wrap 10");

        // uri
        var uri = new JLabel("https://github.com/clagomess/tomato");
        add(uri, "wrap");

        // release
        var release = new JLabel(String.format(
                "%s - %s",
                RevisionUtil.getInstance().getDeployTag(),
                RevisionUtil.getInstance().getDeployCommit()
        ));
        release.putClientProperty("FlatLaf.styleClass", "small");
        add(release);

        pack();
        setLocationRelativeTo(
                Arrays.stream(Window.getWindows())
                        .filter(item -> item instanceof MainUI)
                        .findFirst()
                        .orElse(null)
        );
        setVisible(true);
    }
}
