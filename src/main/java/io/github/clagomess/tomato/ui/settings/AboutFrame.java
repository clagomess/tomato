package io.github.clagomess.tomato.ui.settings;

import io.github.clagomess.tomato.ui.MainFrame;
import io.github.clagomess.tomato.ui.component.favicon.FaviconIcon;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static com.formdev.flatlaf.FlatClientProperties.STYLE_CLASS;
import static io.github.clagomess.tomato.util.RevisionUtil.REVISION;

public class AboutFrame extends JFrame {
    public AboutFrame(MainFrame mainFrame) {
        setTitle("About");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[center, grow]"
        ));

        // icon
        add(new JLabel(new FaviconIcon()), "wrap");

        // title
        var title = new JLabel("Tomato");
        title.putClientProperty(STYLE_CLASS, "h1");
        add(title, "wrap 10");

        // uri
        var uri = new JLabel("https://github.com/clagomess/tomato");
        add(uri, "wrap");

        // release
        var release = new JLabel(REVISION);
        release.putClientProperty(STYLE_CLASS, "small");
        add(release);

        pack();
        setLocationRelativeTo(mainFrame);
        setVisible(true);
    }
}
