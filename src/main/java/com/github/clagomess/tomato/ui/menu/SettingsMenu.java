package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSliderAltIcon;
import com.github.clagomess.tomato.ui.settings.*;

import javax.swing.*;

public class SettingsMenu extends JMenu {
    public SettingsMenu(MainFrame mainFrame) {
        super("Settings");

        var mConfiguration = new JMenuItem("Configuration", new BxSliderAltIcon());
        mConfiguration.addActionListener(l -> new ConfigurationFrame(mainFrame));
        add(mConfiguration);

        var mAbout = new JMenuItem("About");
        mAbout.addActionListener(l -> new AboutFrame(mainFrame));
        add(mAbout);

        var debugPublisher = new JMenuItem("Debug -> Publisher");
        debugPublisher.addActionListener(l -> new DebugPublisherFrame(mainFrame));
        add(debugPublisher);

        var debugCache = new JMenuItem("Debug -> Cache");
        debugCache.addActionListener(l -> new DebugCacheFrame(mainFrame));
        add(debugCache);

        var debugThreads = new JMenuItem("Debug -> Threads");
        debugThreads.addActionListener(l -> new DebugThreadsFrame(mainFrame));
        add(debugThreads);
    }
}
