package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.RepositoryStubs;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestDeleteUITest extends RepositoryStubs {
    private final RequestRepository requestRepository = new RequestRepository();

    @Test
    public void delete() throws IOException {
        var request = new RequestDto();
        var result = requestRepository.save(mockDataDir, request);
        CollectionTreeDto collectionTree = new CollectionTreeDto();
        collectionTree.setPath(result.getParentFile());

        var head = requestRepository.getRequestList(collectionTree)
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

        new RequestDeleteUI(null, head).delete();

        assertTrue(hasPublished.get());
    }
}
