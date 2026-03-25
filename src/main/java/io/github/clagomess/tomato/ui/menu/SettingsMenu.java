package io.github.clagomess.tomato.ui.menu;

import com.formdev.flatlaf.util.SystemInfo;
import io.github.clagomess.tomato.ui.MainFrame;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxBugIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxInfoCircleIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSliderAltIcon;
import io.github.clagomess.tomato.ui.settings.*;

import javax.swing.*;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class SettingsMenu extends JMenu {
    private static final Icon SLIDER_ALT_ICON = !SystemInfo.isMacOS ? new BxSliderAltIcon() : null;
    private static final Icon INFO_CIRCLE_ICON = !SystemInfo.isMacOS ? new BxInfoCircleIcon() : null;
    private static final Icon BUG_ICON = !SystemInfo.isMacOS ? new BxBugIcon() : null;

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
