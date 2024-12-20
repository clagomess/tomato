package com.github.clagomess.tomato.ui.workspace;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.service.DataSessionDataService;
import com.github.clagomess.tomato.service.WorkspaceDataService;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WorkspaceNewUI extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();

    private final DataSessionDataService dataSessionDataService = new DataSessionDataService();
    private final WorkspaceDataService workspaceDataService = new WorkspaceDataService();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    public WorkspaceNewUI(
            Component parent
    ){
        setTitle("New Workspace");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);

        JPanel panel = new JPanel(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        panel.add(new JLabel("Name"), "wrap");
        panel.add(txtName, "width 300!, wrap");
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
        new WaitExecution(this, btnSave).setExecute(() -> {
            WorkspaceDto dto = new WorkspaceDto();
            dto.setName(txtName.getText());
            workspaceDataService.saveWorkspace(dto);

            var session = dataSessionDataService.getDataSession();
            session.setWorkspaceId(dto.getId());
            dataSessionDataService.saveDataSession(session);

            workspacePublisher.getOnSwitch().publish(dto);

            setVisible(false);
            dispose();
        }).execute();
    }
}
