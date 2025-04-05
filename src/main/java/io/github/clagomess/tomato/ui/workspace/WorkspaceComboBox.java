package io.github.clagomess.tomato.ui.workspace;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;

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
