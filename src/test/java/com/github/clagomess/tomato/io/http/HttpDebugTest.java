package com.github.clagomess.tomato.io.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;

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
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class HttpDebugTest {
    private static ClientAndServer mockServer;

    @BeforeAll
    public static void setup(){
        mockServer = ClientAndServer.startClientAndServer(8500);
        mockServer.when(request().withPath("/hello")).respond(response()
                .withStatusCode(200)
                .withContentType(MediaType.TEXT_PLAIN)
                .withBody("hello")
        );
    }

    @AfterAll
    public static void terminate(){
        mockServer.stop();
    }

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

        File resposeFile = File.createTempFile("tomato-test-", ".bin");
        resposeFile.deleteOnExit();
        debug.setResponseBodyFile(resposeFile);

        HttpResponse<Path> response = HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofFile(resposeFile.toPath())
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
                        < connection: keep-alive
                        < content-length: 5
                        < content-type: text/plain
                        
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
