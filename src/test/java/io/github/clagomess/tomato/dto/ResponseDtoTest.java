package io.github.clagomess.tomato.dto;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.clagomess.tomato.io.http.HttpService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

@WireMockTest(httpPort = 8500)
class ResponseDtoTest {
    @ParameterizedTest
    @CsvSource({
            "PHPSESSID=xyz; path=/,PHPSESSID,xyz",
            "xburger=opt1,xburger,opt1",
    })
    void Response_parseSetCookies(
            String input,
            String expectedKey,
            String expectedValue
    ) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:8500/hello"))
                .GET()
                .build();

        HttpResponse<Path> httpResponse = HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofFile(HttpService.createTempFile().toPath())
        );

        // teste
        var response = new ResponseDto.Response(httpResponse, 0L);
        var result = response.parseSetCookies(Map.of(
            "set-cookie", List.of(input)
        ));

        assertEquals(expectedValue, result.get(expectedKey));
    }

    @ParameterizedTest
    @CsvSource({
            "/response-binary,response.bin",
            "/sample.pdf,sample.pdf",
            "/sample-pdf-with-disposition-filename,mypdf.pdf",
            "/octet-stream,response.bin",
    })
    void Response_parseBodyDownloadFileName(
            String method,
            String expected
    ) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI(
                "http://localhost:8500" + method
        )).GET().build();

        HttpResponse<Path> httpResponse = HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofFile(HttpService.createTempFile().toPath())
        );

        var response = new ResponseDto.Response(httpResponse, 0L);

        assertEquals(expected, response.getBodyDownloadFileName());
    }
}
