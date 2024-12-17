package com.github.clagomess.tomato.ui.workspace;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.service.WorkspaceDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;

import javax.swing.*;
import java.awt.*;

public class WorkspaceComboBox extends JComboBox<WorkspaceDto> {
    private final WorkspaceDataService workspaceDataService = new WorkspaceDataService();

    public WorkspaceComboBox() {
        setRenderer(new NameRenderer());
        SwingUtilities.invokeLater(this::addItens);
    }

    private void addItens() {
        try {
            workspaceDataService.listWorkspaces().forEach(this::addItem);
            setSelectedItem(workspaceDataService.getDataSessionWorkspace());
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }
    }

    @Override
    public WorkspaceDto getSelectedItem() {
        return (WorkspaceDto) super.getSelectedItem();
    }

    protected static class NameRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
            );

            if(value != null){
                label.setText(((WorkspaceDto) value).getName());
            }

            return label;
        }
    }
}
