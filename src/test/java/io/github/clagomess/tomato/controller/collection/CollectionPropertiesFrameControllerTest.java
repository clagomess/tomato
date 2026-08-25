package io.github.clagomess.tomato.controller.collection;

import io.github.clagomess.tomato.dto.CollectionPropertiesDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.CollectionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionPropertiesFrameControllerTest {
    private final CollectionRepository collectionRepositoryMock = Mockito.mock(CollectionRepository.class);
    private final CollectionPropertiesFrameController controller = new CollectionPropertiesFrameController(
            collectionRepositoryMock
    );

    @Test
    void properties() {
        var collectionTree = new CollectionTreeDto();
        var expected = new CollectionPropertiesDto(
                "AAAAAAAA",
                "my-collection",
                "/home/.tomato/data/collection-AAAAAAAA",
                "512B",
                "3",
                "2025-01-02 03:04:05"
        );

        Mockito.doReturn(expected)
                .when(collectionRepositoryMock)
                .properties(collectionTree);

        assertEquals(expected, controller.properties(collectionTree));
    }
}
