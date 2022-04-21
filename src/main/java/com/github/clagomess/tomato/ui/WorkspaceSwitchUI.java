package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.dto.WorkspaceDto;
import com.github.clagomess.tomato.factory.IconFactory;
import com.github.clagomess.tomato.service.DataService;
import com.github.clagomess.tomato.util.UIPublisherUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WorkspaceSwitchUI extends JFrame {
    private final JComboBox<WorkspaceDto> cbWorkspace = new JComboBox<>();
    private final JButton btnSwitch = new JButton("Switch");

    public WorkspaceSwitchUI() {
        setTitle("Switch Workspace");
        setIconImage(IconFactory.ICON_FAVICON.getImage());

        JPanel panel = new JPanel(new MigLayout("", "[grow, fill]"));
        panel.add(new JLabel("Select workspace:"), "wrap");
        panel.add(cbWorkspace, "wrap");
        JPanel pButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pButton.add(btnSwitch);
        panel.add(pButton);

        DataService.getInstance().getWorkspaces().forEach(cbWorkspace::addItem);
        cbWorkspace.setSelectedItem(DataService.getInstance().getCurrentWorkspace());
        btnSwitch.addActionListener(l -> btnSwitchAction());

        add(panel);
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);
        pack();
        setVisible(true);
    }

    private void btnSwitchAction(){
        btnSwitch.setEnabled(false);
        WorkspaceDto selected = (WorkspaceDto) cbWorkspace.getSelectedItem();
        DataService.getInstance().setCurrentWorkspace(selected);
        UIPublisherUtil.getInstance().notifySwitchWorkspaceSubscribers(selected);
        setVisible(false);
        dispose();
    }
}
