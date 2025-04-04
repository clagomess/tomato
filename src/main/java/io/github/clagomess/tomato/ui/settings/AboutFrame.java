package io.github.clagomess.tomato.ui.settings;

import io.github.clagomess.tomato.ui.MainFrame;
import io.github.clagomess.tomato.ui.component.favicon.FaviconIcon;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import io.github.clagomess.tomato.util.RevisionUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class AboutFrame extends JFrame {
    public AboutFrame(MainFrame mainFrame) {
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
        setLocationRelativeTo(mainFrame);
        setVisible(true);
    }
}
