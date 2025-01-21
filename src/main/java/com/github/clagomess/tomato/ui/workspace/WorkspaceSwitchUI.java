package com.github.clagomess.tomato.ui.workspace;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.io.repository.DataSessionRepository;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WorkspaceSwitchUI extends JFrame {
    private final WorkspaceComboBox cbWorkspace = new WorkspaceComboBox();
    private final JButton btnSwitch = new JButton("Switch");

    private final DataSessionRepository dataSessionRepository = new DataSessionRepository();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    public WorkspaceSwitchUI(Component parent) {
        setTitle("Switch Workspace");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

            var session = dataSessionRepository.load();
            session.setWorkspaceId(selected.getId());
            dataSessionRepository.save(session);

            workspacePublisher.getOnSwitch().publish(selected);

            setVisible(false);
            dispose();
        }).execute();
    }
}
