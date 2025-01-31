package com.github.clagomess.tomato.io.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class MediaType {
    private final String type;
    private final String subtype;
    private final Map<String, String> parameters = new TreeMap<>(String::compareToIgnoreCase);
    private final Charset charset;

    public MediaType(String type, String subtype) {
        this.type = type;
        this.subtype = subtype;
        this.charset = null;
    }

    public MediaType(String type, String subtype, String charset) {
        this(type, subtype, Charset.forName(charset));
        parameters.put("charset", charset.toLowerCase().trim());
    }

    public MediaType(String contentType){
        int typeSeparator = contentType.indexOf('/');
        type = contentType.substring(0, typeSeparator);

        if(contentType.contains(";")){
            int paramSeparator = contentType.indexOf(';');
            subtype = contentType.substring(typeSeparator + 1, paramSeparator);
            String[] params = contentType.substring(paramSeparator + 1).split(";");
            Stream.of(params)
                    .filter(StringUtils::isNotBlank)
                    .filter(item -> item.contains("="))
                    .forEach(item -> {
                        String[] param = item.split("=");
                        parameters.put(
                                param[0].toLowerCase().trim(),
                                param[1].toLowerCase().trim()
                        );
                    });

            if(parameters.containsKey("charset")){
                charset = Charset.forName(parameters.get("charset"));
            }else{
                charset = null;
            }
        }else{
            subtype = contentType.substring(typeSeparator + 1);
            charset = null;
        }
    }

    public boolean isCompatible(MediaType other){
        return Objects.equals(type, other.getType()) &&
                Objects.equals(subtype, other.getSubtype());
    }

    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append(type).append("/").append(subtype);

        if(!parameters.isEmpty()) {
            result.append(";");
            result.append(parameters.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining(";"))
            );
        }

        return result.toString();
    }

    public boolean isString(){
        return Stream.of(
                "utf-8",
                "utf8",
                "text",
                "json",
                "css",
                "html",
                "xhtml",
                "form",
                "javascript",
                "ecmascript",
                "xml",
                "wsdl",
                "csv",
                "urlencoded"
        ).anyMatch(s -> toString().contains(s));
    }

    public final static MediaType WILDCARD = new MediaType("*", "*");
    public final static MediaType TEXT_PLAIN = new MediaType("text", "plain");
    public final static MediaType APPLICATION_JSON = new MediaType("application", "json");
    public final static MediaType TEXT_HTML = new MediaType("text", "html");
    public final static MediaType TEXT_XML = new MediaType("text", "xml");

    public final static String TEXT_PLAIN_TYPE = "text/plain";
    public final static String APPLICATION_OCTET_STREAM_TYPE = "application/octet-stream";
}
