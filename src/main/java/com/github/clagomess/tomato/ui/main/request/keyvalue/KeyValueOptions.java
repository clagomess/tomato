package com.github.clagomess.tomato.ui.main.request.keyvalue;

import com.github.clagomess.tomato.ui.component.CharsetComboBox;
import com.github.clagomess.tomato.ui.component.envtextfield.EnvTextfieldOptions;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KeyValueOptions {
    @Builder.Default
    private boolean enableTypeColumn = false;

    @Builder.Default
    private CharsetComboBox charsetComboBox = null;

    @Builder.Default
    private RowComponent.OnChange onChange = item -> {};

    @Builder.Default
    private EnvTextfieldOptions envTextfieldOptions = EnvTextfieldOptions.builder().build();
}
