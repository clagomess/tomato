package com.github.clagomess.tomato.controller.main.collection;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.TreeRepository;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionTreeControllerTest {
    private final TreeRepository treeRepository = Mockito.mock(TreeRepository.class);
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    private final CollectionTreeController controller = Mockito.spy(new CollectionTreeController(
            treeRepository
    ));

    @BeforeEach
    public void setup(){
        Mockito.reset(treeRepository);
        Mockito.reset(controller);
    }

    @Test
    public void addOnSwitchListener_trigger(){
        AtomicInteger result = new AtomicInteger();

        controller.addOnSwitchListener(result::incrementAndGet);

        workspacePublisher.getOnSwitch().publish(new WorkspaceDto());

        assertEquals(1, result.get());
    }

    @Test
    public void loadCurrentWorkspace_assertRemoveOnChangeListener() throws IOException {
        Mockito.doReturn(new CollectionTreeDto())
                .when(treeRepository)
                .getWorkspaceCollectionTree();

        var key = RandomStringUtils.secure().nextAlphanumeric(8);
        AtomicInteger result = new AtomicInteger();

        controller.listenerUuid = workspacePublisher.getOnChange()
                .addListener(
                        key,
                        e -> result.incrementAndGet()
                );

        controller.loadCurrentWorkspace(e -> {});

        workspacePublisher.getOnChange().publish(key, new PublisherEvent<>(
                UPDATED,
                new WorkspaceDto()
        ));

        assertEquals(0, result.get());
    }

    @Test
    public void loadCurrentWorkspace_assertAddOnChangeListener() throws IOException {
        var tree = new CollectionTreeDto();
        tree.setId(RandomStringUtils.secure().nextAlphanumeric(8));

        Mockito.doReturn(tree)
                .when(treeRepository)
                .getWorkspaceCollectionTree();

        AtomicInteger result = new AtomicInteger();

        controller.loadCurrentWorkspace(e -> result.incrementAndGet());

        workspacePublisher.getOnChange().publish(tree.getId(), new PublisherEvent<>(
                UPDATED,
                new WorkspaceDto()
        ));

        assertEquals(1, result.get());
    }
}
