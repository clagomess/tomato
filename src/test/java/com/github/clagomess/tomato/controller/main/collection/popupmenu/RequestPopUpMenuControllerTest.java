package com.github.clagomess.tomato.controller.main.collection.popupmenu;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.RepositoryStubs;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.io.repository.TreeRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.ParentCollectionKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;
import static org.junit.jupiter.api.Assertions.*;

public class RequestPopUpMenuControllerTest extends RepositoryStubs {
    private final RequestRepository requestRepository = new RequestRepository();
    private final TreeRepository treeRepository = new TreeRepository();

    @Test
    public void duplicate() throws IOException {
        var request = new RequestDto();
        var result = requestRepository.save(mockDataDir, request);
        CollectionTreeDto collectionTree = new CollectionTreeDto();
        collectionTree.setId("c_a");
        collectionTree.setPath(result.getParentFile());
        var head = treeRepository.getRequestList(collectionTree)
                .filter(item -> item.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow();

        var hasPublished = new AtomicBoolean(false);

        RequestPublisher.getInstance().getOnParentCollectionChange()
                .addListener(new ParentCollectionKey("c_a"), event -> {
                    hasPublished.set(true);

                    assertEquals(INSERTED, event.getType());
                    Assertions.assertThat(event.getEvent().getId())
                            .isNotEqualTo(head.getId());
                    Assertions.assertThat(event.getEvent().getName())
                            .contains("Copy");

                    Assertions.assertThat(event.getEvent().getPath())
                            .isFile()
                            .isNotEqualTo(head.getPath());

                    assertNotSame(head, event.getEvent());
                });

        var controller = new RequestPopUpMenuController();
        controller.duplicate(head);

        assertTrue(hasPublished.get());
    }
}
