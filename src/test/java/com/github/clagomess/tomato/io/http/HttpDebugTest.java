package com.github.clagomess.tomato.io.http;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = 8500)
public class HttpDebugTest {
    @Test
    public void assembly_whenRequestIsNull(){
        var debug = new HttpDebug();
        var result = debug.assembly();
        Assertions.assertThat(result).isBlank();
    }

    @Test
    public void assembly_whenPOSTString() throws URISyntaxException, IOException, InterruptedException {
        var bodyString = "hello world";

        var debug = new HttpDebug();
        debug.setRequestBodyString(bodyString);

        HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:8500/hello"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(bodyString))
                .build();

        debug.setRequest(request);

        File responseFile = HttpService.createTempFile();
        debug.setResponseBodyFile(responseFile);

        HttpResponse<Path> response = HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofFile(responseFile.toPath())
        );
        debug.setResponse(response);

        Assertions.assertThat(debug.assembly())
                .containsIgnoringNewLines(
                        """
                        > POST http://localhost:8500/hello
                        > Content-Type: application/json
                        
                        hello world
                        ----------------------------------------
                        < HTTP_1_1 200 OK
                        < content-type: text/plain
                        < matched-stub-id: afe1ec08-4106-4f93-8dab-a4a7cc4e9241
                        < transfer-encoding: chunked
                        
                        hello
                        """
                );
    }

    @Test
    public void assemblyHeader_whenSingle(){
        var debug = new HttpDebug();
        var headers = Map.of("key", List.of("value"));
        var result = debug.assemblyHeader("> ", headers);
        assertEquals("> key: value\n", result.toString());
    }

    @Test
    public void assemblyHeader_whenMutiple(){
        var debug = new HttpDebug();
        var headers = Map.of("key", List.of("value1", "value2"));
        var result = debug.assemblyHeader("< ", headers);
        assertEquals(
                "< key: value1\n< key: value2\n",
                result.toString()
        );
    }

    @ParameterizedTest
    @CsvSource({
            "helloword,hello[more 4 bytes]",
            "hello,hello"
    })
    public void assemblyBody_whenConstructString(
            String input,
            String expected
    ){
        var debug = new HttpDebug();
        var result = debug.assemblyBody(input, 5);

        Assertions.assertThat(result).isEqualToIgnoringNewLines(expected);
    }

    @Test
    public void assemblyBody_whenConstructFileWithSizeLessThan10Bytes(){
        var debug = new HttpDebug();
        var file = new File(getClass()
                .getResource("HttpDebugTest/file-less-than-10bytes.txt")
                .getFile()
        );
        var result = debug.assemblyBody(file, UTF_8, 10);

        assertEquals("hello", result.trim());
    }

    @Test
    public void assemblyBody_whenConstructFileWithSizeMoreThan10Bytes(){
        var debug = new HttpDebug();
        var file = new File(getClass()
                .getResource("HttpDebugTest/file-more-than-10bytes.txt")
                .getFile()
        );
        var result = debug.assemblyBody(file, UTF_8, 10);

        Assertions.assertThat(result)
                .contains("helloworda\n[more");
    }
}
