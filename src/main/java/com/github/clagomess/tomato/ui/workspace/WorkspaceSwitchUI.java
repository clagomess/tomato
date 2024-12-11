package com.github.clagomess.tomato.ui.workspace;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.service.DataSessionDataService;
import com.github.clagomess.tomato.service.WorkspaceDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.IconFactory;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WorkspaceSwitchUI extends JFrame {
    private final JComboBox<WorkspaceDto> cbWorkspace = new JComboBox<>(); // @TODO: create custon, label load
    private final JButton btnSwitch = new JButton("Switch");

    private final WorkspaceDataService workspaceDataService = WorkspaceDataService.getInstance();
    private final DataSessionDataService dataSessionDataService = DataSessionDataService.getInstance();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    public WorkspaceSwitchUI(Component parent) {
        setTitle("Switch Workspace");
        setIconImage(IconFactory.ICON_FAVICON.getImage());
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);

        JPanel panel = new JPanel(new MigLayout("", "[grow, fill]"));
        panel.add(new JLabel("Select workspace:"), "wrap");
        panel.add(cbWorkspace, "wrap");
        JPanel pButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pButton.add(btnSwitch);
        panel.add(pButton);
        add(panel);


        // data
        SwingUtilities.invokeLater(this::setCbWorkspaceItens);
        btnSwitch.addActionListener(l -> btnSwitchAction());

        setLocationRelativeTo(parent);
        pack();
        setVisible(true);
    }

    private void setCbWorkspaceItens(){
        try {
            workspaceDataService.listWorkspaces().forEach(cbWorkspace::addItem);
            cbWorkspace.setSelectedItem(workspaceDataService.getDataSessionWorkspace());
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }
    }

    private void btnSwitchAction(){
        try {
            btnSwitch.setEnabled(false);
            WorkspaceDto selected = (WorkspaceDto) cbWorkspace.getSelectedItem();
            if(selected == null) return;

            var session = dataSessionDataService.getDataSession();
            session.setWorkspaceId(selected.getId());
            dataSessionDataService.saveDataSession(session);

            workspacePublisher.getOnSwitch().publish(selected);

            setVisible(false);
            dispose();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSwitch.setEnabled(true);
        }
    }
}
