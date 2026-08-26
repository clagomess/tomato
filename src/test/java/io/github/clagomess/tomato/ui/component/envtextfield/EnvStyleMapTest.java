package io.github.clagomess.tomato.ui.component.envtextfield;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static io.github.clagomess.tomato.io.repository.EnvironmentRepository.SYSENV_COLLECTION_FILE_DIR_KEY;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class EnvStyleMapTest {
    private final EnvironmentRepository environmentRepositoryMock = Mockito.mock(EnvironmentRepository.class);
    private final EnvStyleMap envStyleMap = new EnvStyleMap(environmentRepositoryMock);

    @Test
    void pattern(){
        Matcher matcher = EnvStyleMap.pattern.matcher("{{aaa}} {{ }} { asasas {} {{}}{{a}} t {{c}}{{d}}");

        List<String> found = new ArrayList<>();

        while (matcher.find()) {
            found.add(matcher.group());
            log.info("found: {}:{} - {}", matcher.start(), matcher.end(), matcher.group());
        }

        assertThat(found)
                .containsOnly(
                        "{{aaa}}",
                        "{{ }}",
                        "{{}}",
                        "{{a}}",
                        "{{c}}",
                        "{{d}}"
                );
    }

    @Nested
    class ContainsKey {
        @BeforeEach
        void setup(){
            Mockito.reset(environmentRepositoryMock);
        }

        @Test
        void whenEmptyEnvironments() throws IOException {
            Mockito.when(environmentRepositoryMock.getWorkspaceSessionEnvironment())
                    .thenReturn(Optional.of(new EnvironmentDto()));

            assertThat(envStyleMap.containsKey("{{aaa}}")).isFalse();
        }

        @Test
        void whenHaveEnvironments() throws IOException {
            var environment = new EnvironmentDto();
            environment.setEnvs(List.of(new EnvironmentItemDto("foo", "bar")));

            Mockito.when(environmentRepositoryMock.getWorkspaceSessionEnvironment())
                    .thenReturn(Optional.of(environment));

            assertThat(envStyleMap.containsKey("{{foo}}")).isTrue();
            assertThat(envStyleMap.containsKey("{{bar}}")).isFalse();
            assertThat(envStyleMap.getInjected()).containsKey("{{foo}}");
        }

        @Test
        void whenContainsSysEnv() throws IOException {
            assertThat(envStyleMap.containsKey("{{" + SYSENV_COLLECTION_FILE_DIR_KEY + "}}")).isTrue();
        }
    }
}
