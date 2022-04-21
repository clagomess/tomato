package com.github.clagomess.tomato.fi;

import com.github.clagomess.tomato.dto.WorkspaceDto;

@FunctionalInterface
public interface SwitchWorkspaceFI {
    void switchWorkspace(WorkspaceDto currentWorkspace);
}
