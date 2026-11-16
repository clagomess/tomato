package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.request.AuthDto;
import io.github.clagomess.tomato.dto.data.request.auth.MTLSAuthDto;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import javax.net.ssl.X509KeyManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

class HttpServiceTest {
    private static final String ALIAS = "tomato-client";
    private static final String FILE_NOT_FOUND_MESSAGE = "mTLS: certificate file not informed/found";

    @BeforeAll
    static void setup() throws URISyntaxException {
        var dir = new File(Objects.requireNonNull(HttpServiceTest.class
                .getResource("/echo-server/mtls-client")).toURI());

        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .addListener(query -> List.of(
                        new EnvironmentItemDto("dir", dir.getAbsolutePath()),
                        new EnvironmentItemDto("pass", "tomato"),
                        new EnvironmentItemDto("alias", ALIAS)
                ));
    }

    @AfterAll
    static void unload(){
        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .getListeners()
                .remove();
    }

    @Nested
    class buildSSLContext {
        private static AuthDto mtlsAuth(String file){
            var auth = new AuthDto();
            auth.setType(AuthDto.Type.MTLS);
            auth.getMtls().setPkcs12(new MTLSAuthDto.Pkcs12Dto(file, "{{pass}}", "{{alias}}"));

            return auth;
        }

        private static HttpService httpService(){
            return new HttpService(null, new RequestDto());
        }

        @Test
        void whenNotMtls_withoutClientCertificate() throws Exception {
            var result = httpService()
                    .buildSSLContext(new AuthDto(), new HttpDebug());

            Assertions.assertThat(result.getKeyManagers()).isNull();
        }

        @Test
        void whenPkcs12_injectEnvironment() throws Exception {
            var result = httpService()
                    .buildSSLContext(mtlsAuth("{{dir}}/client.pfx"), new HttpDebug());

            var keyManager = (X509KeyManager) Objects.requireNonNull(result.getKeyManagers())[0];
            Assertions.assertThat(keyManager.getClientAliases("RSA", null))
                    .containsExactly(ALIAS);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void whenBlankFile_throwFileNotFoundException(String file) {
            var service = httpService();
            var auth = mtlsAuth(file);

            Assertions.assertThatThrownBy(() -> service.buildSSLContext(auth, new HttpDebug()))
                    .isInstanceOf(FileNotFoundException.class)
                    .hasMessage(FILE_NOT_FOUND_MESSAGE);
        }

        @Test
        void whenFileNotExists_throwFileNotFoundException() {
            var service = httpService();
            var auth = mtlsAuth("{{dir}}/not-found.pfx");

            Assertions.assertThatThrownBy(() -> service.buildSSLContext(auth, new HttpDebug()))
                    .isInstanceOf(FileNotFoundException.class)
                    .hasMessage(FILE_NOT_FOUND_MESSAGE);
        }
    }
}
