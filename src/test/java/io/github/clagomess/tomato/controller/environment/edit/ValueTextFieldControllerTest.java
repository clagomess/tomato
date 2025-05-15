package io.github.clagomess.tomato.controller.environment.edit;

import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.io.keystore.EnvironmentKeystore;
import io.github.clagomess.tomato.ui.environment.edit.ValueTextFieldInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValueTextFieldControllerTest {
    @Nested
    class unlockSecret {
        final EnvironmentKeystore environmentKeystore = Mockito.mock(EnvironmentKeystore.class);
        final ValueTextFieldInterface abstractUi = Mockito.mock(ValueTextFieldInterface.class);

        final AtomicBoolean triggeredUnlockUI = new AtomicBoolean(false);

        @BeforeEach
        void setup(){
            triggeredUnlockUI.set(false);
            Mockito.reset(abstractUi);

            Mockito.doAnswer(answer -> {
                triggeredUnlockUI.set(true);
                return null;
            }).when(abstractUi).revealSecret();
        }

        @Test
        void whenEmptySecret_keepValue() throws IOException {
            Mockito.doThrow(new RuntimeException("Not: ui.setText"))
                    .when(abstractUi)
                    .setText(Mockito.anyString());

            var item = new EnvironmentItemDto(SECRET, null, "foo", "bar");

            var controller = new ValueTextFieldController(
                    abstractUi,
                    environmentKeystore,
                    item
            );

            assertEquals("bar", controller.unlockSecret());
            assertTrue(triggeredUnlockUI.get());
        }

        @Test
        void whenNotEmptySecretAndNotEmptyValue_keepValue() throws IOException {
            Mockito.doThrow(new RuntimeException("Not: loadSecret"))
                    .when(environmentKeystore)
                    .loadSecret(Mockito.any(UUID.class));

            var item = new EnvironmentItemDto(SECRET, UUID.randomUUID(), "foo", "bar");

            var controller = new ValueTextFieldController(
                    abstractUi,
                    environmentKeystore,
                    item
            );

            assertEquals("bar", controller.unlockSecret());
            assertTrue(triggeredUnlockUI.get());
        }

        @Test
        void whenLoadSecret_returnValueAndTriggerUi() throws IOException {
            Mockito.doReturn(Optional.of("mySecret"))
                    .when(environmentKeystore)
                    .loadSecret(Mockito.any(UUID.class));

            var item = new EnvironmentItemDto(SECRET, UUID.randomUUID(), "foo", null);

            var controller = new ValueTextFieldController(
                    abstractUi,
                    environmentKeystore,
                    item
            );

            assertEquals("mySecret", controller.unlockSecret());
            assertTrue(triggeredUnlockUI.get());
        }
    }
}
