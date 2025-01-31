package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.enums.HttpStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Setter
public class HttpDebug {
    private HttpRequest request;
    private String requestBodyString;
    private File requestBodyFile;

    @Getter
    private String certIssue;
    private HttpResponse<Path> response;
    private File responseBodyFile;

    private final int defaultLimitBody = 300;

    public String assembly(){
        if(request == null) return null;

        StringBuilder result = new StringBuilder();
        result.append("> ");
        result.append(request.method());
        result.append(" ");
        result.append(request.uri());
        result.append("\n");
        result.append(assemblyHeader("> ", request.headers().map()));
        result.append("\n");

        if(requestBodyString != null){
            result.append(assemblyBody(requestBodyString, defaultLimitBody)).append("\n");
        }

        if(requestBodyFile != null){
            result.append(assemblyBody(requestBodyFile, defaultLimitBody)).append("\n");
        }

        if(response == null) return result.toString();

        result.append("-".repeat(40)).append("\n");

        if(response.sslSession().isPresent()){
            result.append(assemblyCertificates(response.sslSession().get()));
            result.append("-".repeat(40)).append("\n");
        }

        result.append("< ");
        result.append(response.version());
        result.append(" ");
        result.append(response.statusCode());
        result.append(" ");
        result.append(HttpStatusEnum.getReasonPhrase(response.statusCode()));
        result.append("\n");
        result.append(assemblyHeader("< ", response.headers().map()));
        result.append("\n");

        if(responseBodyFile != null){
            result.append(assemblyBody(responseBodyFile, defaultLimitBody)).append("\n");
            result.append("-".repeat(40)).append("\n");
            result.append("Written response in:\n");
            result.append(responseBodyFile.getAbsolutePath());
        }

        return result.toString();
    }

    protected String assemblyCN(String dn) {
        try {
            return new LdapName(dn).getRdns().stream()
                    .filter(item -> "CN".equals(item.getType()))
                    .map(item -> String.valueOf(item.getValue()))
                    .findFirst()
                    .orElse(dn);
        }catch (InvalidNameException e){
            log.error(e.getMessage(), e);
        }

        return dn;
    }

    protected StringBuilder assemblyCertificates(SSLSession session){
        StringBuilder result = new StringBuilder();

        result.append("# CERTIFICATE CHAIN - ");
        result.append(session.getProtocol()).append(" - ");
        result.append(session.getCipherSuite()).append("\n");

        if(certIssue != null){
            result.append("Cert Issue: ");
            result.append(certIssue);
            result.append("\n");
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Arrays.stream(session.getPeerCertificates())
                    .filter(X509Certificate.class::isInstance)
                    .map(X509Certificate.class::cast)
                    .forEach(certificate -> {
                        var subject = certificate.getSubjectX500Principal();
                        var issuer = certificate.getIssuerX500Principal();

                        result.append("Subject: ");
                        result.append(assemblyCN(subject.getName()));
                        result.append("\n");
                        result.append("Valid from: ");
                        result.append(formatter.format(certificate.getNotBefore()));
                        result.append(" - ");
                        result.append(formatter.format(certificate.getNotAfter()));
                        result.append("\n");

                        if(!issuer.equals(subject)) {
                            result.append("Issuer: ");
                            result.append(assemblyCN(issuer.getName()));
                            result.append("\n\n");
                        }
                    });
        }catch (Throwable e){
            log.warn(e.getMessage(), e);
        }

        return result;
    }

    protected StringBuilder assemblyHeader(
            String arrow,
            Map<String, List<String>> headers
    ){
        StringBuilder result = new StringBuilder();

        headers.forEach((key, value) -> {
            value.forEach(item -> {
                result.append(arrow);
                result.append(key);
                result.append(": ");
                result.append(item);
                result.append("\n");
            });
        });

        return result;
    }

    protected String assemblyBody(String body, int limit){
        long size = body.length();

        if(size > limit){
            return body.substring(0, limit) + String.format(
                    "\n[more %s bytes]",
                    size - limit
            );
        }else{
            return body;
        }
    }

    protected String assemblyBody(File body, int limit){
        long fileSize = body.length();
        if(fileSize == 0) return "";

        StringBuilder result = new StringBuilder();
        Charset charset = new MediaType(response.headers()).getCharsetOrDefault();

        try (BufferedReader br = new BufferedReader(new FileReader(
                body,
                charset
        ))){
            char[] buffer = new char[limit];
            int n = br.read(buffer);
            result.append(buffer, 0, n);

            if(fileSize > limit){
                result.append(String.format(
                        "\n[more %s bytes]",
                        fileSize - limit
                ));
            }

            return result.toString();
        }catch (IOException e){
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
}
