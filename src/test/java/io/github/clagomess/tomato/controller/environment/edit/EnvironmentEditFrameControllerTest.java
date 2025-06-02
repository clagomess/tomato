package io.github.clagomess.tomato.controller.environment.edit;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.io.keystore.EnvironmentKeystore;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import io.github.clagomess.tomato.ui.environment.edit.EnvironmentEditFrameInterface;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.SECRET;
import static io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.TEXT;

class EnvironmentEditFrameControllerTest {
    @Nested
    class save {
        @Test
        void whenEmptyItemKeyRemove() throws IOException {
            var environment = new EnvironmentDto();
            environment.setEnvs(new ArrayList<>(List.of(
                new EnvironmentItemDto("foo", "bar"),
                new EnvironmentItemDto("", ""),
                new EnvironmentItemDto(null, null)
            )));

            var controller = new EnvironmentEditFrameController(
                    Mockito.mock(EnvironmentRepository.class),
                    Mockito.mock(WorkspaceRepository.class),
                    Mockito.mock(EnvironmentKeystore.class),
                    EnvironmentPublisher.getInstance(),
                    Mockito.mock(EnvironmentEditFrameInterface.class),
                    environment
            );

            controller.save();
            Assertions.assertThat(environment.getEnvs())
                    .containsExactly(new EnvironmentItemDto("foo", "bar"));
        }

        @Test
        void whenItemSecretWithEmpyValueAndId_ConvertToText() throws IOException {
            var environment = new EnvironmentDto();
            environment.setEnvs(new ArrayList<>(List.of(
                    new EnvironmentItemDto(SECRET, null, "foo", null)
            )));

            var controller = new EnvironmentEditFrameController(
                    Mockito.mock(EnvironmentRepository.class),
                    Mockito.mock(WorkspaceRepository.class),
                    Mockito.mock(EnvironmentKeystore.class),
                    EnvironmentPublisher.getInstance(),
                    Mockito.mock(EnvironmentEditFrameInterface.class),
                    environment
            );

            controller.save();

            Assertions.assertThat(environment.getEnvs())
                    .containsExactly(new EnvironmentItemDto(TEXT, null, "foo", null));
        }
    }
}
