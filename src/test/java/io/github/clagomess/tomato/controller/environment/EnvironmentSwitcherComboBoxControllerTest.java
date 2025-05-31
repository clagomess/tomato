package io.github.clagomess.tomato.controller.environment;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import io.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.ui.environment.EnvironmentSwitcherComboBoxInterface;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnvironmentSwitcherComboBoxControllerTest {
    private final EnvironmentRepository environmentRepositoryMock = Mockito.mock(EnvironmentRepository.class);
    private final WorkspaceRepository workspaceRepositoryMock = Mockito.mock(WorkspaceRepository.class);
    private final WorkspaceSessionRepository workspaceSessionRepositoryMock = Mockito.mock(WorkspaceSessionRepository.class);
    private final EnvironmentSwitcherComboBoxInterface ui = Mockito.mock(EnvironmentSwitcherComboBoxInterface.class);
    private final EnvironmentSwitcherComboBoxController controller = Mockito.spy(new EnvironmentSwitcherComboBoxController(
            environmentRepositoryMock,
            workspaceRepositoryMock,
            workspaceSessionRepositoryMock,
            WorkspacePublisher.getInstance(),
            WorkspaceSessionPublisher.getInstance(),
            EnvironmentPublisher.getInstance(),
            ui
    ));

    @BeforeEach
    void setup(){
        Mockito.reset(environmentRepositoryMock);
        Mockito.reset(workspaceRepositoryMock);
        Mockito.reset(workspaceSessionRepositoryMock);
        Mockito.reset(controller);
        Mockito.reset(ui);
    }

    @Nested
    class addListeners {
        AtomicBoolean triggered = new AtomicBoolean(false);

        @BeforeEach
        void setup(){
            triggered.set(false);

            controller.addListeners();

            Mockito.doAnswer(a -> {
                triggered.set(true);
                return null;
            }).when(ui).refreshItems();
        }

        @Test
        void trigger_getOnChange(){
            EnvironmentPublisher.getInstance()
                    .getOnChange()
                    .publish(new PublisherEvent<>(
                            UPDATED,
                            "foo"
                    ));

            assertTrue(triggered.get());
        }

        @Test
        void trigger_getOnSwitch(){
            WorkspacePublisher.getInstance()
                    .getOnSwitch()
                    .publish(new WorkspaceDto());

            assertTrue(triggered.get());
        }
    }

    @Nested
    class refreshEnvironmentCurrentEnvsListener {
        @BeforeEach
        void setup() throws IOException {
            var wsSession = new WorkspaceSessionDto();
            wsSession.setEnvironmentId("aaa");

            Mockito.doReturn(wsSession)
                    .when(workspaceSessionRepositoryMock)
                    .load();

            var workspace = new WorkspaceDto();
            Mockito.doReturn(workspace)
                    .when(workspaceRepositoryMock)
                    .getDataSessionWorkspace();
        }

        @Test
        void whenHasEnvironment() throws IOException {
            var environment = new EnvironmentDto();
            environment.setEnvs(List.of(
                    new EnvironmentItemDto("foo", "bar")
            ));
            Mockito.doReturn(Optional.of(environment))
                    .when(environmentRepositoryMock)
                    .getWorkspaceSessionEnvironment();

            controller.refreshEnvironmentCurrentEnvsListener();

            var result = EnvironmentPublisher.getInstance()
                    .getCurrentEnvs()
                    .request();

            Assertions.assertThat(result).isNotEmpty();
        }

        @Test
        void whenNoEnvironment() throws IOException {
            Mockito.doReturn(Optional.empty())
                    .when(environmentRepositoryMock)
                    .getWorkspaceSessionEnvironment();

            controller.refreshEnvironmentCurrentEnvsListener();

            var result = EnvironmentPublisher.getInstance()
                    .getCurrentEnvs()
                    .request();

            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    class setWorkspaceSessionSelected {
        @BeforeEach
        void setup() throws IOException {
            WorkspaceSessionPublisher.getInstance()
                    .getOnChange()
                    .getListeners()
                    .clear();

            Mockito.doReturn(new WorkspaceSessionDto())
                    .when(workspaceSessionRepositoryMock)
                    .load();
        }

        @Test
        void whenEquals_doNothing() throws IOException {
            Mockito.doThrow(new AssertionError())
                    .when(workspaceSessionRepositoryMock)
                    .save(Mockito.any());

            controller.setWorkspaceSessionSelected(null);
        }

        @Test
        void whenDiferent_trigger() throws IOException {
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
}
