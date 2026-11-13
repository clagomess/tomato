package io.github.clagomess.tomato.io.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.net.ssl.X509KeyManager;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.util.Objects;

class SSLContextBuilderTest {
    private static final String RSA = "RSA";
    private static final String PASSWORD = "tomato";
    private static final String ALIAS = "tomato-client";
    private static final String OTHER_ALIAS = "other-client";

    private static File getMtlsClientFile(String name) throws URISyntaxException {
        return new File(Objects.requireNonNull(SSLContextBuilderTest.class
                .getResource("/echo-server/mtls-client/" + name)).toURI());
    }

    private static X509KeyManager getKeyManager(SSLContextBuilder builder){
        Assertions.assertThat(builder.getKeyManagers()).hasSize(1);
        return (X509KeyManager) Objects.requireNonNull(builder.getKeyManagers())[0];
    }

    @Nested
    class withPkcs12 {
        @ParameterizedTest
        @ValueSource(strings = {"client.pfx", "client-legacy.pfx"})
        void whenValidPfx_loadClientCertificate(String pfx) throws Exception {
            var builder = new SSLContextBuilder(new HttpDebug())
                    .withPkcs12(getMtlsClientFile(pfx), PASSWORD, null);

            var keyManager = getKeyManager(builder);
            var alias = keyManager.chooseClientAlias(new String[]{RSA}, null, null);

            Assertions.assertThat(alias).isIn(ALIAS, OTHER_ALIAS);
            Assertions.assertThat(keyManager.getPrivateKey(alias)).isNotNull();
            Assertions.assertThat(keyManager.getCertificateChain(alias)).isNotEmpty();
        }

        @Test
        void whenAliasNotInformed_keepAllAliases() throws Exception {
            var builder = new SSLContextBuilder(new HttpDebug())
                    .withPkcs12(getMtlsClientFile("client.pfx"), PASSWORD, null);

            Assertions.assertThat(getKeyManager(builder).getClientAliases(RSA, null))
                    .containsExactlyInAnyOrder(ALIAS, OTHER_ALIAS);
        }

        @ParameterizedTest
        @CsvSource({
                ALIAS + ",CN=Tomato Client",
                OTHER_ALIAS + ",CN=Other Client",
        })
        void whenAliasInformed_keepOnlyInformedAlias(String alias, String subject) throws Exception {
            var builder = new SSLContextBuilder(new HttpDebug())
                    .withPkcs12(getMtlsClientFile("client.pfx"), PASSWORD, alias);

            var keyManager = getKeyManager(builder);

            Assertions.assertThat(keyManager.getClientAliases(RSA, null))
                    .containsExactly(alias);
            Assertions.assertThat(keyManager.chooseClientAlias(new String[]{RSA}, null, null))
                    .isEqualTo(alias);
            Assertions.assertThat(keyManager.getCertificateChain(alias)[0]
                    .getSubjectX500Principal().getName())
                    .contains(subject);
        }

        @Test
        void whenAliasNotFound_throwKeyStoreException() {
            var builder = new SSLContextBuilder(new HttpDebug());

            Assertions.assertThatThrownBy(() -> builder.withPkcs12(
                    getMtlsClientFile("client.pfx"),
                    PASSWORD,
                    "unknown"
            ))
                    .isInstanceOf(KeyStoreException.class)
                    .hasMessageContaining("unknown")
                    .hasMessageContaining(ALIAS);
        }

        @Test
        void whenWrongPassword_throwIOException() {
            var builder = new SSLContextBuilder(new HttpDebug());

            Assertions.assertThatThrownBy(() -> builder.withPkcs12(
                    getMtlsClientFile("client.pfx"),
                    "wrong",
                    null
            )).isInstanceOf(IOException.class);
        }

        @Test
        void whenFileNotFound_throwIOException() {
            var builder = new SSLContextBuilder(new HttpDebug());

            Assertions.assertThatThrownBy(() -> builder.withPkcs12(
                    new File("target/not-found.pfx"),
                    PASSWORD,
                    null
            )).isInstanceOf(IOException.class);
        }
    }

    @Nested
    class build {
        @Test
        void whenWithoutClientCertificate_createContext() throws Exception {
            var builder = new SSLContextBuilder(new HttpDebug());

            Assertions.assertThat(builder.build()).isNotNull();
            Assertions.assertThat(builder.getKeyManagers()).isNull();
        }

        @Test
        void whenWithPkcs12_createContext() throws Exception {
            var sslContext = new SSLContextBuilder(new HttpDebug())
                    .withPkcs12(getMtlsClientFile("client.pfx"), PASSWORD, null)
                    .build();

            Assertions.assertThat(sslContext).isNotNull();
        }
    }
}
