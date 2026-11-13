package io.github.clagomess.tomato.dto.data.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.request.body.BinaryBodyDto;
import io.github.clagomess.tomato.dto.data.request.body.RawBodyDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class BodyDto {
    private Type type = Type.NO_BODY;
    private Charset charset;
    private RawBodyDto raw;
    private BinaryBodyDto binary;
    private List<ContentTypeKeyValueItemDto> urlEncodedForm  = new ArrayList<>();
    private List<FileKeyValueItemDto> multiPartForm = new ArrayList<>();

    public Charset getCharset() {
        if(charset == null) charset = UTF_8;
        return charset;
    }

    public Type getType() {
        if(type == null) type = Type.NO_BODY;
        return type;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public RawBodyDto getRaw() {
        if(type != Type.RAW) return null;
        if(raw == null) raw = new RawBodyDto();
        return raw;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BinaryBodyDto getBinary() {
        if(type != Type.BINARY) return null;
        if(binary == null) binary = new BinaryBodyDto();
        return binary;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<ContentTypeKeyValueItemDto> getUrlEncodedForm() {
        if(type != Type.URL_ENCODED_FORM) return null;
        if(urlEncodedForm == null) urlEncodedForm = new ArrayList<>();
        return urlEncodedForm;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<FileKeyValueItemDto> getMultiPartForm() {
        if(type != Type.MULTIPART_FORM) return null;
        if(multiPartForm == null) multiPartForm = new ArrayList<>();
        return multiPartForm;
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        NO_BODY("No Body"),
        MULTIPART_FORM("Multipart Form"),
        URL_ENCODED_FORM("URL Encoded Form"),
        RAW("Raw"),
        BINARY("Binary");

        private final String description;

        @Override
        public String toString() {
            return description;
        }
    }
}
