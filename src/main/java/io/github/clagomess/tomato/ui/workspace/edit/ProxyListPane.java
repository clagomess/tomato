package io.github.clagomess.tomato.ui.workspace.edit;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.ui.component.ColorConstant;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingUtilities.invokeLater;

public class ProxyListPane extends JPanel {
    public ProxyListPane(WorkspaceDto workspace) {
        setLayout(new MigLayout(
                "insets 2",
                "[grow,fill]"
        ));

        refresh(workspace);
    }

    public JScrollPane getWrappedScrollPane() {
        JScrollPane scrollPane = new JScrollPane(
                this,
                VERTICAL_SCROLLBAR_AS_NEEDED,
                HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new MatteBorder(1, 1, 1, 1, ColorConstant.GRAY),
                "Proxies (SSH Servers)",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));

        return scrollPane;
    }

    protected void refresh(WorkspaceDto workspace){
        invokeLater(() -> {
            removeAll();
            add(new ProxyRowPane(this, workspace), "wrap");
            for(var item : workspace.getProxies()){
                add(new ProxyRowPane(this, workspace, item), "wrap");
            }
            revalidate();
            repaint();
        });
    }
}
