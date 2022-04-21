package com.github.clagomess.tomato.util;

import com.github.clagomess.tomato.dto.WorkspaceDto;
import com.github.clagomess.tomato.fi.SwitchWorkspaceFI;
import com.github.clagomess.tomato.fi.TabRequestModificationHintFI;
import com.github.clagomess.tomato.ui.main.request.tabrequest.TabRequestUI;
import lombok.Getter;

import java.util.*;

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

    private final Map<TabRequestUI, TabRequestModificationHintFI> tabRequestModificationHintFIMap = new HashMap<>();
    public void notifyTabRequestModificationHintFIMap(TabRequestUI tab){
        tabRequestModificationHintFIMap.forEach((key, value) -> {
            if(Objects.equals(key, tab)){
                value.hintModification();
            }
        });
    }
}
