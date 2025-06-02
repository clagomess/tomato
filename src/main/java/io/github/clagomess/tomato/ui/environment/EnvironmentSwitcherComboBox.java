package io.github.clagomess.tomato.ui.environment;

import io.github.clagomess.tomato.controller.environment.EnvironmentSwitcherComboBoxController;
import io.github.clagomess.tomato.ui.component.ComponentUtil;
import io.github.clagomess.tomato.ui.component.IconButton;
import io.github.clagomess.tomato.ui.component.PasswordDialog;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import io.github.clagomess.tomato.ui.environment.edit.EnvironmentEditFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;
import static javax.swing.SwingUtilities.getWindowAncestor;

public class EnvironmentSwitcherComboBox extends JPanel implements EnvironmentSwitcherComboBoxInterface {
    private static final Icon EDIT_ICON = new BxEditIcon();

    private final EnvironmentComboBox comboBox = new EnvironmentComboBox(true);
    private final JButton btnEdit = new IconButton(
            EDIT_ICON,
            "Edit Environment"
    );

    private final EnvironmentSwitcherComboBoxController controller;

    public EnvironmentSwitcherComboBox(){
        ComponentUtil.checkIsEventDispatchThread();
        controller = new EnvironmentSwitcherComboBoxController(this);

        setLayout(new MigLayout(
                "insets 2",
                "[grow, fill][]"
        ));
        add(comboBox, "width ::100% - 32px");
        add(btnEdit);

        controller.addListeners();
        configureActionListener();

        btnEdit.addActionListener(event -> btnEditAction());
    }

    private void setBtnEditEnabledOrDisabled(){
        btnEdit.setEnabled(
                comboBox.getItemCount() > 0 &&
                        comboBox.getSelectedItem() != null
        );
    }

    private void btnEditAction(){
        setBtnEditEnabledOrDisabled();
        if(comboBox.getSelectedItem() == null) return;

        var id = comboBox.getSelectedItem().getId();
        var parent = this;

        new WaitExecution(this, btnEdit, () -> toFrontIfExists(
                EnvironmentEditFrame.class,
                () -> new EnvironmentEditFrame(parent, id),
                item -> Objects.equals(id, item.getEnvironment().getId())
        )).execute();
    }

    @Override
    public void refreshItems() {
        new WaitExecution(this, () -> {
            Arrays.stream(comboBox.getActionListeners())
                    .forEach(comboBox::removeActionListener);

            comboBox.loadItems();

            configureActionListener();
        }).execute();
    }

    private void configureActionListener() {
        new WaitExecution(this, () -> {
            setBtnEditEnabledOrDisabled();
            controller.refreshEnvironmentCurrentEnvsListener();
            comboBox.addActionListener(event ->
                    setWorkspaceSessionSelected());
        }).execute();
    }

    private void setWorkspaceSessionSelected() {
        new WaitExecution(this, () -> {
            controller.setWorkspaceSessionSelected(comboBox.getSelectedItem());
            setBtnEditEnabledOrDisabled();
            controller.refreshEnvironmentCurrentEnvsListener();
        }).execute();
    }

    @Override
    public String getPassword() {
        return PasswordDialog.showInputPassword(getWindowAncestor(this));
    }

    @Override
    public String getNewPassword() {
        return PasswordDialog.showInputNewPassword(getWindowAncestor(this));
    }
}
