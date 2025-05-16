package io.github.clagomess.tomato.ui.environment;

import io.github.clagomess.tomato.controller.environment.EnvironmentSwitcherComboBoxController;
import io.github.clagomess.tomato.ui.component.*;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import io.github.clagomess.tomato.ui.environment.edit.EnvironmentEditFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;
import static javax.swing.SwingUtilities.*;

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
        if(isEventDispatchThread()){
            return new PasswordDialog(getWindowAncestor(this))
                    .showDialog();
        }

        try {
            var result = new AtomicReference<String>();
            invokeAndWait(() -> result.set(
                    new PasswordDialog(getWindowAncestor(this))
                            .showDialog()
            ));
            return result.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNewPassword() {
        if(isEventDispatchThread()){
            new NewPasswordDialog(getWindowAncestor(this))
                    .showDialog();
        }

        try {
            var result = new AtomicReference<String>();
            invokeAndWait(() -> result.set(
                    new NewPasswordDialog(getWindowAncestor(this))
                            .showDialog()
            ));
            return result.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
