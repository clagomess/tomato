package io.github.clagomess.tomato.controller.main.collection;

import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.TreeRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import io.github.clagomess.tomato.publisher.SystemPublisher;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class CollectionTreeController {
    protected UUID listenerUuid = null;
    private final TreeRepository treeRepository;
    private final WorkspaceSessionRepository workspaceSessionRepository;

    public CollectionTreeController() {
        treeRepository = new TreeRepository();
        this.workspaceSessionRepository = new WorkspaceSessionRepository();
    }

    public WorkspaceSessionDto loadWorkspaceSession() throws IOException {
        return workspaceSessionRepository.load();
    }

    public void addOnSwitchListener(Runnable loadCurrentWorkspace) {
        WorkspacePublisher.getInstance()
                .getOnSwitch()
                .addListener(event -> loadCurrentWorkspace.run());
    }

    public void addSaveTreeExpandedStateListener(
            Supplier<List<TomatoID>> expandedCollectionsIds
    ){
        WorkspacePublisher.getInstance()
                .getOnBeforeSwitch()
                .addListener(e -> saveTreeExpandedState(expandedCollectionsIds.get()));

        SystemPublisher.getInstance()
                .getOnClosing()
                .addListener(e -> saveTreeExpandedState(expandedCollectionsIds.get()));
    }

    protected void saveTreeExpandedState(List<TomatoID> expandedCollectionsIds){
        try {
            var session = workspaceSessionRepository.load();
            session.setExpandedCollectionsIds(expandedCollectionsIds);
            workspaceSessionRepository.save(session);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public CollectionTreeDto loadCurrentWorkspace(Consumer<WorkspaceDto> onChange) throws IOException {
        if(listenerUuid != null) {
            WorkspacePublisher.getInstance()
                    .getOnChange()
                    .removeListener(listenerUuid);
        }

        var rootCollection = treeRepository.getWorkspaceCollectionTree();

        listenerUuid = WorkspacePublisher.getInstance().getOnChange().addListener(
                rootCollection.getId(),
                event -> onChange.accept(event.getEvent())
        );

        return rootCollection;
    }
}
