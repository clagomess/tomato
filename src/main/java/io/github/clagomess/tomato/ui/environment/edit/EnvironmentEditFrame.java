package io.github.clagomess.tomato.ui.environment.edit;

import io.github.clagomess.tomato.controller.environment.edit.EnvironmentEditFrameController;
import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.ui.component.ListenableTextField;
import io.github.clagomess.tomato.ui.component.PasswordDialog;
import io.github.clagomess.tomato.ui.component.StagingMonitor;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static javax.swing.SwingUtilities.invokeLater;

public class EnvironmentEditFrame
        extends JFrame
        implements EnvironmentEditFrameInterface {
    private final JButton btnSave = new JButton("Save");
    private final ListenableTextField txtName = new ListenableTextField();
    private final JCheckBox chkProduction = new JCheckBox();
    private final KeyValue keyValue;
    private final StagingMonitor<EnvironmentDto> stagingMonitor;

    @Getter
    private final String environmentId;

    private final EnvironmentEditFrameController controller;

    public EnvironmentEditFrame(
            Component parent,
            String environmentId
    ) throws IOException {
        this.environmentId = environmentId;
        this.controller = new EnvironmentEditFrameController(environmentId, this);
        this.stagingMonitor = new StagingMonitor<>(controller.getEnvironment());

        setTitle(buildTitle(false));
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(600, 500));
        setPreferredSize(new Dimension(600, 500));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        txtName.setText(controller.getEnvironment().getName());
        chkProduction.setSelected(controller.getEnvironment().isProduction());
        txtName.addOnChange(value -> {
            controller.getEnvironment().setName(value);
            updateStagingMonitor();
        });
        chkProduction.addActionListener(listener -> {
            controller.getEnvironment().setProduction(chkProduction.isSelected());
            updateStagingMonitor();
        });
        keyValue = new KeyValue(
                controller.getEnvironmentKeystore(),
                controller.getEnvironment().getEnvs()
        );

        setLayout(new MigLayout(
                "insets 10",
                "[][grow, fill]"
        ));
        add(new JLabel("Name:"));
        add(txtName, "wrap");
        add(new JLabel("Production?:"));
        add(chkProduction, "wrap");
        add(keyValue, "span 2, width 100%, height 100%, wrap");
        add(btnSave, "span 2, align right");

        btnSave.addActionListener(l -> btnSaveAction());

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    @Override
    public String getPassword() {
        return PasswordDialog.showInputPassword(this);
    }

    @Override
    public String getNewPassword() {
        return PasswordDialog.showInputNewPassword(this);
    }

    private static final String TITLE_PREFIX = "Environment - ";
    private static final String TITLE_CHANGING_PREFIX = "[*] Environment - ";
    private String buildTitle(boolean isChanging){
        if(isChanging){
            return TITLE_CHANGING_PREFIX + controller.getEnvironment().getName();
        }

        return TITLE_PREFIX + controller.getEnvironment().getName();
    }

    public void updateStagingMonitor(){
        stagingMonitor.update();

        invokeLater(() -> setTitle(
                buildTitle(stagingMonitor.isDiferent())
        ));
    }

    public void resetStagingMonitor(){
        keyValue.resetStagingMonitor();
        stagingMonitor.reset();
        invokeLater(() -> setTitle(
                buildTitle(false)
        ));
    }

    private void btnSaveAction(){
        new WaitExecution(this, btnSave, controller::save)
                .execute();
    }
}
