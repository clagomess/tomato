package com.github.clagomess.tomato.io.snippet;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum;
import com.github.clagomess.tomato.dto.data.request.BinaryBodyDto;
import com.github.clagomess.tomato.dto.data.request.RawBodyDto;
import com.github.clagomess.tomato.io.http.RequestBuilder;
import com.github.clagomess.tomato.io.http.UrlBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import static com.github.clagomess.tomato.io.http.MediaType.HTTP_CONTENT_TYPE;

@RequiredArgsConstructor
public class CurlSnippet {
    private final Type type;
    private RequestBuilder requestBuilder;

    public StringBuilder build(RequestDto request) throws IOException {
        requestBuilder = new RequestBuilder();

        StringBuilder code = new StringBuilder();
        code.append("curl -X ");
        code.append(request.getMethod());
        code.append(" ");
        code.append(type.getStringQuote());
        code.append(new UrlBuilder(request).buildUri().toString());
        code.append(type.getStringQuote());

        buildHeaders(request.getHeaders(), code);
        buildCookies(request.getCookies(), code);

        switch (request.getBody().getType()){
            case RAW -> buildBodyRaw(request.getBody().getRaw(), code);
            case BINARY -> buildBodyBinary(request.getBody().getBinary(), code);
            case URL_ENCODED_FORM -> buildUrlEncodedForm(request.getBody().getUrlEncodedForm(), code);
            case MULTIPART_FORM -> buildMultipartFormData(request.getBody().getMultiPartForm(), code);
        }

        return code;
    }

    protected void writeHeader(
            String key,
            String value,
            StringBuilder code
    ) {
        code.append(type.getNewLine());
        code.append("-H ");
        code.append(type.getStringQuote());
        code.append(key);
        code.append(": ");
        code.append(value);
        code.append(type.getStringQuote());
    }


    protected void buildHeaders(
            List<KeyValueItemDto> headers,
            StringBuilder code
    ) {
        requestBuilder.buildHeaders(headers).forEach(item ->
            writeHeader(item.getKey(), item.getValue(), code)
        );
    }

    protected void buildCookies(
            List<KeyValueItemDto> cookies,
            StringBuilder code
    ) {
        requestBuilder.buildCookies(cookies).forEach(item ->
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
        code.append(type.getStringQuote());
        code.append("@");
        code.append(binaryBody.getFile());
        code.append(type.getStringQuote());
    }

    protected void buildBodyRaw(
            RawBodyDto rawBody,
            StringBuilder code
    ){
        writeHeader(
                HTTP_CONTENT_TYPE,
                rawBody.getType().getContentType().toString(),
                code
        ); //@TODO: check

        code.append(type.getNewLine());
        code.append("--data-raw ");
        code.append(type.getStringQuote());
        code.append(rawBody.getRaw());
        code.append(type.getStringQuote());
    }

    protected void buildUrlEncodedForm(
            List<ContentTypeKeyValueItemDto> form,
            StringBuilder code
    ) {
        requestBuilder.buildUrlEncodedForm(form).forEach(item -> {
            code.append(type.getNewLine());
            code.append("-d ");
            code.append(type.getStringQuote());
            code.append(item.getKey());
            code.append("=");
            code.append(item.getValue());
            code.append(type.getStringQuote());
        });
    }

    protected void buildMultipartFormData(
            List<FileKeyValueItemDto> form,
            StringBuilder code
    ) {
        requestBuilder.buildMultipartFormData(form).forEach(item -> {
            code.append(type.getNewLine());
            code.append("-F ");
            code.append(type.getStringQuote());
            code.append(item.getKey());
            code.append("=");

            if(item.getType() == KeyValueTypeEnum.FILE){
                code.append("@");
            }

            code.append(item.getValue());
            code.append(type.getStringQuote());
        });
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        BASH("cURL - BASH", "'", " \\\n"),
        POWERSHELL("cURL - PowerShell", "'", " `\n"),
        CMD("cURL - CMD", "\"", " ^\n");

        private final String name;
        private final String stringQuote;
        private final String newLine;
    }
}
