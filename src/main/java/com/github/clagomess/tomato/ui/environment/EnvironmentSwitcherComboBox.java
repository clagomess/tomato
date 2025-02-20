package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.controller.environment.EnvironmentComboBoxController;
import com.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import com.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import com.github.clagomess.tomato.ui.environment.edit.EnvironmentEditFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import static javax.swing.SwingUtilities.invokeLater;

public class EnvironmentSwitcherComboBox extends JPanel {
    private final ComboBox comboBox = new ComboBox();
    private final JButton btnEdit = new IconButton(new BxEditIcon(), "Edit Environment");

    private final EnvironmentComboBoxController controller = new EnvironmentComboBoxController();

    public EnvironmentSwitcherComboBox(){
        setLayout(new MigLayout(
                "insets 2",
                "[grow, fill][]"
        ));
        add(comboBox, "width ::100% - 32px");
        add(btnEdit);

        invokeLater(this::refreshItems);

        controller.addEnvironmentOnChangeListener(() -> invokeLater(this::refreshItems));
        controller.addWorkspaceOnSwitchListener(() -> invokeLater(this::refreshItems));

        btnEdit.addActionListener(event -> {
            setBtnEditEnabledOrDisabled();
            if(comboBox.getSelectedItem() == null) return;

            new WaitExecution(this, btnEdit, () -> new EnvironmentEditFrame(
                    this,
                    comboBox.getSelectedItem().getId()
            )).execute();
        });
    }

    private void refreshItems() {
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
                        () -> invokeLater(() -> {
                            setBtnEditEnabledOrDisabled();
                            comboBox.addActionListener(event ->
                                    setWorkspaceSessionSelected());
                        })
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
        }).execute();
    }

    private static class ComboBox extends JComboBox<EnvironmentHeadDto> {
        public ComboBox() {
            setRenderer(new DtoListCellRenderer<>(EnvironmentHeadDto::getName));
        }

        @Override
        public EnvironmentHeadDto getSelectedItem() {
            return (EnvironmentHeadDto) super.getSelectedItem();
        }
    }
}
