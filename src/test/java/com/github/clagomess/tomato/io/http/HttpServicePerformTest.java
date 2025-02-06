package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.request.BinaryBodyDto;
import com.github.clagomess.tomato.dto.data.request.BodyDto;
import com.github.clagomess.tomato.dto.data.request.RawBodyDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Objects;

import static com.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;
import static com.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8500)
public class HttpServicePerformTest {

    @Test
    public void get_response_json() {
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/response-json");
        request.setMethod(HttpMethodEnum.GET);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();

            assertTrue(response.isRequestStatus());
            assertNull(response.getRequestMessage());
            assertEquals(200, response.getHttpResponse().getStatus());
            assertEquals("OK", response.getHttpResponse().getStatusReason());
            assertEquals(37, response.getHttpResponse().getBodySize());
            assertEquals("application/json", response.getHttpResponse().getContentType().toString());
            assertNotNull(response.getRequestDebug());
        }
    }

    @Test
    public void get_response_binary() {
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/response-binary");
        request.setMethod(HttpMethodEnum.GET);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();

            assertTrue(response.isRequestStatus());
            assertNull(response.getRequestMessage());
            assertEquals(4, response.getHttpResponse().getBodySize());
            assertEquals("application/pdf", response.getHttpResponse().getContentType().toString());

            Assertions.assertThat(response.getHttpResponse().getBody())
                    .content()
                    .isEqualTo("%PDF");
        }
    }

    @Test
    public void get_response_gzip() {
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/response-gzip");
        request.setMethod(HttpMethodEnum.GET);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();

            assertTrue(response.isRequestStatus());
            assertNull(response.getRequestMessage());
            Assertions.assertThat(response.getHttpResponse().getBody())
                    .content()
                    .isEqualTo("{\"foo\":\"bar\"}");
        }
    }

    @Test
    public void set_headers() {
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/set-headers");
        request.setMethod(HttpMethodEnum.GET);
        request.setHeaders(Collections.singletonList(new KeyValueItemDto("foo", "bar")));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
        }
    }

    @Test
    public void get_headers() {
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/get-headers");
        request.setMethod(HttpMethodEnum.GET);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
            assertTrue(response.getHttpResponse().getHeaders().keySet().stream().anyMatch("foo"::equals));
        }
    }

    @Test
    public void follow_redirect() {
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/redirect");
        request.setMethod(HttpMethodEnum.GET);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(301, response.getHttpResponse().getStatus());
            assertEquals("< Empty Body", response.getHttpResponse().getBodyAsString());
        }
    }

    @Test
    public void conection_error(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8110/");
        request.setMethod(HttpMethodEnum.GET);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();

            assertNull(response.getHttpResponse());
            assertNotNull(response.getRequestDebug());
            assertFalse(response.isRequestStatus());
            assertNotNull(response.getRequestMessage());
        }
    }

    @Test
    public void conection_drop(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/conection-drop");
        request.setMethod(HttpMethodEnum.GET);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();

            assertNull(response.getHttpResponse());
            assertNotNull(response.getRequestDebug());
            assertFalse(response.isRequestStatus());
            assertNotNull(response.getRequestMessage());
        }
    }

    @Test
    public void set_cookie() {
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/set-cookie");
        request.setMethod(HttpMethodEnum.GET);
        request.getCookies().add(new KeyValueItemDto("foo", "bar"));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
        }
    }

    @Test
    public void get_cookie() {
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/get-cookie");
        request.setMethod(HttpMethodEnum.GET);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
            assertEquals("bar", response.getHttpResponse().getCookies().get("foo"));
        }
    }

    @Test
    public void post_urlencoded_form(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/urlencoded-form");
        request.setMethod(HttpMethodEnum.POST);
        request.setBody(new BodyDto());
        request.getBody().setType(BodyTypeEnum.URL_ENCODED_FORM);
        request.getBody().setUrlEncodedForm(Collections.singletonList(new ContentTypeKeyValueItemDto("foo", "bar")));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
        }
    }

    @Test
    public void get_with_query_param(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/with-query-param?foo=bar");
        request.setMethod(HttpMethodEnum.GET);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
        }
    }

    @Test
    public void get_with_response_500(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/response-500");
        request.setMethod(HttpMethodEnum.GET);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(500, response.getHttpResponse().getStatus());
            assertEquals("hello", response.getHttpResponse().getBodyAsString());
        }
    }

    @Test
    public void post_multipart_form(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/multipart-form");
        request.setMethod(HttpMethodEnum.POST);
        request.setBody(new BodyDto());
        request.getBody().setType(BodyTypeEnum.MULTIPART_FORM);
        request.getBody().getMultiPartForm().add(new FileKeyValueItemDto("foo", "bar"));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
        }
    }

    @Test
    public void post_multipart_form_with_file(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/multipart-form-with-file");
        request.setMethod(HttpMethodEnum.POST);
        request.setBody(new BodyDto());
        request.getBody().setType(BodyTypeEnum.MULTIPART_FORM);

        String formFile = Objects.requireNonNull(getClass()
                        .getResource("HttpServicePerformTest/dummy.txt"))
                .getFile();

        request.getBody().getMultiPartForm().add(new FileKeyValueItemDto(
                FILE,
                "key",
                formFile,
                null,
                true
        ));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
        }
    }

    @Test
    public void post_raw(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/raw");
        request.setMethod(HttpMethodEnum.POST);
        request.setBody(new BodyDto());
        request.getBody().setType(BodyTypeEnum.RAW);
        request.getBody().setRaw(new RawBodyDto(
                RawBodyTypeEnum.JSON,
                "{\"foo\": \"bar\"}"
        ));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
        }
    }

    @Test
    public void post_binary(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/binary");
        request.setMethod(HttpMethodEnum.POST);
        request.setBody(new BodyDto());
        request.getBody().setType(BodyTypeEnum.BINARY);
        request.getBody().setBinary(new BinaryBodyDto(
                APPLICATION_OCTET_STREAM_TYPE,
                ".gitignore"
        ));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
        }
    }

    @Test
    public void put_raw(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/raw");
        request.setMethod(HttpMethodEnum.PUT);
        request.setBody(new BodyDto());
        request.getBody().setType(BodyTypeEnum.RAW);
        request.getBody().setRaw(new RawBodyDto(
                RawBodyTypeEnum.JSON,
                "{\"foo\": \"bar\"}"
        ));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
        }
    }

    @Test
    public void delete(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/hello");
        request.setMethod(HttpMethodEnum.DELETE);

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            ResponseDto response = new HttpService(request).perform();
            assertEquals(200, response.getHttpResponse().getStatus());
        }
    }
}
