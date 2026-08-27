package io.github.clagomess.tomato.ui.workspace.edit;

import io.github.clagomess.tomato.controller.workspace.edit.WorkspaceEditFrameController;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.ui.BaseFrame;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.undoabletextcomponent.UndoableTextField;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WorkspaceEditFrame extends BaseFrame {
    protected final JButton btnSave = new JButton("Save");
    protected final UndoableTextField txtName = new UndoableTextField();

    private final WorkspaceEditFrameController controller = new WorkspaceEditFrameController();

    public WorkspaceEditFrame(
            Component parent,
            WorkspaceDto workspace
    ) {
        setTitle("Workspace Edit");
        setResizable(false);
        setMinimumSize(new Dimension(500, 300));
        setLayout(new MigLayout(
                "insets 10",
                "[][grow, fill]"
        ));

        var proxyListPane = new ProxyListPane(workspace);

        add(new JLabel("Name"));
        add(txtName, "wrap");
        add(proxyListPane.getWrappedScrollPane(), "span 2, width 100%, height 100%, wrap");
        add(btnSave, "span 2, align right");

        txtName.setText(workspace.getName());

        btnSave.addActionListener(l -> new WaitExecution(this, btnSave, () -> {
            workspace.setName(txtName.getText());
            controller.save(workspace);

            setVisible(false);
            dispose();
        }).execute());

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
