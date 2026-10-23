package io.github.clagomess.tomato.io.snippet;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum;
import io.github.clagomess.tomato.dto.data.request.BinaryBodyDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.http.RequestBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.clagomess.tomato.enums.HttpMethodEnum.GET;
import static io.github.clagomess.tomato.io.http.MediaType.HTTP_CONTENT_TYPE;
import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_PHP;

public class PHPcURLSnippet extends RequestBuilder implements CodeSnippet {
    public PHPcURLSnippet(RequestHeadDto requestHead) {
        super(requestHead);
    }

    public String getName(){
        return "PHP - cURL";
    }

    public String getSyntaxStyle(){
        return SYNTAX_STYLE_PHP;
    }

    protected String quote(String value) {
        return value
                .replaceAll("\\\\$", "\\\\\\\\")
                .replace("'", "\\'");
    }

    protected void buildCookies(
            List<KeyValueItemDto> cookies,
            StringBuilder code
    ) {
        if(cookies == null || cookies.isEmpty()) return;

        code.append("curl_setopt($ch, CURLOPT_COOKIE, '");

        AtomicBoolean first = new AtomicBoolean(true);
        buildCookies(cookies).forEach(item -> {
            if (!first.compareAndSet(true, false)){
                code.append("; ");
            }

            code.append(quote(item.getKey() + "=" + item.getValue()));
        });

        code.append("');\n");
    }

    private final List<KeyValueItemDto> headersBuffer = new ArrayList<>();
    private final AtomicBoolean headersFlushed = new AtomicBoolean(false);
    protected void flushHeaders(StringBuilder code){
        if(!headersFlushed.compareAndSet(false, true)) return;
        if(headersBuffer.isEmpty()) return;

        code.append("curl_setopt($ch, CURLOPT_HTTPHEADER, array(\n");

        AtomicBoolean first = new AtomicBoolean(true);
        headersBuffer.forEach(item -> {
            if (!first.compareAndSet(true, false)){
                code.append(",\n");
            }

            code.append("    '");
            code.append(quote(item.getKey() + ": " + item.getValue()));
            code.append("'");
        });

        code.append("\n));\n");
    }

    protected void buildArray(
            Map<String, String> assoc,
            StringBuilder code
    ){
        code.append("array(\n");

        AtomicBoolean first = new AtomicBoolean(true);
        for(var entry : assoc.entrySet()){
            if (!first.compareAndSet(true, false)){
                code.append(",\n");
            }

            code.append("    '");
            code.append(entry.getKey());
            code.append("' => '");
            code.append(entry.getValue());
            code.append("'");
        }

        code.append("\n)");
    }

    protected void buildUrlEncodedForm(
            List<ContentTypeKeyValueItemDto> form,
            StringBuilder code
    ) {
        if(form == null || form.isEmpty()) return;
        code.append("curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(");

        Map<String, String> assoc = new LinkedHashMap<>();
        buildUrlEncodedForm(form).forEach(item ->
                assoc.put(quote(item.getKey()), quote(item.getValue()))
        );

        buildArray(assoc, code);

        code.append("));\n");
    }

    protected void buildMultipartFormData(
            List<FileKeyValueItemDto> form,
            StringBuilder code
    ) {
        if(form == null || form.isEmpty()) return;
        code.append("curl_setopt($ch, CURLOPT_POSTFIELDS, ");

        Map<String, String> assoc = new LinkedHashMap<>();
        buildMultipartFormData(form).forEach(item -> {
            var prefix = item.getType() == KeyValueTypeEnum.FILE ? "@" : "";
            var suffix = item.getType() == KeyValueTypeEnum.FILE && StringUtils.isNotBlank(item.getValueContentType())
                    ? ";type=" + item.getValueContentType() : "";
            assoc.put(quote(item.getKey()) , prefix + quote(item.getValue()) + suffix);
        });

        buildArray(assoc, code);

        code.append(");\n");
    }

    protected void buildBodyRaw(
            RequestDto request,
            StringBuilder code
    ) {
        var raw = request.getBody().getRaw();

        if(headersBuffer.stream().noneMatch(item ->
                        HTTP_CONTENT_TYPE.equalsIgnoreCase(item.getKey()))) {
            headersBuffer.add(new KeyValueItemDto(
                    HTTP_CONTENT_TYPE,
                    raw.getType().getContentType().toString()
            ));
        }

        flushHeaders(code);
        code.append("curl_setopt($ch, CURLOPT_POSTFIELDS, '");
        code.append(quote(raw.getRaw()));
        code.append("');\n");
    }

    protected void buildBodyBinary(
            BinaryBodyDto binaryBody,
            StringBuilder code
    ) {
        headersBuffer.add(new KeyValueItemDto(
                HTTP_CONTENT_TYPE,
                binaryBody.getContentType()
        ));

        var file = buildBodyBinary(binaryBody.getFile());

        flushHeaders(code);
        code.append("curl_setopt($ch, CURLOPT_POSTFIELDS, file_get_contents('");
        code.append(file != null ? quote(file.toString()) : "");
        code.append("'));\n");
    }

    public String build(RequestDto request) throws IOException {
        StringBuilder code = new StringBuilder();
        code.append("<?php\n");
        code.append("$ch = curl_init();\n");
        code.append("curl_setopt($ch, CURLOPT_URL, '");
        code.append(quote(buildUri(request).toString()));
        code.append("');\n");

        if(request.getMethod() != GET){
            code.append("curl_setopt($ch, CURLOPT_CUSTOMREQUEST, '");
            code.append(request.getMethod().name());
            code.append("');\n");
        }

        code.append("curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);\n");

        buildCookies(request.getCookies(), code);
        headersBuffer.addAll(buildHeaders(request.getHeaders()).toList());

        switch (request.getBody().getType()){
            case RAW -> buildBodyRaw(request, code);
            case BINARY -> buildBodyBinary(request.getBody().getBinary(), code);
            case URL_ENCODED_FORM -> buildUrlEncodedForm(request.getBody().getUrlEncodedForm(), code);
            case MULTIPART_FORM -> buildMultipartFormData(request.getBody().getMultiPartForm(), code);
        }

        flushHeaders(code);

        code.append("$response = curl_exec($ch);\n");
        code.append("curl_close($ch);\n");
        code.append("echo $response;\n");

        return code.toString();
    }
}
