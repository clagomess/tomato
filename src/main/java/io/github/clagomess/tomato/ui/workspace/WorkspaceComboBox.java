package io.github.clagomess.tomato.ui.workspace;

import io.github.clagomess.tomato.controller.workspace.WorkspaceComboBoxController;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;

import javax.swing.*;

public class WorkspaceComboBox
        extends JComboBox<WorkspaceDto>
        implements WorkspaceComboBoxInterface {

    public WorkspaceComboBox() {
        setRenderer(new DtoListCellRenderer<>(WorkspaceDto::getName));
        SwingUtilities.invokeLater(this::loadItems);
    }

    private void loadItems() {
        try {
            new WorkspaceComboBoxController(this).loadItems();
        } catch (Throwable e){
            new ExceptionDialog(this, e);
        }
    }

    @Override
    public WorkspaceDto getSelectedItem() {
        return (WorkspaceDto) super.getSelectedItem();
    }
}
