package io.github.clagomess.tomato.ui.workspace.edit;

import io.github.clagomess.tomato.controller.workspace.edit.ProxyEditFrameController;
import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.workspace.ProxyDto;
import io.github.clagomess.tomato.ui.BaseFrame;
import io.github.clagomess.tomato.ui.component.ListenableTextField;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.stagingmonitor.StagingMonitor;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ProxyEditFrame
        extends BaseFrame
        implements ProxyEditFrameInterface {
    private final JButton btnSave = new JButton("Save");
    private final JButton btnTest = new JButton("Test");
    private final ListenableTextField txtHost = new ListenableTextField();
    private final ListenableTextField txtPort = new ListenableTextField();
    private final ListenableTextField txtUsername = new ListenableTextField();
    private final ListenableTextField txtPassword = new ListenableTextField();

    @Getter
    private final TomatoID proxyId;

    @Getter
    private final StagingMonitor<ProxyDto> stagingMonitor;

    private final ProxyEditFrameController controller = new ProxyEditFrameController(this);

    public ProxyEditFrame(
            ProxyListPane parent,
            WorkspaceDto workspace,
            ProxyDto proxy
    ) {
        this.proxyId = proxy.getId();
        this.stagingMonitor = new StagingMonitor<>(proxy);

        setTitle("Proxy");
        setMinimumSize(new Dimension(400, 100));
        setResizable(true);

        txtHost.setText(proxy.getHost());
        txtPort.setText(proxy.getPort().toString());
        txtUsername.setText(proxy.getUsername());
        txtPassword.setText(proxy.getPassword());
        btnSave.addActionListener(e -> btnSaveAction(parent, workspace, proxy));
        btnTest.addActionListener(e -> btnTestAction(proxy));
        addOnChangeListeners(proxy);

        setLayout(new MigLayout(
                "insets 10",
                "[][grow, fill]"
        ));
        add(new JLabel("Host:"));
        add(txtHost, "wrap");
        add(new JLabel("Port:"));
        add(txtPort, "wrap");
        add(new JLabel("Username:"));
        add(txtUsername, "wrap");
        add(new JLabel("Password:"));
        add(txtPassword, "wrap");
        add(btnTest, "span 2, split 2, align right");
        add(btnSave);

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void addOnChangeListeners(ProxyDto proxy) {
        txtHost.addOnChange(value -> {
            proxy.setHost(value);
            updateStagingMonitor();
        });
        txtPort.addOnChange(value -> {
            proxy.setPort(Integer.parseInt(value));
            updateStagingMonitor();
        });
        txtUsername.addOnChange(value -> {
            proxy.setUsername(value);
            updateStagingMonitor();
        });
        txtPassword.addOnChange(value -> {
            proxy.setPassword(value);
            updateStagingMonitor();
        });
    }

    private void btnSaveAction(
            ProxyListPane parent,
            WorkspaceDto workspace,
            ProxyDto proxy
    ){
        new WaitExecution(this, btnSave, () -> {
            controller.save(workspace, proxy);
            parent.refresh(workspace);
        }).execute();
    }

    private void btnTestAction(
            ProxyDto proxy
    ){
        new WaitExecution(this, btnTest, () -> {
            controller.test(proxy);
            JOptionPane.showMessageDialog(
                    this,
                    "SSH connection established successfully",
                    "Proxy Test",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }).execute();
    }
}
