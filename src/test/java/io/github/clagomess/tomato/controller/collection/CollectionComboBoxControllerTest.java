package io.github.clagomess.tomato.controller.collection;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.TreeRepository;
import io.github.clagomess.tomato.ui.collection.CollectionComboBoxMock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionComboBoxControllerTest {
    private final CollectionComboBoxMock ui = new CollectionComboBoxMock();
    private final TreeRepository treeRepositoryMock = Mockito.mock(TreeRepository.class);
    private final CollectionComboBoxController controller = new CollectionComboBoxController(
            treeRepositoryMock, ui
    );

    @Test
    void loadItems_assertSorted() throws IOException {
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
                .when(treeRepositoryMock)
                .getWorkspaceCollectionTree();

        controller.loadItems(null);

        assertEquals(3, ui.items.size());
        assertEquals("ROOT", ui.items.get(0).getName());
        assertEquals("AAA", ui.items.get(1).getName());
        assertEquals("BBB", ui.items.get(2).getName());
    }
}
