package io.github.clagomess.tomato.ui.workspace.edit;

import io.github.clagomess.tomato.controller.workspace.edit.ProxyRowPaneController;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.workspace.ProxyDto;
import io.github.clagomess.tomato.ui.component.ColorConstant;
import io.github.clagomess.tomato.ui.component.IconButton;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.util.Objects;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class ProxyRowPane extends JPanel {
    private static final Icon EDIT_ICON = new BxEditIcon();
    private static final Icon TRASH_ICON = new BxTrashIcon();
    private static final Icon PLUS_ICON = new BxPlusIcon();

    private final ProxyListPane parent;
    private final WorkspaceDto workspace;
    private final ProxyRowPaneController controller = new ProxyRowPaneController();

    public ProxyRowPane(
            ProxyListPane parent,
            WorkspaceDto workspace
    ) {
        this.parent = parent;
        this.workspace = workspace;

        setLayout(new MigLayout(
                "insets 2",
                "[grow]0[grow]0[grow]0[]0"
        ));

        var btnNew = new IconButton(PLUS_ICON, "New Proxy");
        btnNew.addActionListener(l -> btnEditAction(btnNew, new ProxyDto()));

        setBorder(new MatteBorder(0, 0, 1, 0, ColorConstant.GRAY));
        add(new JLabel("Host"), "width 30%");
        add(new JLabel("Port"), "width 30%");
        add(new JLabel("User"), "width 30%");
        add(btnNew, "width 10%");
    }

    public ProxyRowPane(
            ProxyListPane parent,
            WorkspaceDto workspace,
            ProxyDto proxy
    ) {
        this.parent = parent;
        this.workspace = workspace;

        setLayout(new MigLayout(
                "insets 2",
                "[grow]0[grow]0[grow]0[]0[]0"
        ));

        setBorder(new MatteBorder(0, 0, 1, 0, ColorConstant.GRAY));
        var btnEdit = new IconButton(EDIT_ICON, "Edit Proxy");
        btnEdit.addActionListener(l -> btnEditAction(btnEdit, proxy));

        var btnDelete = new IconButton(TRASH_ICON, "Delete Proxy");
        btnDelete.addActionListener(l -> btnDeleteAction(btnEdit, proxy));

        add(new JLabel(proxy.getHost()), "width 30%");
        add(new JLabel(String.valueOf(proxy.getPort())), "width 30%");
        add(new JLabel(proxy.getUsername()), "width 30%");
        add(btnEdit, "width 5%");
        add(btnDelete, "width 5%");
    }

    public void btnEditAction(
            IconButton btn,
            ProxyDto proxy
    ) {
        new WaitExecution(this, btn, () -> toFrontIfExists(
                ProxyEditFrame.class,
                () -> new ProxyEditFrame(parent, workspace, proxy),
                item -> Objects.equals(proxy.getId(), item.getProxyId())
        )).execute();
    }

    public void btnDeleteAction(
            IconButton btn,
            ProxyDto proxy
    ) {
        new WaitExecution(parent, btn, () -> {
            int ret = JOptionPane.showConfirmDialog(
                    parent,
                    String.format(
                            "Are you sure you want to delete \"%s\"?",
                            proxy.getHost()
                    ),
                    "Proxy Delete",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if(ret != JOptionPane.OK_OPTION) return;

            controller.delete(workspace, proxy);
            parent.refresh(workspace);
        }).execute();
    }
}
