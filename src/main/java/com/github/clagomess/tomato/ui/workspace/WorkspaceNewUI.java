package com.github.clagomess.tomato.ui.workspace;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.service.DataSessionDataService;
import com.github.clagomess.tomato.service.WorkspaceDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImageIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WorkspaceNewUI extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();

    private final DataSessionDataService dataSessionDataService = DataSessionDataService.getInstance();
    private final WorkspaceDataService workspaceDataService = WorkspaceDataService.getInstance();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    public WorkspaceNewUI(
            Component parent
    ){
        setTitle("New Workspace");
        setIconImage(new FaviconImageIcon().getImage());
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);

        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel("Name"), "wrap");
        panel.add(txtName, "width 100%, wrap");
        panel.add(btnSave, "align right");
        add(panel);

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnSave.addActionListener(l -> btnSaveAction());
    }

    private void btnSaveAction(){
        btnSave.setEnabled(false);

        try {
            WorkspaceDto dto = new WorkspaceDto();
            dto.setName(txtName.getText());
            workspaceDataService.saveWorkspace(dto);

            var session = dataSessionDataService.getDataSession();
            session.setWorkspaceId(dto.getId());
            dataSessionDataService.saveDataSession(session);

            workspacePublisher.getOnSwitch().publish(dto);

            setVisible(false);
            dispose();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSave.setEnabled(true);
        }
    }
}
