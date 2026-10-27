package io.github.clagomess.tomato.io.snippet;

import io.github.clagomess.tomato.io.repository.RepositoryStubs;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.github.clagomess.tomato.io.snippet.CurlSnippet.Type.POWERSHELL;
import static org.assertj.core.api.Assertions.assertThat;

class CurlSnippetPOWERSHELLTest extends RepositoryStubs implements CodeSnippetTestContract {
    private final CurlSnippet snippet = new CurlSnippet(null, POWERSHELL);

    @Test
    public void queryParam() throws IOException {
        var result = snippet.build(queryParamRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000/echo.php?foo=bar&opt1=lil%27+%C3%A7%C3%A3'
                """);
    }

    @Test
    public void url_whenContainsQuote_escape() throws IOException {
        var result = snippet.build(urlWhenContainsQuoteRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000/it''s'
                """);
    }

    @Test
    public void withMethod() throws IOException {
        var result = snippet.build(withMethodRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X DELETE 'http://localhost:8000'
                """);
    }

    @Test
    public void withCookie() throws IOException {
        var result = snippet.build(withCookieRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000' `
                -H 'Cookie: foo=bar'
                """);
    }

    @Test
    public void withCookie_whenContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(withCookieWhenContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000' `
                -H 'Cookie: foo=it''s "C:\\tmp"' `
                -H 'Cookie: bar=C:\\tmp\\'
                """);
    }

    @Test
    public void withCookie_whenKeyContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(withCookieWhenKeyContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000' `
                -H 'Cookie: it''s "C:\\tmp"=bar' `
                -H 'Cookie: C:\\tmp\\=bar'
                """);
    }

    @Test
    public void withHeaders() throws IOException {
        var result = snippet.build(withHeadersRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000' `
                -H 'foo: bar'
                """);
    }

    @Test
    public void withHeaders_whenContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(withHeadersWhenContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000' `
                -H 'foo: it''s "C:\\tmp"' `
                -H 'bar: C:\\tmp\\'
                """);
    }

    @Test
    public void withHeaders_whenKeyContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(withHeadersWhenKeyContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000' `
                -H 'it''s "C:\\tmp": bar' `
                -H 'C:\\tmp\\: bar'
                """);
    }

    @Test
    public void withHeadersAndCookies() throws IOException {
        var result = snippet.build(withHeadersAndCookiesRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000' `
                -H 'foo: bar' `
                -H 'Accept: application/json' `
                -H 'Cookie: foo=bar' `
                -H 'Cookie: session=123'
                """);
    }

    @Test
    public void withEnvironment() throws IOException {
        var result = snippet.build(withEnvironmentRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000/bar' `
                -H 'x-foo: bar' `
                -H 'Cookie: foo=bar'
                """);
    }

    @Test
    public void whenItemUnselectedOrBlankKey_ignore() throws IOException {
        var result = snippet.build(whenItemUnselectedOrBlankKeyRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X GET 'http://localhost:8000' `
                -H 'foo: bar' `
                -H 'Cookie: foo=bar'
                """);
    }

    @Test
    public void urlEncodedForm() throws IOException {
        var result = snippet.build(urlEncodedFormRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -d 'foo=bar'
                """);
    }

    @Test
    public void urlEncodedForm_withEnvironment() throws IOException {
        var result = snippet.build(urlEncodedFormWithEnvironmentRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -d 'foo=bar'
                """);
    }

    @Test
    public void urlEncodedForm_whenContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(urlEncodedFormWhenContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -d 'foo="C:\\tmp"' `
                -d 'bar=C:\\tmp\\'
                """);
    }

    @Test
    public void urlEncodedForm_whenKeyContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(urlEncodedFormWhenKeyContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -d 'it''s "C:\\tmp"=bar' `
                -d 'C:\\tmp\\=bar'
                """);
    }

    @Test
    public void multiPartForm() throws IOException {
        var result = snippet.build(multiPartFormRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -F 'myfile=@/path/to/file' `
                -F 'foo=bar'
                """);
    }

    @Test
    public void multiPartForm_withEnvironment() throws IOException {
        var result = snippet.build(multiPartFormWithEnvironmentRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -F 'myfile=@/path/bar/file' `
                -F 'foo=bar'
                """);
    }

    @Test
    public void multiPartForm_whenContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(multiPartFormWhenContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -F 'myfile=@/tmp/it''s.json' `
                -F 'foo=it''s "C:\\tmp"' `
                -F 'bar=C:\\tmp\\'
                """);
    }

    @Test
    public void multiPartForm_whenKeyContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(multiPartFormWhenKeyContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -F 'it''s "C:\\tmp"=@/path/to/file' `
                -F 'C:\\tmp\\=bar'
                """);
    }

    @Test
    public void raw() throws IOException {
        var result = snippet.build(rawRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -H 'Content-Type: application/json' `
                --data-raw '{"foo":"bar"}'
                """);
    }

    @Test
    public void raw_whenRequestWithContentType() throws IOException {
        var result = snippet.build(rawWhenRequestWithContentTypeRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -H 'Content-Type: text/html' `
                --data-raw '{"foo":"bar"}'
                """);
    }

    @Test
    public void raw_withHeaders() throws IOException {
        var result = snippet.build(rawWithHeadersRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -H 'foo: bar' `
                -H 'Content-Type: application/json' `
                --data-raw '{"foo":"bar"}'
                """);
    }

    @Test
    public void raw_whenContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(rawWhenContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -H 'Content-Type: text/plain' `
                --data-raw 'it''s "C:\\tmp"'
                """);
    }

    @Test
    public void binary() throws IOException {
        var result = snippet.build(binaryRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -H 'Content-Type: application/octet-stream' `
                --data-binary '@bitmap.png'
                """);
    }

    @Test
    public void binary_withEnvironment() throws IOException {
        var result = snippet.build(binaryWithEnvironmentRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -H 'Content-Type: application/octet-stream' `
                --data-binary '@bar/bitmap.png'
                """);
    }

    @Test
    public void binary_whenContainsQuote_escape() throws IOException {
        var result = snippet.build(binaryWhenContainsQuoteRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                curl -X POST 'http://localhost:8000' `
                -H 'Content-Type: application/octet-stream' `
                --data-binary '@/tmp/it''s.png'
                """);
    }
}
