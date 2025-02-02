package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.MediaType;
import org.mockserver.model.*;

import java.util.Collections;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.FILE;
import static com.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static org.junit.jupiter.api.Assertions.*;

public class HttpServicePerformTest {
    private static ClientAndServer mockServer;
    private static MockedConstruction<EnvironmentRepository> environmentRepositoryMockedConstruction;

    @BeforeAll
    public static void setup(){
        mockServer = ClientAndServer.startClientAndServer(8500);
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/hello")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.TEXT_PLAIN)
                                .withBody("hello")
                );

        environmentRepositoryMockedConstruction = Mockito.mockConstruction(EnvironmentRepository.class);
    }

    @AfterAll
    public static void terminate(){
        mockServer.stop();
        environmentRepositoryMockedConstruction.close();
    }

    @Test
    public void get_response_json() {
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/get_response_json")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody("{\"foo\": \"bar\", \"true\": false, \"number\": 1}")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/get_response_json");
        request.setMethod(HttpMethodEnum.GET);

        ResponseDto response = new HttpService(request).perform();

        assertTrue(response.isRequestStatus());
        assertNull(response.getRequestMessage());
        assertEquals(200, response.getHttpResponse().getStatus());
        assertEquals("OK", response.getHttpResponse().getStatusReason());
        assertEquals(42, response.getHttpResponse().getBodySize());
        assertEquals("application/json", response.getHttpResponse().getContentType().toString());
        assertNotNull(response.getRequestDebug());
    }

    @Test
    public void get_response_binary() {
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/get_response_binary")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.create("application", "pdf"))
                                .withBody(new byte[]{0x25, 0x50, 0x44, 0x46})
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/get_response_binary");
        request.setMethod(HttpMethodEnum.GET);

        ResponseDto response = new HttpService(request).perform();

        assertTrue(response.isRequestStatus());
        assertNull(response.getRequestMessage());
        assertEquals(4, response.getHttpResponse().getBodySize());
        assertEquals("application/pdf", response.getHttpResponse().getContentType().toString());

        Assertions.assertThat(response.getHttpResponse().getBody())
                .content()
                .isEqualTo("%PDF");
    }

    @Test
    public void set_headers() {
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/set_headers")
                                .withHeader(new Header("foo", "bar"))
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody("{}")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/set_headers");
        request.setMethod(HttpMethodEnum.GET);
        request.setHeaders(Collections.singletonList(new KeyValueItemDto("foo", "bar")));

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
    }

    @Test
    public void get_headers() {
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/get_headers")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withHeader(new Header("foo", "bar"))
                                .withBody("{}")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/get_headers");
        request.setMethod(HttpMethodEnum.GET);

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
        assertTrue(response.getHttpResponse().getHeaders().keySet().stream().anyMatch("foo"::equals));
    }

    @Test
    public void follow_redirect() {
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/follow_redirect")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(301)
                                .withHeader(new Header("Location", "http://localhost:8500/hello"))
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/follow_redirect");
        request.setMethod(HttpMethodEnum.GET);

        ResponseDto response = new HttpService(request).perform();
        assertEquals(301, response.getHttpResponse().getStatus());
        assertEquals("< Empty Body", response.getHttpResponse().getBodyAsString());
    }

    @Test
    public void conection_error(){
        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8110/");
        request.setMethod(HttpMethodEnum.GET);

        ResponseDto response = new HttpService(request).perform();

        assertNull(response.getHttpResponse());
        assertNotNull(response.getRequestDebug());
        assertFalse(response.isRequestStatus());
        assertNotNull(response.getRequestMessage());
    }

    @Test
    public void conection_drop(){
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/conection_drop")
                )
                .error(
                        HttpError.error()
                                .withDropConnection(true)
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/conection_drop");
        request.setMethod(HttpMethodEnum.GET);

        ResponseDto response = new HttpService(request).perform();

        assertNull(response.getHttpResponse());
        assertNotNull(response.getRequestDebug());
        assertFalse(response.isRequestStatus());
        assertNotNull(response.getRequestMessage());
    }

    @Test
    public void set_cookie() {
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/set_cookie")
                                .withCookie("foo", "bar")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody("{}")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/set_cookie");
        request.setMethod(HttpMethodEnum.GET);
        request.getCookies().add(new KeyValueItemDto("foo", "bar"));

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
    }

    @Test
    public void get_cookie() {
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/get_cookie")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withCookie("foo", "bar")
                                .withBody("{}")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/get_cookie");
        request.setMethod(HttpMethodEnum.GET);

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
        assertEquals("bar", response.getHttpResponse().getCookies().get("foo"));
    }

    @Test
    public void post_urlencoded_form(){
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("POST")
                                .withPath("/post_urlencoded_form")
                                .withBody(ParameterBody.params(
                                        Parameter.param("foo", "bar")
                                ))
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withCookie("foo", "bar")
                                .withBody("{}")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/post_urlencoded_form");
        request.setMethod(HttpMethodEnum.POST);
        request.setBody(new RequestDto.Body());
        request.getBody().setType(BodyTypeEnum.URL_ENCODED_FORM);
        request.getBody().setUrlEncodedForm(Collections.singletonList(new KeyValueItemDto("foo", "bar")));

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
    }

    @Test
    public void get_with_query_param(){
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/get_with_query_param")
                                .withQueryStringParameter("foo", "bar")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.TEXT_PLAIN)
                                .withBody("hello")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/get_with_query_param?foo=bar");
        request.setMethod(HttpMethodEnum.GET);

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
    }

    @Test
    public void get_with_response_500(){
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/get_with_response_500")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(500)
                                .withContentType(MediaType.TEXT_PLAIN)
                                .withBody("hello")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/get_with_response_500");
        request.setMethod(HttpMethodEnum.GET);

        ResponseDto response = new HttpService(request).perform();
        assertEquals(500, response.getHttpResponse().getStatus());
        assertEquals("hello", response.getHttpResponse().getBodyAsString());
    }

    @Test
    public void post_multipart_form(){
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/post_multipart_form")
                ).respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withContentType(MediaType.TEXT_PLAIN)
                        .withBody("hello")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/post_multipart_form");
        request.setMethod(HttpMethodEnum.POST);
        request.setBody(new RequestDto.Body());
        request.getBody().setType(BodyTypeEnum.MULTIPART_FORM);
        request.getBody().getMultiPartForm().add(new KeyValueItemDto("foo", "bar"));

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
    }

    @Test
    public void post_multipart_form_with_file(){
        mockServer.when(HttpRequest.request()
                .withMethod("POST")
                .withPath("/post_multipart_form_with_file")
        ).respond(HttpResponse.response()
                .withStatusCode(200)
                .withContentType(MediaType.TEXT_PLAIN)
                .withBody("hello")
        );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/post_multipart_form_with_file");
        request.setMethod(HttpMethodEnum.POST);
        request.setBody(new RequestDto.Body());
        request.getBody().setType(BodyTypeEnum.MULTIPART_FORM);

        String formFile = getClass()
                .getResource("HttpServicePerformTest/dummy.txt")
                .getFile();

        request.getBody().getMultiPartForm().add(new KeyValueItemDto(
                FILE,
                "key",
                formFile,
                null,
                true
        ));

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
    }

    @Test
    public void post_raw(){
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("POST")
                                .withPath("/post_raw")
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(JsonBody.json("{\"foo\": \"bar\"}", MatchType.STRICT))
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.TEXT_PLAIN)
                                .withBody("hello")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/post_raw");
        request.setMethod(HttpMethodEnum.POST);
        request.setBody(new RequestDto.Body());
        request.getBody().setType(BodyTypeEnum.RAW);
        request.getBody().setRaw(new RequestDto.RawBody(
                RawBodyTypeEnum.JSON,
                "{\"foo\": \"bar\"}"
        ));

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
    }

    @Test
    public void post_binary(){
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("POST")
                                .withPath("/post_binary")
                                .withContentType(MediaType.APPLICATION_OCTET_STREAM)
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.TEXT_PLAIN)
                                .withBody("hello")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/post_binary");
        request.setMethod(HttpMethodEnum.POST);
        request.setBody(new RequestDto.Body());
        request.getBody().setType(BodyTypeEnum.BINARY);
        request.getBody().setBinary(new RequestDto.BinaryBody(
                APPLICATION_OCTET_STREAM_TYPE,
                ".gitignore"
        ));

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
    }

    @Test
    public void put_raw(){
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("PUT")
                                .withPath("/put_raw")
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(JsonBody.json("{\"foo\": \"bar\"}", MatchType.STRICT))
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.TEXT_PLAIN)
                                .withBody("hello")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/put_raw");
        request.setMethod(HttpMethodEnum.PUT);
        request.setBody(new RequestDto.Body());
        request.getBody().setType(BodyTypeEnum.RAW);
        request.getBody().setRaw(new RequestDto.RawBody(
                RawBodyTypeEnum.JSON,
                "{\"foo\": \"bar\"}"
        ));

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
    }

    @Test
    public void delete(){
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("DELETE")
                                .withPath("/delete")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.TEXT_PLAIN)
                                .withBody("hello")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/delete");
        request.setMethod(HttpMethodEnum.DELETE);

        ResponseDto response = new HttpService(request).perform();
        assertEquals(200, response.getHttpResponse().getStatus());
    }
}
