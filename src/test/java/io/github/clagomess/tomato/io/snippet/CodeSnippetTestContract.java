package io.github.clagomess.tomato.io.snippet;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.data.request.BinaryBodyDto;
import io.github.clagomess.tomato.dto.data.request.RawBodyDto;
import io.github.clagomess.tomato.enums.BodyTypeEnum;
import io.github.clagomess.tomato.enums.HttpMethodEnum;
import io.github.clagomess.tomato.enums.RawBodyTypeEnum;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;
import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.TEXT;
import static io.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;

interface CodeSnippetTestContract {
    @BeforeAll
    static void setupEnvironment(){
        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .addListener(query -> List.of(new EnvironmentItemDto("foo", "bar")));
    }

    @AfterAll
    static void unloadEnvironment(){
        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .getListeners()
                .remove();
    }

    void queryParam() throws Exception;
    void url_whenContainsQuote_escape() throws Exception;
    void withMethod() throws Exception;
    void withCookie() throws Exception;
    void withCookie_whenContainsQuoteOrBackslash_escape() throws Exception;
    void withCookie_whenKeyContainsQuoteOrBackslash_escape() throws Exception;
    void withHeaders() throws Exception;
    void withHeaders_whenContainsQuoteOrBackslash_escape() throws Exception;
    void withHeaders_whenKeyContainsQuoteOrBackslash_escape() throws Exception;
    void withHeadersAndCookies() throws Exception;
    void withEnvironment() throws Exception;
    void whenItemUnselectedOrBlankKey_ignore() throws Exception;
    void urlEncodedForm() throws Exception;
    void urlEncodedForm_withEnvironment() throws Exception;
    void urlEncodedForm_whenContainsQuoteOrBackslash_escape() throws Exception;
    void urlEncodedForm_whenKeyContainsQuoteOrBackslash_escape() throws Exception;
    void multiPartForm() throws Exception;
    void multiPartForm_withEnvironment() throws Exception;
    void multiPartForm_whenContainsQuoteOrBackslash_escape() throws Exception;
    void multiPartForm_whenKeyContainsQuoteOrBackslash_escape() throws Exception;
    void raw() throws Exception;
    void raw_whenRequestWithContentType() throws Exception;
    void raw_withHeaders() throws Exception;
    void raw_whenContainsQuoteOrBackslash_escape() throws Exception;
    void binary() throws Exception;
    void binary_withEnvironment() throws Exception;
    void binary_whenContainsQuote_escape() throws Exception;

    default RequestDto queryParamRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000/:foo");
        request.getUrlParam().setQuery(List.of(
                new ContentTypeKeyValueItemDto("foo", "bar"),
                new ContentTypeKeyValueItemDto("opt1", "lil' çã")
        ));
        request.getUrlParam().setPath(List.of(
                new KeyValueItemDto("foo", "echo.php")
        ));

        return request;
    }

    default RequestDto urlWhenContainsQuoteRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000/it's");

        return request;
    }

    default RequestDto withMethodRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.DELETE);

        return request;
    }

    default RequestDto withCookieRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setCookies(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        return request;
    }

    default RequestDto withCookieWhenContainsQuoteOrBackslashRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setCookies(List.of(
                new KeyValueItemDto("foo", "it's \"C:\\tmp\""),
                new KeyValueItemDto("bar", "C:\\tmp\\")
        ));

        return request;
    }

    default RequestDto withCookieWhenKeyContainsQuoteOrBackslashRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setCookies(List.of(
                new KeyValueItemDto("it's \"C:\\tmp\"", "bar"),
                new KeyValueItemDto("C:\\tmp\\", "bar")
        ));

        return request;
    }

    default RequestDto withHeadersRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setHeaders(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        return request;
    }

    default RequestDto withHeadersWhenContainsQuoteOrBackslashRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setHeaders(List.of(
                new KeyValueItemDto("foo", "it's \"C:\\tmp\""),
                new KeyValueItemDto("bar", "C:\\tmp\\")
        ));

        return request;
    }

    default RequestDto withHeadersWhenKeyContainsQuoteOrBackslashRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setHeaders(List.of(
                new KeyValueItemDto("it's \"C:\\tmp\"", "bar"),
                new KeyValueItemDto("C:\\tmp\\", "bar")
        ));

        return request;
    }

    default RequestDto withHeadersAndCookiesRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setHeaders(List.of(
                new KeyValueItemDto("foo", "bar"),
                new KeyValueItemDto("Accept", "application/json")
        ));
        request.setCookies(List.of(
                new KeyValueItemDto("foo", "bar"),
                new KeyValueItemDto("session", "123")
        ));

        return request;
    }

    default RequestDto withEnvironmentRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000/{{foo}}");
        request.setHeaders(List.of(
                new KeyValueItemDto("x-foo", "{{foo}}")
        ));
        request.setCookies(List.of(
                new KeyValueItemDto("foo", "{{foo}}")
        ));

        return request;
    }

    default RequestDto whenItemUnselectedOrBlankKeyRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setHeaders(List.of(
                new KeyValueItemDto("foo", "bar"),
                new KeyValueItemDto("unselected", "bar", false),
                new KeyValueItemDto(" ", "bar")
        ));
        request.setCookies(List.of(
                new KeyValueItemDto("foo", "bar"),
                new KeyValueItemDto("unselected", "bar", false),
                new KeyValueItemDto("", "bar")
        ));

        return request;
    }

    default RequestDto urlEncodedFormRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.URL_ENCODED_FORM);
        request.getBody().setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("foo", "bar")
        ));

        return request;
    }

    default RequestDto urlEncodedFormWithEnvironmentRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.URL_ENCODED_FORM);
        request.getBody().setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("foo", "{{foo}}")
        ));

        return request;
    }

    default RequestDto urlEncodedFormWhenContainsQuoteOrBackslashRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.URL_ENCODED_FORM);
        request.getBody().setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("foo", "\"C:\\tmp\""),
                new ContentTypeKeyValueItemDto("bar", "C:\\tmp\\")
        ));

        return request;
    }

    default RequestDto urlEncodedFormWhenKeyContainsQuoteOrBackslashRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.URL_ENCODED_FORM);
        request.getBody().setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("it's \"C:\\tmp\"", "bar"),
                new ContentTypeKeyValueItemDto("C:\\tmp\\", "bar")
        ));

        return request;
    }

    default RequestDto multiPartFormRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.MULTIPART_FORM);
        request.getBody().setMultiPartForm(List.of(
                new FileKeyValueItemDto(FILE, "myfile", "/path/to/file", "application/json", true),
                new FileKeyValueItemDto(TEXT, "foo", "bar", "text/plain", true)
        ));

        return request;
    }

    default RequestDto multiPartFormWithEnvironmentRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.MULTIPART_FORM);
        request.getBody().setMultiPartForm(List.of(
                new FileKeyValueItemDto(FILE, "myfile", "/path/{{foo}}/file", "application/json", true),
                new FileKeyValueItemDto(TEXT, "foo", "{{foo}}", "text/plain", true)
        ));

        return request;
    }

    default RequestDto multiPartFormWhenContainsQuoteOrBackslashRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.MULTIPART_FORM);
        request.getBody().setMultiPartForm(List.of(
                new FileKeyValueItemDto(FILE, "myfile", "/tmp/it's.json", "application/json", true),
                new FileKeyValueItemDto(TEXT, "foo", "it's \"C:\\tmp\"", "text/plain", true),
                new FileKeyValueItemDto(TEXT, "bar", "C:\\tmp\\", "text/plain", true)
        ));

        return request;
    }

    default RequestDto multiPartFormWhenKeyContainsQuoteOrBackslashRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.MULTIPART_FORM);
        request.getBody().setMultiPartForm(List.of(
                new FileKeyValueItemDto(FILE, "it's \"C:\\tmp\"", "/path/to/file", "application/json", true),
                new FileKeyValueItemDto(TEXT, "C:\\tmp\\", "bar", "text/plain", true)
        ));

        return request;
    }

    default RequestDto rawRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.RAW);
        request.getBody().setRaw(new RawBodyDto(
                RawBodyTypeEnum.JSON,
                "{\"foo\":\"bar\"}"
        ));

        return request;
    }

    default RequestDto rawWhenRequestWithContentTypeRequest(){
        var request = rawRequest();
        request.setHeaders(List.of(
                new ContentTypeKeyValueItemDto("Content-Type", "text/html")
        ));

        return request;
    }

    default RequestDto rawWithHeadersRequest(){
        var request = rawRequest();
        request.setHeaders(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        return request;
    }

    default RequestDto rawWhenContainsQuoteOrBackslashRequest(){
        var request = rawRequest();
        request.getBody().setRaw(new RawBodyDto(
                RawBodyTypeEnum.TEXT,
                "it's \"C:\\tmp\""
        ));

        return request;
    }

    default RequestDto binaryRequest(){
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.BINARY);
        request.getBody().setBinary(new BinaryBodyDto(
                APPLICATION_OCTET_STREAM_TYPE,
                "bitmap.png"
        ));

        return request;
    }

    default RequestDto binaryWithEnvironmentRequest(){
        var request = binaryRequest();
        request.getBody().getBinary().setFile("{{foo}}/bitmap.png");

        return request;
    }

    default RequestDto binaryWhenContainsQuoteRequest(){
        var request = binaryRequest();
        request.getBody().getBinary().setFile("/tmp/it's.png");

        return request;
    }
}
