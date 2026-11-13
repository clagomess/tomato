package io.github.clagomess.tomato.io.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

@Slf4j
public class SSLContextBuilder extends X509ExtendedTrustManager {
    private final HttpDebug debug;
    private final X509ExtendedTrustManager defaultTrustManager;
    private KeyManager @Nullable [] keyManagers;

    public SSLContextBuilder(HttpDebug debug) {
        X509ExtendedTrustManager defaultTrustManager = null;

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);

            TrustManager[] defaultTrustManagers = trustManagerFactory.getTrustManagers();
            defaultTrustManager = (X509ExtendedTrustManager) defaultTrustManagers[0];
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        this.debug = debug;
        this.defaultTrustManager = defaultTrustManager;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {
        runCertificateCheck(() -> defaultTrustManager.checkClientTrusted(chain, authType, socket));
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {
        runCertificateCheck(() -> defaultTrustManager.checkServerTrusted(chain, authType, socket));
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
        runCertificateCheck(() -> defaultTrustManager.checkClientTrusted(chain, authType, engine));
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
        runCertificateCheck(() -> defaultTrustManager.checkServerTrusted(chain, authType, engine));
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        runCertificateCheck(() -> defaultTrustManager.checkClientTrusted(chain, authType));
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        runCertificateCheck(() -> defaultTrustManager.checkServerTrusted(chain, authType));
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return defaultTrustManager != null ?
                defaultTrustManager.getAcceptedIssuers() :
                new X509Certificate[0];
    }

    private void runCertificateCheck(CertificateCheckRunnable runnable) {
        try {
            runnable.run();
        } catch (CertificateException e) {
            debug.setCertIssue(e.getMessage());
            log.warn(e.getMessage(), e);
        }
    }

    @FunctionalInterface
    private interface CertificateCheckRunnable {
        void run() throws CertificateException;
    }

    public SSLContextBuilder withPkcs12(
            File file,
            @Nullable String password,
            @Nullable String alias
    ) throws IOException, GeneralSecurityException {
        var passwordChars = password != null ? password.toCharArray() : new char[0];

        var keyStore = KeyStore.getInstance("PKCS12");
        try (var is = new FileInputStream(file)) {
            keyStore.load(is, passwordChars);
        }

        if (StringUtils.isNotBlank(alias)) {
            if (!keyStore.isKeyEntry(alias)) {
                throw new KeyStoreException(String.format(
                        "Alias \"%s\" not found in %s. Available: %s",
                        alias,
                        file.getName(),
                        Collections.list(keyStore.aliases())
                ));
            }

            // only in memory, the file is untouched
            for (var item : Collections.list(keyStore.aliases())) {
                if (!item.equals(alias)) keyStore.deleteEntry(item);
            }
        }

        var keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, passwordChars);
        this.keyManagers = keyManagerFactory.getKeyManagers();

        return this;
    }

    KeyManager @Nullable [] getKeyManagers() {
        return keyManagers;
    }

    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        var sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, new TrustManager[]{this}, new SecureRandom());

        return sslContext;
    }

}
