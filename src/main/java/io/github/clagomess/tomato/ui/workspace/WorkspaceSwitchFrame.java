package io.github.clagomess.tomato.ui.workspace;

import io.github.clagomess.tomato.controller.workspace.WorkspaceSwitchFrameController;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WorkspaceSwitchFrame extends JFrame {
    private final WorkspaceComboBox cbWorkspace = new WorkspaceComboBox();
    private final JButton btnSwitch = new JButton("Switch");

    public WorkspaceSwitchFrame(Component parent) {
        setTitle("Switch Workspace");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Select workspace:"), "wrap");
        add(cbWorkspace, "width 300!, wrap");
        add(btnSwitch, "align right");

        getRootPane().setDefaultButton(btnSwitch);

        // data
        btnSwitch.addActionListener(l -> btnSwitchAction());

        setLocationRelativeTo(parent);
        pack();
        setVisible(true);
    }

    private void btnSwitchAction(){
        new WaitExecution(this, btnSwitch, () -> {
            WorkspaceDto selected = cbWorkspace.getSelectedItem();
            if(selected == null) return;

            new WorkspaceSwitchFrameController()
                    .switchWorkspace(selected);

            setVisible(false);
            dispose();
        }).execute();
    }
}
