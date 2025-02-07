package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainUI;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSliderAltIcon;
import com.github.clagomess.tomato.ui.settings.AboutUI;
import com.github.clagomess.tomato.ui.settings.ConfigurationUI;
import com.github.clagomess.tomato.ui.settings.DebugPublisherUI;

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
    }
}
