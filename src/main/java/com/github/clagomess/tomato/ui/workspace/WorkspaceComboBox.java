package com.github.clagomess.tomato.ui.workspace;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;

import javax.swing.*;
import java.util.concurrent.ForkJoinPool;

public class WorkspaceComboBox extends JComboBox<WorkspaceDto> {
    private final WorkspaceRepository workspaceRepository = new WorkspaceRepository();

    public WorkspaceComboBox() {
        setRenderer(new DtoListCellRenderer<>(WorkspaceDto::getName));
        ForkJoinPool.commonPool().submit(this::addItens);
    }

    private void addItens() {
        try {
            workspaceRepository.list().forEach(this::addItem);
            setSelectedItem(workspaceRepository.getDataSessionWorkspace());
        } catch (Throwable e){
            new ExceptionDialog(this, e);
        }
    }

    @Override
    public WorkspaceDto getSelectedItem() {
        return (WorkspaceDto) super.getSelectedItem();
    }
}
