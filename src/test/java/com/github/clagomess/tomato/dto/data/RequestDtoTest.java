package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.request.BinaryBodyDto;
import com.github.clagomess.tomato.dto.data.request.RawBodyDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.io.converter.JsonSchemaBuilder;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.JsonSchema;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.REQUEST;
import static com.github.clagomess.tomato.io.http.MediaType.TEXT_PLAIN_TYPE;

@Slf4j
public class RequestDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getTomatoJsonSchema(REQUEST);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    public void toJson_Defaults() throws JsonProcessingException {
        var dto = new RequestDto();
        dto.setUrl("http://localhost:8080/tomato");

        var json = mapper.writeValueAsString(dto);
        log.info(json);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void toJson_UrlParam() throws JsonProcessingException {
        var dto = new RequestDto();
        dto.setUrl("http://localhost:8080/tomato");
        dto.getUrlParam().getPath().add(new KeyValueItemDto("key", "value"));
        dto.getUrlParam().getQuery().add(new ContentTypeKeyValueItemDto("key", "value"));

        var json = mapper.writeValueAsString(dto);
        log.info(json);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void toJson_Header() throws JsonProcessingException {
        var dto = new RequestDto();
        dto.setUrl("http://localhost:8080/tomato");
        dto.getHeaders().add(new KeyValueItemDto("key", "value"));
        dto.getHeaders().add(new KeyValueItemDto("key", "value"));

        var json = mapper.writeValueAsString(dto);
        log.info(json);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void toJson_Cookie() throws JsonProcessingException {
        var dto = new RequestDto();
        dto.setUrl("http://localhost:8080/tomato");
        dto.getCookies().add(new KeyValueItemDto("key", "value"));

        var json = mapper.writeValueAsString(dto);
        log.info(json);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void toJson_Body_Raw() throws JsonProcessingException {
        var dto = new RequestDto();
        dto.setUrl("http://localhost:8080/tomato");
        dto.getBody().setType(BodyTypeEnum.RAW);
        dto.getBody().setRaw(new RawBodyDto(RawBodyTypeEnum.TEXT, "aaa"));

        var json = mapper.writeValueAsString(dto);
        log.info(json);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void toJson_Body_Binary() throws JsonProcessingException {
        var dto = new RequestDto();
        dto.setUrl("http://localhost:8080/tomato");
        dto.getBody().setType(BodyTypeEnum.BINARY);
        dto.getBody().setBinary(new BinaryBodyDto(
                TEXT_PLAIN_TYPE,
                "file"
        ));

        var json = mapper.writeValueAsString(dto);
        log.info(json);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void toJson_Body_urlEncodedForm() throws JsonProcessingException {
        var dto = new RequestDto();
        dto.setUrl("http://localhost:8080/tomato");
        dto.getBody().setType(BodyTypeEnum.URL_ENCODED_FORM);
        dto.getBody().getUrlEncodedForm().add(
                new ContentTypeKeyValueItemDto("key", "value")
        );

        var json = mapper.writeValueAsString(dto);
        log.info(json);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void toJson_Body_multiPartForm() throws JsonProcessingException {
        var dto = new RequestDto();
        dto.setUrl("http://localhost:8080/tomato");
        dto.getBody().setType(BodyTypeEnum.MULTIPART_FORM);
        dto.getBody().getMultiPartForm().add(
                new FileKeyValueItemDto("key", "value")
        );

        var json = mapper.writeValueAsString(dto);
        log.info(json);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void equalsHashCode(){
        var dtoA = new RequestDto();
        dtoA.setId("aaa");

        var dtoB = new RequestDto();
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }
}
