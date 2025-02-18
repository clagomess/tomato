package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.TreeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionComboBoxTest {
    private final TreeRepository treeRepository = Mockito.mock(TreeRepository.class);
    private final CollectionComboBox comboBoxMock = Mockito.spy(new CollectionComboBox(treeRepository));

    @Test
    public void addItens_assertSorted() throws IOException {
        var root = new CollectionTreeDto(){{
            setName("ROOT");
            setChildren(parent -> Stream.of(
                    new CollectionTreeDto(){{
                        setName("BBB");
                    }},
                    new CollectionTreeDto(){{
                        setName("AAA");
                    }}
            ).sorted());
        }};

        Mockito.doReturn(root)
                .when(treeRepository)
                .getWorkspaceCollectionTree();

        comboBoxMock.addItens(null);

        assertEquals(3, comboBoxMock.getItemCount());
        assertEquals("ROOT", comboBoxMock.getItemAt(0).getName());
        assertEquals("AAA", comboBoxMock.getItemAt(1).getName());
        assertEquals("BBB", comboBoxMock.getItemAt(2).getName());
    }
}
