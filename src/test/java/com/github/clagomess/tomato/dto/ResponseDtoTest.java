package com.github.clagomess.tomato.dto;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ResponseDtoTest {
    private static ClientAndServer mockServer;

    @BeforeAll
    public static void setup() {
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

    @ParameterizedTest
    @CsvSource({
            "PHPSESSID=xyz; path=/,PHPSESSID,xyz",
            "xburger=opt1,xburger,opt1",
    })
    public void Response_parseSetCookies(
            String input,
            String expectedKey,
            String expectedValue
    ) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:8500/hello"))
                .GET()
                .build();

        File resposeFile = File.createTempFile("tomato-test-", ".bin");
        resposeFile.deleteOnExit();

        HttpResponse<Path> httpResponse = HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofFile(resposeFile.toPath())
        );

        // teste
        var response = new ResponseDto.Response(httpResponse, 0L);
        var result = response.parseSetCookies(Map.of(
            "set-cookie", List.of(input)
        ));

        assertEquals(expectedValue, result.get(expectedKey));
    }
}
