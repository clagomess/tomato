package io.github.clagomess.tomato.io.snippet;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum;
import io.github.clagomess.tomato.dto.data.request.BinaryBodyDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.http.RequestBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.function.UnaryOperator;

import static io.github.clagomess.tomato.io.http.MediaType.HTTP_CONTENT_TYPE;
import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.*;

public class CurlSnippet extends RequestBuilder implements CodeSnippet {
    private final Type type;

    public CurlSnippet(RequestHeadDto requestHead, Type type) {
        super(requestHead);
        this.type = type;
    }

    public String getName(){
        return type.getName();
    }

    public String getSyntaxStyle(){
        return type.getSyntaxStyle();
    }

    public String build(RequestDto request) throws IOException {
        StringBuilder code = new StringBuilder();
        code.append("curl -X ");
        code.append(request.getMethod());
        code.append(" ");
        code.append(quote(buildUri(request).toString()));

        buildHeaders(request.getHeaders(), code);
        buildCookies(request.getCookies(), code);

        switch (request.getBody().getType()){
            case RAW -> buildBodyRaw(request, code);
            case BINARY -> buildBodyBinary(request.getBody().getBinary(), code);
            case URL_ENCODED_FORM -> buildUrlEncodedForm(request.getBody().getUrlEncodedForm(), code);
            case MULTIPART_FORM -> buildMultipartFormData(request.getBody().getMultiPartForm(), code);
        }

        return code.toString();
    }

    protected void writeHeader(
            String key,
            String value,
            StringBuilder code
    ) {
        code.append(type.getNewLine());
        code.append("-H ");
        code.append(quote(key + ": " + value));
    }

    protected String quote(String value) {
        return type.getStringQuote() +
                type.getEscape().apply(value) +
                type.getStringQuote();
    }

    protected void buildHeaders(
            List<KeyValueItemDto> headers,
            StringBuilder code
    ) {
        buildHeaders(headers).forEach(item ->
            writeHeader(item.getKey(), item.getValue(), code)
        );
    }

    protected void buildCookies(
            List<KeyValueItemDto> cookies,
            StringBuilder code
    ) {
        buildCookies(cookies).forEach(item ->
            writeHeader(
                    "Cookie",
                    item.getKey() + "=" + item.getValue(),
                    code
            )
        );
    }

    protected void buildBodyBinary(
            BinaryBodyDto binaryBody,
            StringBuilder code
    ){
        writeHeader(
                HTTP_CONTENT_TYPE,
                binaryBody.getContentType(),
                code
        );

        code.append(type.getNewLine());
        code.append("--data-binary ");
        code.append(quote("@" + buildBodyBinary(binaryBody.getFile())));
    }

    protected void buildBodyRaw(
            RequestDto request,
            StringBuilder code
    ){
        var raw = request.getBody().getRaw();

        if(request.getHeaders().stream()
                .noneMatch(item ->
                        HTTP_CONTENT_TYPE.equalsIgnoreCase(item.getKey())
                )
        ) {
            writeHeader(
                    HTTP_CONTENT_TYPE,
                    raw.getType().getContentType().toString(),
                    code
            );
        }

        code.append(type.getNewLine());
        code.append("--data-raw ");
        code.append(quote(raw.getRaw()));
    }

    protected void buildUrlEncodedForm(
            List<ContentTypeKeyValueItemDto> form,
            StringBuilder code
    ) {
        buildUrlEncodedForm(form).forEach(item -> {
            code.append(type.getNewLine());
            code.append("-d ");
            code.append(quote(item.getKey() + "=" + item.getValue()));
        });
    }

    protected void buildMultipartFormData(
            List<FileKeyValueItemDto> form,
            StringBuilder code
    ) {
        buildMultipartFormData(form).forEach(item -> {
            code.append(type.getNewLine());
            code.append("-F ");
            code.append(quote(
                    item.getKey() + "=" +
                    (item.getType() == KeyValueTypeEnum.FILE ? "@" : "") +
                    item.getValue()
            ));
        });
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        BASH(
                "cURL - BASH",
                SYNTAX_STYLE_UNIX_SHELL,
                "'",
                " \\\n",
                value -> value.replace("'", "'\\''")
        ),
        POWERSHELL(
                "cURL - PowerShell",
                SYNTAX_STYLE_NONE,
                "'",
                " `\n",
                value -> value.replace("'", "''")
        ),
        CMD(
                "cURL - CMD",
                SYNTAX_STYLE_WINDOWS_BATCH,
                "\"",
                " ^\n",
                value -> value
                        .replaceAll("(\\\\*)\"", "$1$1\\\\\"")
                        .replaceAll("(\\\\+)$", "$1$1")
        );

        private final String name;
        private final String syntaxStyle;
        private final String stringQuote;
        private final String newLine;
        private final UnaryOperator<String> escape;
    }
}
