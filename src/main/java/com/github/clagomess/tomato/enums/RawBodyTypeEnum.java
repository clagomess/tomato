package com.github.clagomess.tomato.enums;

import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static jakarta.ws.rs.core.MediaType.*;
import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.*;

@Getter
@AllArgsConstructor
public enum RawBodyTypeEnum {
    TEXT(SYNTAX_STYLE_NONE, TEXT_PLAIN_TYPE, "Text"),
    JSON(SYNTAX_STYLE_JSON, APPLICATION_JSON_TYPE, "JSON"),
    XML(SYNTAX_STYLE_XML, APPLICATION_XML_TYPE, "XML"),
    HTML(SYNTAX_STYLE_HTML, TEXT_HTML_TYPE, "HTML");

    private final String syntaxStyle;
    private final MediaType contentType;
    private final String label;

    @Override
    public String toString() {
        return label;
    }
}
