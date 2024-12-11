package com.github.clagomess.tomato.ui.workspace;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.service.WorkspaceDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImageIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WorkspaceNewUI extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();

    private final WorkspaceDataService workspaceDataService = WorkspaceDataService.getInstance();

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

            setVisible(false);
            dispose();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSave.setEnabled(true);
        }
    }
}
