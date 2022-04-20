package com.github.clagomess.tomato.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

@Getter
@AllArgsConstructor
public enum ContentTypeEnum {
    TEXT(SyntaxConstants.SYNTAX_STYLE_NONE, "Text"),
    JSON(SyntaxConstants.SYNTAX_STYLE_JSON, "JSON"),
    XML(SyntaxConstants.SYNTAX_STYLE_XML, "XML"),
    HTML(SyntaxConstants.SYNTAX_STYLE_HTML, "HTML"),
    JAVASCRIPT(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT, "Javascript");

    private final String syntaxStyle;
    private final String label;

    @Override
    public String toString() {
        return label;
    }
}
