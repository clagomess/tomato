package io.github.clagomess.tomato.io.http;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.net.URISyntaxException;

import static io.github.clagomess.tomato.io.http.SSHProxyWrapper.LOCALHOST;
import static org.junit.Assert.assertEquals;

public class SSHProxyWrapperTest {
    private final SSHProxyWrapper wrapper = new SSHProxyWrapper();

    @ParameterizedTest
    @CsvSource({
            "https://localhost.com.br:8080,8080",
            "https://localhost.com.br,443",
            "http://localhost.com.br,80",
    })
    void getURIPort(String input, Integer expectedPort) {
        URI uri = URI.create(input);
        var result = wrapper.getURIPort(uri);
        assertEquals(expectedPort.intValue(), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://localhost.com.br:8080",
            "https://localhost.com.br",
            "http://localhost.com.br",
    })
    void changeToLocalhost(String input) throws URISyntaxException {
        URI uri = URI.create(input);
        var result = wrapper.changeToLocalhost(uri, 6666);
        assertEquals(6666, result.getPort());
        assertEquals(LOCALHOST, result.getHost());
    }
}
