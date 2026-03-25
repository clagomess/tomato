package io.github.clagomess.tomato.ui;

import com.formdev.flatlaf.util.SystemInfo;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import io.github.clagomess.tomato.ui.main.collection.CollectionTree;
import io.github.clagomess.tomato.ui.main.request.RequestTabbedPane;
import io.github.clagomess.tomato.ui.menu.CollectionMenu;
import io.github.clagomess.tomato.ui.menu.EnvironmentMenu;
import io.github.clagomess.tomato.ui.menu.SettingsMenu;
import io.github.clagomess.tomato.ui.menu.WorkspaceMenu;
import io.github.clagomess.tomato.ui.settings.AboutFrame;
import io.github.clagomess.tomato.ui.settings.ConfigurationFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;
import static javax.swing.SwingUtilities.invokeLater;

public class MainFrame extends JFrame {
    public MainFrame(){
        setTitle("Tomato");
        setIconImages(FaviconImage.getFrameIconImage());
        setVisible(true);
        setMinimumSize(new Dimension(1200, 650));
        setJMenuBar(getMenu());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new MainWindowAdapter(this));

        if(SystemInfo.isMacOS) {
            if (SystemInfo.isMacFullWindowContentSupported) {
                getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
                getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
                getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
            }

            var desktop = Desktop.getDesktop();

            if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
                desktop.setAboutHandler(e -> toFrontIfExists(
                        AboutFrame.class,
                        () -> new AboutFrame(this)
                ));
            }

            if (desktop.isSupported(Desktop.Action.APP_PREFERENCES)) {
                desktop.setPreferencesHandler(e -> toFrontIfExists(
                        ConfigurationFrame.class,
                        () -> new ConfigurationFrame(this)
                ));
            }

            if (desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
                desktop.setQuitHandler((e, response) -> {
                    if(MainWindowAdapter.shouldQuit(this)){
                        this.dispose();
                        response.performQuit();
                    }else{
                        response.cancelQuit();
                    }
                });
            }
        }

        setLayout(new MigLayout("insets 5", "[grow, fill]"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        add(splitPane, "height 100%");

        invokeLater(() -> {
            splitPane.setLeftComponent(new CollectionTree());
            splitPane.setDividerLocation(250);
            splitPane.revalidate();
            splitPane.repaint();
        });
        invokeLater(() -> {
            splitPane.setRightComponent(new RequestTabbedPane());
            splitPane.setDividerLocation(250);
            splitPane.revalidate();
            splitPane.repaint();
        });

        pack();
        setVisible(true);
    }

    public JMenuBar getMenu(){
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new WorkspaceMenu(this));
        menuBar.add(new CollectionMenu(this));
        menuBar.add(new EnvironmentMenu(this));
        menuBar.add(new SettingsMenu(this));

        return menuBar;
    }
}
