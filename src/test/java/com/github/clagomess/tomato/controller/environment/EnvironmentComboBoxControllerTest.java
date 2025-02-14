package com.github.clagomess.tomato.controller.environment;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import com.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvironmentComboBoxControllerTest {
    private final EnvironmentRepository environmentRepositoryMock = Mockito.mock(EnvironmentRepository.class);
    private final WorkspaceSessionRepository workspaceSessionRepositoryMock = Mockito.mock(WorkspaceSessionRepository.class);
    private final EnvironmentComboBoxController controller = Mockito.spy(new EnvironmentComboBoxController(
            environmentRepositoryMock,
            workspaceSessionRepositoryMock
    ));

    @BeforeEach
    public void setup(){
        Mockito.reset(environmentRepositoryMock);
        Mockito.reset(workspaceSessionRepositoryMock);
        Mockito.reset(controller);
    }

    @Test
    public void addEnvironmentOnChangeListener_trigger(){
        var triggered = new AtomicBoolean(false);

        controller.addEnvironmentOnChangeListener(() -> triggered.set(true));

        EnvironmentPublisher.getInstance()
                .getOnChange()
                .publish(new PublisherEvent<>(
                        UPDATED,
                        "foo"
                ));

        assertTrue(triggered.get());
    }

    @Test
    public void addWorkspaceOnSwitchListener_trigger(){
        var triggered = new AtomicBoolean(false);

        controller.addWorkspaceOnSwitchListener(() -> triggered.set(true));
        WorkspacePublisher.getInstance()
                .getOnSwitch()
                .publish(new WorkspaceDto());

        assertTrue(triggered.get());
    }

    @Test
    public void loadItems() throws IOException {
        var session = new WorkspaceSessionDto();
        session.setEnvironmentId("aaa");

        var itemSelected = new EnvironmentHeadDto();
        itemSelected.setId("aaa");

        var itemNonSelected = new EnvironmentHeadDto();
        itemNonSelected.setId("bbb");

        Mockito.doReturn(session)
                .when(workspaceSessionRepositoryMock)
                .load();

        Mockito.doReturn(List.of(itemNonSelected, itemSelected))
                .when(environmentRepositoryMock)
                .listHead();

        var result = new HashMap<Boolean, EnvironmentHeadDto>();
        var completeTriggered = new AtomicBoolean(false);

        controller.loadItems(
                result::put,
                () -> completeTriggered.set(true)
        );

        assertTrue(completeTriggered.get());
        Assertions.assertThat(result)
                .hasSize(2)
                .containsEntry(true, itemSelected)
                .containsEntry(false, itemNonSelected)
        ;
    }

    @Test
    public void setWorkspaceSessionSelected_whenEquals_doNothing() throws IOException {
        Mockito.doReturn(new WorkspaceSessionDto())
                .when(workspaceSessionRepositoryMock)
                .load();

        Mockito.doThrow(new AssertionError())
                .when(workspaceSessionRepositoryMock)
                .save(Mockito.any());

        controller.setWorkspaceSessionSelected(null);
    }

    @Test
    public void setWorkspaceSessionSelected_whenDiferent_trigger() throws IOException {
        WorkspaceSessionPublisher.getInstance()
                .getOnChange()
                .getListeners()
                .clear();

        Mockito.doReturn(new WorkspaceSessionDto())
                .when(workspaceSessionRepositoryMock)
                .load();

        var triggered = new AtomicBoolean(false);
        WorkspaceSessionPublisher.getInstance()
                .getOnChange()
                .addListener(event -> triggered.set(true));

        var itemSelected = new EnvironmentHeadDto();
        itemSelected.setId("aaa");
        controller.setWorkspaceSessionSelected(itemSelected);

        assertTrue(triggered.get());
    }
}
