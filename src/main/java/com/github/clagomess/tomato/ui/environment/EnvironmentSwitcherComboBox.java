package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.controller.environment.EnvironmentComboBoxController;
import com.github.clagomess.tomato.ui.component.*;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import com.github.clagomess.tomato.ui.environment.edit.EnvironmentEditFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static com.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;
import static javax.swing.SwingUtilities.*;

public class EnvironmentSwitcherComboBox extends JPanel {
    private final EnvironmentComboBox comboBox = new EnvironmentComboBox();
    private final JButton btnEdit = new IconButton(new BxEditIcon(), "Edit Environment");

    private final EnvironmentComboBoxController controller = new EnvironmentComboBoxController();

    public EnvironmentSwitcherComboBox(){
        ComponentUtil.checkIsEventDispatchThread();

        setLayout(new MigLayout(
                "insets 2",
                "[grow, fill][]"
        ));
        add(comboBox, "width ::100% - 32px");
        add(btnEdit);

        refreshItems();

        controller.addEnvironmentOnChangeListener(() -> invokeLater(this::refreshItems));
        controller.addWorkspaceOnSwitchListener(() -> invokeLater(this::refreshItems));

        btnEdit.addActionListener(event -> {
            setBtnEditEnabledOrDisabled();
            if(comboBox.getSelectedItem() == null) return;

            new WaitExecution(this, btnEdit, () -> btnEditAction(
                    this,
                    comboBox.getSelectedItem().getId()
            )).execute();
        });
    }

    private void refreshItems() {
        ComponentUtil.checkIsEventDispatchThread();

        Arrays.stream(comboBox.getActionListeners())
                .forEach(comboBox::removeActionListener);
        comboBox.removeAllItems();

        // add empty option
        comboBox.addItem(null);
        comboBox.setSelectedItem(null);

        ForkJoinPool.commonPool().submit(() -> {
            try {
                controller.loadItems(
                        (selected, item) -> invokeLater(() -> {
                            comboBox.addItem(item);
                            if(selected) comboBox.setSelectedItem(item);
                        }),
                        () -> new WaitExecution(this, () -> {
                            setBtnEditEnabledOrDisabled();
                            refreshEnvironmentCurrentEnvsListener();
                            comboBox.addActionListener(event ->
                                    setWorkspaceSessionSelected());
                        }).execute()
                );
            } catch (Throwable e){
                invokeLater(() -> new ExceptionDialog(this, e));
            }
        });
    }

    private void setBtnEditEnabledOrDisabled(){
        btnEdit.setEnabled(
                comboBox.getItemCount() > 0 &&
                comboBox.getSelectedItem() != null
        );
    }

    private void setWorkspaceSessionSelected() {
        new WaitExecution(this, () -> {
            controller.setWorkspaceSessionSelected(comboBox.getSelectedItem());
            setBtnEditEnabledOrDisabled();
            refreshEnvironmentCurrentEnvsListener();
        }).execute();
    }

    private void refreshEnvironmentCurrentEnvsListener() throws IOException {
        Supplier<String> getPassword = () -> {
            if(isEventDispatchThread()){
                return new PasswordDialog(this).showDialog();
            }

            var result = new AtomicReference<String>();
            try {
                invokeAndWait(() -> result.set(
                        new PasswordDialog(this).showDialog()
                ));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return result.get();
        };

        Supplier<String> getNewPassword = () -> {
            if(isEventDispatchThread()){
                return new NewPasswordDialog(this).showDialog();
            }

            var result = new AtomicReference<String>();
            try {
                invokeAndWait(() -> result.set(
                        new NewPasswordDialog(this).showDialog()
                ));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return result.get();
        };

        controller.refreshEnvironmentCurrentEnvsListener(
                getPassword,
                getNewPassword
        );
    }

    private void btnEditAction(
            Container parent,
            String id
    ) throws IOException {
        toFrontIfExists(
                EnvironmentEditFrame.class,
                () -> new EnvironmentEditFrame(parent, id),
                item -> Objects.equals(id, item.getEnvironment().getId())
        );
    }
}
