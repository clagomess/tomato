package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxBugIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxInfoCircleIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSliderAltIcon;
import com.github.clagomess.tomato.ui.settings.*;

import javax.swing.*;

import static com.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class SettingsMenu extends JMenu {
    public SettingsMenu(MainFrame mainFrame) {
        super("Settings");

        var mConfiguration = new JMenuItem("Configuration", new BxSliderAltIcon());
        mConfiguration.addActionListener(l -> toFrontIfExists(
                ConfigurationFrame.class,
                () -> new ConfigurationFrame(mainFrame)
        ));
        add(mConfiguration);

        var mAbout = new JMenuItem("About", new BxInfoCircleIcon());
        mAbout.addActionListener(l -> toFrontIfExists(
                AboutFrame.class,
                () -> new AboutFrame(mainFrame)
        ));
        add(mAbout);

        var debugPublisher = new JMenuItem("Debug -> Publisher", new BxBugIcon());
        debugPublisher.addActionListener(l -> toFrontIfExists(
                DebugPublisherFrame.class,
                () -> new DebugPublisherFrame(mainFrame)
        ));
        add(debugPublisher);

        var debugCache = new JMenuItem("Debug -> Cache", new BxBugIcon());
        debugCache.addActionListener(l -> toFrontIfExists(
                DebugCacheFrame.class,
                () -> new DebugCacheFrame(mainFrame)
        ));
        add(debugCache);

        var debugThreads = new JMenuItem("Debug -> Threads", new BxBugIcon());
        debugThreads.addActionListener(l -> toFrontIfExists(
                DebugThreadsFrame.class,
                () -> new DebugThreadsFrame(mainFrame)
        ));
        add(debugThreads);
    }
}
