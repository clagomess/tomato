package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxBugIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxInfoCircleIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSliderAltIcon;
import com.github.clagomess.tomato.ui.settings.*;

import javax.swing.*;

import static com.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class SettingsMenu extends JMenu {
    private static final Icon SLIDER_ALT_ICON = new BxSliderAltIcon();
    private static final Icon INFO_CIRCLE_ICON = new BxInfoCircleIcon();
    private static final Icon BUG_ICON = new BxBugIcon();

    public SettingsMenu(MainFrame mainFrame) {
        super("Settings");

        var mConfiguration = new JMenuItem("Configuration", SLIDER_ALT_ICON);
        mConfiguration.addActionListener(l -> toFrontIfExists(
                ConfigurationFrame.class,
                () -> new ConfigurationFrame(mainFrame)
        ));
        add(mConfiguration);

        var mAbout = new JMenuItem("About", INFO_CIRCLE_ICON);
        mAbout.addActionListener(l -> toFrontIfExists(
                AboutFrame.class,
                () -> new AboutFrame(mainFrame)
        ));
        add(mAbout);

        var debugPublisher = new JMenuItem("Debug -> Publisher", BUG_ICON);
        debugPublisher.addActionListener(l -> toFrontIfExists(
                DebugPublisherFrame.class,
                () -> new DebugPublisherFrame(mainFrame)
        ));
        add(debugPublisher);

        var debugCache = new JMenuItem("Debug -> Cache", BUG_ICON);
        debugCache.addActionListener(l -> toFrontIfExists(
                DebugCacheFrame.class,
                () -> new DebugCacheFrame(mainFrame)
        ));
        add(debugCache);

        var debugThreads = new JMenuItem("Debug -> Threads", BUG_ICON);
        debugThreads.addActionListener(l -> toFrontIfExists(
                DebugThreadsFrame.class,
                () -> new DebugThreadsFrame(mainFrame)
        ));
        add(debugThreads);
    }
}
