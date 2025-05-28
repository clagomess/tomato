package io.github.clagomess.tomato.controller.environment;

import io.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import io.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import io.github.clagomess.tomato.ui.environment.EnvironmentComboBoxInterface;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvironmentComboBoxControllerTest {
    private final EnvironmentRepository environmentRepositoryMock = Mockito.mock(EnvironmentRepository.class);
    private final WorkspaceSessionRepository workspaceSessionRepositoryMock = Mockito.mock(WorkspaceSessionRepository.class);
    private final EnvironmentComboBoxInterface ui = Mockito.mock(EnvironmentComboBoxInterface.class);

    private final EnvironmentComboBoxController controller = Mockito.spy(new EnvironmentComboBoxController(
            workspaceSessionRepositoryMock,
            environmentRepositoryMock,
            ui
    ));

    @Test
    void loadItems() throws IOException {
        var session = new WorkspaceSessionDto();
        session.setEnvironmentId("aaa");

        var itemSelected = new EnvironmentHeadDto();
        itemSelected.setId("aaa");

        var itemNonSelected = new EnvironmentHeadDto();
        itemNonSelected.setId("bbb");

        // ---
        Mockito.doReturn(session)
                .when(workspaceSessionRepositoryMock)
                .load();

        Mockito.doReturn(List.of(itemNonSelected, itemSelected))
                .when(environmentRepositoryMock)
                .listHead();

        var resultItems = new ArrayList<EnvironmentHeadDto>();
        Mockito.doAnswer(a -> {
            resultItems.add(a.getArgument(0));
            return null;
        }).when(ui).addItem(Mockito.any(EnvironmentHeadDto.class));

        AtomicReference<EnvironmentHeadDto> resultSelected = new AtomicReference<>();
        Mockito.doAnswer(a -> {
            resultSelected.set(a.getArgument(0));
            return null;
        }).when(ui).setSelectedItem(Mockito.any(EnvironmentHeadDto.class));
        // ---

        controller.loadItems();

        Assertions.assertThat(resultItems)
                .hasSize(2);
        assertEquals(itemSelected, resultSelected.get());
    }
}
