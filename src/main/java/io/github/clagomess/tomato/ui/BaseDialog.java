package io.github.clagomess.tomato.ui;

import com.formdev.flatlaf.util.SystemInfo;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public abstract class BaseDialog extends JDialog {
    public BaseDialog(
            @Nullable Component parent,
            String title
    ) {
        super(
                parent != null ? SwingUtilities.getWindowAncestor(parent) : null,
                title,
                Dialog.DEFAULT_MODALITY_TYPE
        );

        if (SystemInfo.isMacFullWindowContentSupported) {
            getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        }

        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setIconImages(FaviconImage.getFrameIconImage());
    }
}
