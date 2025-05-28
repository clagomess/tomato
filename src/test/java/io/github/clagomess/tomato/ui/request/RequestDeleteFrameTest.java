package io.github.clagomess.tomato.ui.request;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.RepositoryStubs;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.io.repository.TreeRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestDeleteFrameTest extends RepositoryStubs {
    private final RequestRepository requestRepository = new RequestRepository();
    private final TreeRepository treeRepository = new TreeRepository();

    @Test
    void delete() throws IOException {
        var request = new RequestDto();
        var result = requestRepository.save(mockDataDir, request);
        CollectionTreeDto collectionTree = new CollectionTreeDto();
        collectionTree.setPath(result.getParentFile());

        var head = treeRepository.getRequestList(collectionTree)
                .filter(item -> item.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow();

        var hasPublished = new AtomicBoolean(false);

        RequestPublisher.getInstance().getOnChange()
                .addListener(new RequestKey(collectionTree.getId(), request.getId()), event -> {
                    hasPublished.set(true);

                    assertEquals(DELETED, event.getType());

                    Assertions.assertThat(event.getEvent().getPath())
                            .doesNotExist();
                });

        new RequestDeleteFrame(null, head).delete();

        assertTrue(hasPublished.get());
    }
}
