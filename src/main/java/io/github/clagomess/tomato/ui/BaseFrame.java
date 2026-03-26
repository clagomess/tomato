package io.github.clagomess.tomato.ui;

import com.formdev.flatlaf.util.SystemInfo;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;

import javax.swing.*;

public abstract class BaseFrame extends JFrame {
    public BaseFrame() {
        if (SystemInfo.isMacFullWindowContentSupported) {
            getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        }

        setIconImages(FaviconImage.getFrameIconImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
