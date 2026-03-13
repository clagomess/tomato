package io.github.clagomess.tomato.ui.component.envtextfield;

import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.enums.RawBodyTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class EnvTextfieldOptions {
    @Builder.Default
    private PathVar pathVar = null;

    @Builder.Default
    private boolean valueEditorShowContentTypeEdit = false;

    @Builder.Default
    private RawBodyTypeEnum valueEditorSelectedRawBodyType = RawBodyTypeEnum.TEXT;

    @Builder.Default
    private ValueEditorFrame.OnDisposeFI valueEditorOnDispose = (rawBodyType, text) -> {};


    public record PathVar(
            TabKey tabKey,
            List<KeyValueItemDto> urlPathParam
    ){}
}
