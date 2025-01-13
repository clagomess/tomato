package com.github.clagomess.tomato.ui.main.collection.node;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class CollectionTreeNodeTest {
    private final DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("ROOT"));

    private final CollectionTreeDto collectionTree = new CollectionTreeDto(){{
        setName("ROOT");
        setChildren(parent -> Stream.of(
                new CollectionTreeDto(){{
                    setName("LEVEL 1 - A");
                }},
                new CollectionTreeDto(){{
                    setName("LEVEL 1 - B");
                    setChildren(parent -> Stream.of(
                            new CollectionTreeDto(){{
                                setName("LEVEL 2 - A");
                            }}
                    ));
                }}
        ));
    }};

    @Test
    public void loadChildren(){
        var rootNode = new CollectionTreeNode(treeModel, collectionTree);
        treeModel.setRoot(rootNode);
        rootNode.loadChildren();

        assertEquals(2, rootNode.getChildCount());
    }
}
