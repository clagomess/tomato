package com.github.clagomess.tomato.ui.workspace;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.io.repository.DataSessionRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WorkspaceNewUI extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();

    private final DataSessionRepository dataSessionRepository = new DataSessionRepository();
    private final WorkspaceRepository workspaceRepository = new WorkspaceRepository();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    public WorkspaceNewUI(
            Component parent
    ){
        setTitle("New Workspace");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Name"), "wrap");
        add(txtName, "width 300!, wrap");
        add(btnSave, "align right");

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnSave.addActionListener(l -> btnSaveAction());
    }

    private void btnSaveAction(){
        new WaitExecution(this, btnSave, () -> {
            WorkspaceDto dto = new WorkspaceDto();
            dto.setName(txtName.getText());
            workspaceRepository.save(dto);

            var session = dataSessionRepository.load();
            session.setWorkspaceId(dto.getId());
            dataSessionRepository.save(session);

            workspacePublisher.getOnSwitch().publish(dto);

            setVisible(false);
            dispose();
        }).execute();
    }
}
