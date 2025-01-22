package com.github.clagomess.tomato.io.http;

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
    private final X509ExtendedTrustManager defaultTrustManager;

    public SSLContextBuilder() {
        X509ExtendedTrustManager defaultTrustManager = null;

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);

            TrustManager[] defaultTrustManagers = trustManagerFactory.getTrustManagers();
            defaultTrustManager = (X509ExtendedTrustManager) defaultTrustManagers[0];
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }

        this.defaultTrustManager = defaultTrustManager;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {
        try {
            defaultTrustManager.checkClientTrusted(chain, authType, socket);
        } catch (CertificateException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {
        try {
            defaultTrustManager.checkServerTrusted(chain, authType, socket);
        } catch (CertificateException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
        try {
            defaultTrustManager.checkClientTrusted(chain, authType, engine);
        }catch (CertificateException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
        try {
            defaultTrustManager.checkServerTrusted(chain, authType, engine);
        } catch (CertificateException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        try {
            defaultTrustManager.checkClientTrusted(chain, authType);
        } catch (CertificateException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        try {
            defaultTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return defaultTrustManager != null ?
                defaultTrustManager.getAcceptedIssuers() :
                new X509Certificate[0];
    }

    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        var sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{this}, new SecureRandom());

        return sslContext;
    }
}
