package io.github.clagomess.tomato.ui.component.envtextfield;

import io.github.clagomess.tomato.enums.RawBodyTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EnvTextfieldOptions {
    @Builder.Default
    private boolean valueEditorShowContentTypeEdit = false;

    @Builder.Default
    private RawBodyTypeEnum valueEditorSelectedRawBodyType = RawBodyTypeEnum.TEXT;

    @Builder.Default
    private ValueEditorFrame.OnDisposeFI valueEditorOnDispose = (rawBodyType, text) -> {};
}
