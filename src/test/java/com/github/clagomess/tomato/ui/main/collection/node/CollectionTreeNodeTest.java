package com.github.clagomess.tomato.ui.main.collection.node;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class CollectionTreeNodeTest {
    private final DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("ROOT"));

    private final CollectionTreeDto collectionTree = new CollectionTreeDto(){{
        setName("ROOT");
        setChildren(parent -> Stream.of(
                new CollectionTreeDto(){{
                    setName("LEVEL 1 - B");
                }},
                new CollectionTreeDto(){{
                    setName("LEVEL 1 - A");
                    setChildren(parent -> Stream.of(
                            new CollectionTreeDto(){{
                                setName("LEVEL 2 - A");
                            }}
                    ));
                }}
        ).sorted());
        setRequests(parent -> Stream.of(
                new RequestHeadDto(){{
                    setName("LEVEL 1 - D");
                    setParent(parent);
                }},
                new RequestHeadDto(){{
                    setName("LEVEL 1 - C");
                    setParent(parent);
                }}
        ).sorted());
    }};

    @Test
    public void loadChildren(){
        var rootNode = new CollectionTreeNode(treeModel, collectionTree);
        treeModel.setRoot(rootNode);

        try(var msSwing = Mockito.mockStatic(SwingUtilities.class)) {
            msSwing.when(SwingUtilities::isEventDispatchThread)
                    .thenReturn(true);

            rootNode.loadChildren();
        }

        assertEquals(4, rootNode.getChildCount());
    }

    @Test
    public void loadChildren_assertSortedCollections(){
        var rootNode = new CollectionTreeNode(treeModel, collectionTree);
        treeModel.setRoot(rootNode);
        try(var msSwing = Mockito.mockStatic(SwingUtilities.class)) {
            msSwing.when(SwingUtilities::isEventDispatchThread)
                    .thenReturn(true);

            rootNode.loadChildren();
        }

        var result = IntStream.range(0, rootNode.getChildCount())
                .mapToObj(rootNode::getChildAt)
                .filter(item -> item instanceof CollectionTreeNode)
                .map(item -> (CollectionTreeNode) item)
                .map(item -> item.getTree().getName())
                .toList();

        assertEquals("LEVEL 1 - A", result.get(0));
        assertEquals("LEVEL 1 - B", result.get(1));
    }

    @Test
    public void loadChildren_assertSortedRequests(){
        var rootNode = new CollectionTreeNode(treeModel, collectionTree);
        treeModel.setRoot(rootNode);
        try(var msSwing = Mockito.mockStatic(SwingUtilities.class)) {
            msSwing.when(SwingUtilities::isEventDispatchThread)
                    .thenReturn(true);

            rootNode.loadChildren();
        }

        var result = IntStream.range(0, rootNode.getChildCount())
                .mapToObj(rootNode::getChildAt)
                .filter(item -> item instanceof RequestTreeNode)
                .map(item -> (RequestTreeNode) item)
                .map(item -> ((RequestHeadDto) item.getUserObject()).getName())
                .toList();

        assertEquals("LEVEL 1 - C", result.get(0));
        assertEquals("LEVEL 1 - D", result.get(1));
    }
}
