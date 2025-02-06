package com.github.clagomess.tomato.enums;

import com.github.clagomess.tomato.io.http.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static com.github.clagomess.tomato.io.http.MediaType.*;
import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.*;

@Getter
@AllArgsConstructor
public enum RawBodyTypeEnum {
    TEXT(SYNTAX_STYLE_NONE, TEXT_PLAIN, "Text"),
    JSON(SYNTAX_STYLE_JSON, APPLICATION_JSON, "JSON"),
    XML(SYNTAX_STYLE_XML, TEXT_XML, "XML"),
    HTML(SYNTAX_STYLE_HTML, TEXT_HTML, "HTML");

    private final String syntaxStyle;
    private final MediaType contentType;
    private final String label;

    public static RawBodyTypeEnum valueOfContentType(String contentType) {
        var mediaType = new MediaType(contentType);
        return Arrays.stream(RawBodyTypeEnum.values())
                .filter(item -> item.getContentType().isCompatible(mediaType))
                .findFirst()
                .orElse(RawBodyTypeEnum.TEXT);
    }

    @Override
    public String toString() {
        return label;
    }
}
