package com.github.clagomess.tomato.util;

import com.github.clagomess.tomato.dto.WorkspaceDto;
import com.github.clagomess.tomato.fi.SwitchWorkspaceFI;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class UIPublisherUtil {
    private UIPublisherUtil() {}
    private static final UIPublisherUtil instance = new UIPublisherUtil();
    public synchronized static UIPublisherUtil getInstance(){
        return instance;
    }

    private final List<SwitchWorkspaceFI> switchWorkspaceFIList = new ArrayList<>();
    public void notifySwitchWorkspaceSubscribers(WorkspaceDto currentWorkspace){
        switchWorkspaceFIList.forEach(item -> item.switchWorkspace(currentWorkspace));
    }
}
