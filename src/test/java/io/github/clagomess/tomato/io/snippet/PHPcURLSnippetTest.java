package io.github.clagomess.tomato.io.snippet;

import io.github.clagomess.tomato.io.repository.RepositoryStubs;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PHPcURLSnippetTest extends RepositoryStubs implements CodeSnippetTestContract {
    private final PHPcURLSnippet snippet = new PHPcURLSnippet(null);

    @Test
    public void queryParam() throws IOException {
        var result = snippet.build(queryParamRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000/echo.php?foo=bar&opt1=lil%27+%C3%A7%C3%A3');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void url_whenContainsQuote_escape() throws IOException {
        var result = snippet.build(urlWhenContainsQuoteRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000/it\\'s');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void withMethod() throws IOException {
        var result = snippet.build(withMethodRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'DELETE');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void withCookie() throws IOException {
        var result = snippet.build(withCookieRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_COOKIE, 'foo=bar');
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void withCookie_whenContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(withCookieWhenContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_COOKIE, 'foo=it\\'s "C:\\tmp"; bar=C:\\tmp\\\\');
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void withCookie_whenKeyContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(withCookieWhenKeyContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_COOKIE, 'it\\'s "C:\\tmp"=bar; C:\\tmp\\=bar');
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void withHeaders() throws IOException {
        var result = snippet.build(withHeadersRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'foo: bar'
                ));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void withHeaders_whenContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(withHeadersWhenContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'foo: it\\'s "C:\\tmp"',
                    'bar: C:\\tmp\\\\'
                ));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void withHeaders_whenKeyContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(withHeadersWhenKeyContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'it\\'s "C:\\tmp": bar',
                    'C:\\tmp\\: bar'
                ));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void withHeadersAndCookies() throws IOException {
        var result = snippet.build(withHeadersAndCookiesRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_COOKIE, 'foo=bar; session=123');
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'foo: bar',
                    'Accept: application/json'
                ));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void withEnvironment() throws IOException {
        var result = snippet.build(withEnvironmentRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000/bar');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_COOKIE, 'foo=bar');
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'x-foo: bar'
                ));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void whenItemUnselectedOrBlankKey_ignore() throws IOException {
        var result = snippet.build(whenItemUnselectedOrBlankKeyRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_COOKIE, 'foo=bar');
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'foo: bar'
                ));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void urlEncodedForm() throws IOException {
        var result = snippet.build(urlEncodedFormRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(array(
                    'foo' => 'bar'
                )));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void urlEncodedForm_withEnvironment() throws IOException {
        var result = snippet.build(urlEncodedFormWithEnvironmentRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(array(
                    'foo' => 'bar'
                )));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void urlEncodedForm_whenContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(urlEncodedFormWhenContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(array(
                    'foo' => '"C:\\tmp"',
                    'bar' => 'C:\\tmp\\\\'
                )));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void urlEncodedForm_whenKeyContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(urlEncodedFormWhenKeyContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(array(
                    'it\\'s "C:\\tmp"' => 'bar',
                    'C:\\tmp\\\\' => 'bar'
                )));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void multiPartForm() throws IOException {
        var result = snippet.build(multiPartFormRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, array(
                    'myfile' => '@/path/to/file;type=application/json',
                    'foo' => 'bar'
                ));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void multiPartForm_withEnvironment() throws IOException {
        var result = snippet.build(multiPartFormWithEnvironmentRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, array(
                    'myfile' => '@/path/bar/file;type=application/json',
                    'foo' => 'bar'
                ));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void multiPartForm_whenContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(multiPartFormWhenContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, array(
                    'myfile' => '@/tmp/it\\'s.json;type=application/json',
                    'foo' => 'it\\'s "C:\\tmp"',
                    'bar' => 'C:\\tmp\\\\'
                ));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void multiPartForm_whenKeyContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(multiPartFormWhenKeyContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, array(
                    'it\\'s "C:\\tmp"' => '@/path/to/file;type=application/json',
                    'C:\\tmp\\\\' => 'bar'
                ));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void raw() throws IOException {
        var result = snippet.build(rawRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'Content-Type: application/json'
                ));
                curl_setopt($ch, CURLOPT_POSTFIELDS, '{"foo":"bar"}');
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void raw_whenRequestWithContentType() throws IOException {
        var result = snippet.build(rawWhenRequestWithContentTypeRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'Content-Type: text/html'
                ));
                curl_setopt($ch, CURLOPT_POSTFIELDS, '{"foo":"bar"}');
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void raw_withHeaders() throws IOException {
        var result = snippet.build(rawWithHeadersRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'foo: bar',
                    'Content-Type: application/json'
                ));
                curl_setopt($ch, CURLOPT_POSTFIELDS, '{"foo":"bar"}');
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void raw_whenContainsQuoteOrBackslash_escape() throws IOException {
        var result = snippet.build(rawWhenContainsQuoteOrBackslashRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'Content-Type: text/plain'
                ));
                curl_setopt($ch, CURLOPT_POSTFIELDS, 'it\\'s "C:\\tmp"');
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void binary() throws IOException {
        var result = snippet.build(binaryRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'Content-Type: application/octet-stream'
                ));
                curl_setopt($ch, CURLOPT_POSTFIELDS, file_get_contents('bitmap.png'));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void binary_withEnvironment() throws IOException {
        var result = snippet.build(binaryWithEnvironmentRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'Content-Type: application/octet-stream'
                ));
                curl_setopt($ch, CURLOPT_POSTFIELDS, file_get_contents('bar/bitmap.png'));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }

    @Test
    public void binary_whenContainsQuote_escape() throws IOException {
        var result = snippet.build(binaryWhenContainsQuoteRequest());
        assertThat(result)
                .isEqualToIgnoringNewLines("""
                <?php
                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, 'http://localhost:8000');
                curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'Content-Type: application/octet-stream'
                ));
                curl_setopt($ch, CURLOPT_POSTFIELDS, file_get_contents('/tmp/it\\'s.png'));
                $response = curl_exec($ch);
                curl_close($ch);
                echo $response;
                """);
    }
}
