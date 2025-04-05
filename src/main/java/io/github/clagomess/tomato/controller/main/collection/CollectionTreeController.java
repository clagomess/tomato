package io.github.clagomess.tomato.controller.main.collection;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.TreeRepository;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public class CollectionTreeController {
    protected UUID listenerUuid = null;
    private final TreeRepository treeRepository;

    public CollectionTreeController() {
        treeRepository = new TreeRepository();
    }

    public void addOnSwitchListener(Runnable loadCurrentWorkspace) {
        WorkspacePublisher.getInstance()
                .getOnSwitch()
                .addListener(event -> loadCurrentWorkspace.run());
    }

    public CollectionTreeDto loadCurrentWorkspace(OnChangeFI onChange) throws IOException {
        if(listenerUuid != null) {
            WorkspacePublisher.getInstance()
                    .getOnChange()
                    .removeListener(listenerUuid);
        }

        var rootCollection = treeRepository.getWorkspaceCollectionTree();

        listenerUuid = WorkspacePublisher.getInstance().getOnChange().addListener(
                rootCollection.getId(),
                event -> onChange.update(event.getEvent())
        );

        return rootCollection;
    }

    @FunctionalInterface
    public interface OnChangeFI {
        void update(WorkspaceDto workspace);
    }
}
