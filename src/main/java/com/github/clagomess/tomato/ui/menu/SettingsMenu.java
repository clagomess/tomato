package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainUI;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSliderAltIcon;
import com.github.clagomess.tomato.ui.settings.*;

import javax.swing.*;

public class SettingsMenu extends JMenu {
    public SettingsMenu(MainUI mainUI) {
        super("Settings");

        var mConfiguration = new JMenuItem("Configuration", new BxSliderAltIcon());
        mConfiguration.addActionListener(l -> new ConfigurationUI(mainUI));
        add(mConfiguration);

        var mAbout = new JMenuItem("About");
        mAbout.addActionListener(l -> new AboutUI(mainUI));
        add(mAbout);

        var debugPublisher = new JMenuItem("Debug -> Publisher");
        debugPublisher.addActionListener(l -> new DebugPublisherUI(mainUI));
        add(debugPublisher);

        var debugCache = new JMenuItem("Debug -> Cache");
        debugCache.addActionListener(l -> new DebugCacheUI(mainUI));
        add(debugCache);

        var debugThreads = new JMenuItem("Debug -> Threads");
        debugThreads.addActionListener(l -> new DebugThreadsUI(mainUI));
        add(debugThreads);
    }
}
