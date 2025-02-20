package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum;
import com.github.clagomess.tomato.dto.data.request.BinaryBodyDto;
import com.github.clagomess.tomato.dto.data.request.BodyDto;
import com.github.clagomess.tomato.dto.data.request.RawBodyDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.io.repository.RepositoryStubs;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostmanCollectionDumpMapperTest extends RepositoryStubs {
    private final PostmanCollectionDumpMapper dumpMapper = PostmanCollectionDumpMapper.INSTANCE;

    @Test
    public void toItem_fromCollectionTree(){
        var collectionTree = new CollectionTreeDto();
        collectionTree.setName("foo");

        var result = dumpMapper.toItem(collectionTree);
        assertNull(result.getRequest());
        assertNull(result.getItem());
        assertEquals("foo", result.getName());
    }

    @Test
    public void toItem_Request(){
        var request = new RequestDto();
        request.setName("foo");
        request.setUrl("https://foo");
        request.setMethod(HttpMethodEnum.PUT);

        var result = dumpMapper.toItem(request);
        assertNotNull(result.getRequest());
        assertNull(result.getItem());
        assertEquals("foo", result.getName());
        assertEquals("https://foo", result.getRequest().getUrl().getRaw());
        assertEquals("PUT", result.getRequest().getMethod());
    }

    @Test
    public void toBody_Request_noBody(){
        var request = new RequestDto();

        var result = dumpMapper.toItem(request);
        assertNotNull(result.getRequest());
        assertNull(result.getItem());
        assertNull(result.getRequest().getBody());
    }

    @Test
    public void toItem_Request_urlParam_Query(){
        var request = new RequestDto();
        request.getUrlParam().setQuery(List.of(
                new ContentTypeKeyValueItemDto("foo1", "bar"),
                new ContentTypeKeyValueItemDto("foo2", "bar", "text/plain",false)
        ));

        var result = dumpMapper.toItem(request);
        assertNotNull(result.getRequest());
        assertNull(result.getItem());

        var query = result.getRequest().getUrl().getQuery();
        Assertions.assertThat(query).hasSize(2);

        for(int i = 0; i < 2; i++){
            assertEquals(
                    request.getUrlParam().getQuery().get(i).getKey(),
                    query.get(i).getKey()
            );

            assertEquals(
                    request.getUrlParam().getQuery().get(i).getValue(),
                    query.get(i).getValue()
            );

            assertEquals(
                    !request.getUrlParam().getQuery().get(i).isSelected(),
                    query.get(i).getDisabled()
            );
        }
    }

    @Test
    public void toItem_Request_urlParam_Variables(){
        var request = new RequestDto();
        request.getUrlParam().setPath(List.of(
                new KeyValueItemDto("foo1", "bar"),
                new KeyValueItemDto("foo2", "bar", false)
        ));

        var result = dumpMapper.toItem(request);
        assertNotNull(result.getRequest());
        assertNull(result.getItem());

        var variable = result.getRequest().getUrl().getVariable();
        Assertions.assertThat(variable).hasSize(2);

        for(int i = 0; i < 2; i++){
            assertEquals(
                    request.getUrlParam().getPath().get(i).getKey(),
                    variable.get(i).getKey()
            );

            assertEquals(
                    request.getUrlParam().getPath().get(i).getValue(),
                    variable.get(i).getValue()
            );

            assertEquals(
                    !request.getUrlParam().getPath().get(i).isSelected(),
                    variable.get(i).getDisabled()
            );
        }
    }

    @Test
    public void toItem_Request_Headers(){
        var request = new RequestDto();
        request.setHeaders(List.of(
                new KeyValueItemDto("foo1", "bar"),
                new KeyValueItemDto("foo2", "bar", false)
        ));

        var result = dumpMapper.toItem(request);
        assertNotNull(result.getRequest());
        assertNull(result.getItem());

        var headers = result.getRequest().getHeader();
        Assertions.assertThat(headers).hasSize(2);

        for(int i = 0; i < 2; i++){
            assertEquals(
                    request.getHeaders().get(i).getKey(),
                    headers.get(i).getKey()
            );

            assertEquals(
                    request.getHeaders().get(i).getValue(),
                    headers.get(i).getValue()
            );

            assertEquals(
                    !request.getHeaders().get(i).isSelected(),
                    headers.get(i).getDisabled()
            );
        }
    }

    @ParameterizedTest
    @CsvSource({
        "raw,RAW",
        "file,BINARY",
        "formdata,MULTIPART_FORM",
        "urlencoded,URL_ENCODED_FORM",
    })
    public void toBody_type(
            String expected,
            BodyTypeEnum bodyType
    ){
        var body =  new BodyDto();
        body.setType(bodyType);

        var result = dumpMapper.toBody(body);
        assertEquals(expected, result.getMode());
    }

    @Test
    public void toBody_raw(){
        var body =  new BodyDto();
        body.setType(BodyTypeEnum.RAW);
        body.setRaw(new RawBodyDto());
        body.getRaw().setRaw("foo");
        body.getRaw().setType(RawBodyTypeEnum.JSON);

        var result = dumpMapper.toBody(body);
        assertEquals("raw", result.getMode());
        assertEquals("foo", result.getRaw());
        assertEquals("JSON", result.getOptions().getRaw().getLanguage());
    }

    @Test
    public void toBody_binary(){
        var body =  new BodyDto();
        body.setType(BodyTypeEnum.BINARY);
        body.setBinary(new BinaryBodyDto());
        body.getBinary().setFile("foo");

        var result = dumpMapper.toBody(body);
        assertEquals("file", result.getMode());
    }

    @Test
    public void toBody_urlEncodedForm(){
        var body =  new BodyDto();
        body.setType(BodyTypeEnum.URL_ENCODED_FORM);
        body.setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("foo1", "bar"),
                new ContentTypeKeyValueItemDto("foo2", "bar", "text/plain",false)
        ));

        var result = dumpMapper.toBody(body);
        assertEquals("urlencoded", result.getMode());

        var form = result.getUrlencoded();
        Assertions.assertThat(form).hasSize(2);

        for(int i = 0; i < 2; i++){
            assertEquals(
                    body.getUrlEncodedForm().get(i).getKey(),
                    form.get(i).getKey()
            );

            assertEquals(
                    body.getUrlEncodedForm().get(i).getValue(),
                    form.get(i).getValue()
            );

            assertEquals(
                    !body.getUrlEncodedForm().get(i).isSelected(),
                    form.get(i).getDisabled()
            );
        }
    }

    @Test
    public void toBody_multiPartForm(){
        var body =  new BodyDto();
        body.setType(BodyTypeEnum.MULTIPART_FORM);
        body.setMultiPartForm(List.of(
                new FileKeyValueItemDto("foo1", "bar"),
                new FileKeyValueItemDto(KeyValueTypeEnum.FILE, "foo2", "bar", "text/plain",false)
        ));

        var result = dumpMapper.toBody(body);
        assertEquals("formdata", result.getMode());

        var form = result.getFormdata();
        Assertions.assertThat(form).hasSize(2);

        for(int i = 0; i < 2; i++){
            assertEquals(
                    body.getMultiPartForm().get(i).getKey(),
                    form.get(i).getKey()
            );

            assertEquals(
                    body.getMultiPartForm().get(i).getValue(),
                    form.get(i).getValue()
            );

            assertEquals(
                    !body.getMultiPartForm().get(i).isSelected(),
                    form.get(i).getDisabled()
            );
        }
    }

    @ParameterizedTest
    @CsvSource({
            ",",
            "http:,http:",
            "http://,http://",
            "http://foo,http://foo",
            "http://foo/bar,http://foo",
            "http://foo/bar/a,http://foo",
            "{{url}},{{url}}",
            "{{url}}/foo,{{url}}",
            "{{url}}/foo/bar,{{url}}",
    })
    public void parseUrlHost(
            String input,
            String expected
    ){
        var result = PostmanCollectionDumpMapper.parseUrlHost(input);
        assertEquals(
                StringUtils.stripToEmpty(expected),
                StringUtils.stripToEmpty(result.get(0))
        );
    }

    @ParameterizedTest
    @CsvSource({
            ",",
            "http:,",
            "http://,",
            "http://foo,",
            "http://foo/bar,bar",
            "http://foo/bar/a,bar#a",
            "{{url}},",
            "{{url}}/foo,foo",
            "{{url}}/foo/bar,foo#bar",
    })
    public void parseUrlPath(
            String input,
            String expected
    ){
        var result = PostmanCollectionDumpMapper.parseUrlPath(input);
        assertEquals(
                StringUtils.stripToEmpty(expected),
                StringUtils.stripToEmpty(String.join("#", result))
        );
    }
}
