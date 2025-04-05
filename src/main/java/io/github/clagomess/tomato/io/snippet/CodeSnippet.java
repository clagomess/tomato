package io.github.clagomess.tomato.io.snippet;

import io.github.clagomess.tomato.dto.data.RequestDto;

public interface CodeSnippet {
    String getName();
    String getSyntaxStyle();
    String build(RequestDto request) throws Exception;
}
