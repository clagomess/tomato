package io.github.clagomess.tomato.io.http;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
public class SSLContextBuilder extends X509ExtendedTrustManager {
    private final HttpDebug debug;
    private final X509ExtendedTrustManager defaultTrustManager;

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

    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        var sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{this}, new SecureRandom());

        return sslContext;
    }
}
