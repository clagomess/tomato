package io.github.clagomess.tomato.ui.main.request.keyvalue;

import io.github.clagomess.tomato.ui.component.CharsetComboBox;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KeyValueOptions {
    @Builder.Default
    private CharsetComboBox charsetComboBox = null;

    @Builder.Default
    private RowComponent.OnChange onChange = item -> {};
}
