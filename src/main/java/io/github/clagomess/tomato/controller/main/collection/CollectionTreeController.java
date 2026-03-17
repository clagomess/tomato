package io.github.clagomess.tomato.controller.main.collection;

import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.TreeRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import io.github.clagomess.tomato.publisher.SystemPublisher;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import io.github.clagomess.tomato.ui.main.collection.node.CollectionTreeNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

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

    // @TODO: impl. junit
    public void addSaveTreeExpandedStateListener(
            JTree tree
    ){
        WorkspacePublisher.getInstance()
                .getOnBeforeSwitch()
                .addListener(e -> saveTreeExpandedState(tree));

        SystemPublisher.getInstance()
                .getOnClosing()
                .addListener(e -> saveTreeExpandedState(tree));
    }

    // @TODO: impl. junit
    protected void saveTreeExpandedState(JTree tree){
        try {
            Set<TomatoID> expandedCollectionsIds = new TreeSet<>();

            Enumeration<TreePath> expanded = tree.getExpandedDescendants(
                    new TreePath(tree.getModel().getRoot())
            );

            while (expanded != null && expanded.hasMoreElements()) {
                TreePath path = expanded.nextElement();

                if(path.getLastPathComponent() instanceof CollectionTreeNode treeNode){
                    expandedCollectionsIds.add(treeNode.getCollection().getId());
                }
            }

            var session = workspaceSessionRepository.load();
            session.setExpandedCollectionsIds(expandedCollectionsIds.stream().toList());
            workspaceSessionRepository.save(session);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
