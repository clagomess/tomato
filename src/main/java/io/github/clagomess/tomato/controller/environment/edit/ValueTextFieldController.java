package io.github.clagomess.tomato.controller.environment.edit;

import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.io.keystore.EnvironmentKeystore;
import io.github.clagomess.tomato.ui.environment.edit.ValueTextFieldInterface;

import java.io.IOException;

public class ValueTextFieldController {
    private final EnvironmentKeystore environmentKeystore;
    private final ValueTextFieldInterface ui;

    private final EnvironmentItemDto item;

    public ValueTextFieldController(
            ValueTextFieldInterface ui,
            EnvironmentKeystore environmentKeystore,
            EnvironmentItemDto item
    ) {
        this.environmentKeystore = environmentKeystore;
        this.ui = ui;
        this.item = item;
    }

    public String unlockSecret() throws IOException {
        String text;

        if (item.getSecretId() != null && item.getValue() == null) {
            text = environmentKeystore.loadSecret(item.getSecretId())
                    .orElse(null);

            ui.setText(text);
        } else {
            text = item.getValue();
        }

        ui.revealSecret();

        return text;
    }
}
