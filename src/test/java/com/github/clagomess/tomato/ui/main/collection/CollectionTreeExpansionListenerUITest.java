package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class CollectionTreeExpansionListenerUITest {
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROOT");
    private final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

    private CollectionTreeDto collectionTree = new CollectionTreeDto(){{
        setName("ROOT");
        setChildren(Stream.of(
                new CollectionTreeDto(){{
                    setName("LEVEL 1 - A");
                    setChildren(Stream.empty());
                }},
                new CollectionTreeDto(){{
                    setName("LEVEL 1 - B");
                    setChildren(Stream.of(
                            new CollectionTreeDto(){{
                                setName("LEVEL 2 - A");
                                setChildren(Stream.empty());
                            }}
                    ));
                }}
        ));
    }};

    @Test
    public void createLeaf(){
        var rootNode = new DefaultMutableTreeNode("ROOT");
        var treeModel = new DefaultTreeModel(rootNode);

        var treeExpansionListener = new CollectionTreeExpansionListenerUI(treeModel);
        treeExpansionListener.createLeaf(rootNode, collectionTree);

        assertEquals(2, rootNode.getChildCount());
    }
}
