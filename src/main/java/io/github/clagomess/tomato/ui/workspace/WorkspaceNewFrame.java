package io.github.clagomess.tomato.ui.workspace;

import io.github.clagomess.tomato.controller.workspace.WorkspaceNewFrameController;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import io.github.clagomess.tomato.ui.component.undoabletextcomponent.UndoableTextField;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WorkspaceNewFrame extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final UndoableTextField txtName = new UndoableTextField();

    public WorkspaceNewFrame(
            Component parent
    ){
        setTitle("New Workspace");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
            new WorkspaceNewFrameController()
                    .save(txtName.getText());

            setVisible(false);
            dispose();
        }).execute();
    }
}
