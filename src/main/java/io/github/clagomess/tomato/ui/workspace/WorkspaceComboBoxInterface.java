package io.github.clagomess.tomato.ui.workspace;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;

public interface WorkspaceComboBoxInterface {
    void setSelectedItem(Object anObject);
    void addItem(WorkspaceDto item);
}
