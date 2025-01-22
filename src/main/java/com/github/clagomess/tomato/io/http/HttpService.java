package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static com.github.clagomess.tomato.enums.HttpMethodEnum.PUT;

@Slf4j
@RequiredArgsConstructor
public class HttpService {
    private final RequestDto requestDto;
    private final HttpDebug debug;

    public HttpService(RequestDto requestDto) {
        this.requestDto = requestDto;
        this.debug = new HttpDebug();
    }

    private static HttpClient client;
    private HttpClient getClient() throws NoSuchAlgorithmException, KeyManagementException {
        if(client != null) return client;

        client = HttpClient.newBuilder()
                .sslContext(new SSLContextBuilder().build())
                .build();

        return client;
    }

    public ResponseDto perform(){
        ResponseDto result = new ResponseDto(requestDto.getId());

        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new UrlBuilder(requestDto).buildUri());

            // set headers
            new HttpHeaderBuilder(requestBuilder, requestDto).build();

            HttpRequest request = buildBody(requestBuilder);
            debug.setRequest(request);

            var reponseFile = File.createTempFile("tomato-reponse-", ".bin");
            reponseFile.deleteOnExit();
            debug.setResponseBodyFile(reponseFile);

            long requestTime = System.currentTimeMillis();

            HttpResponse<Path> response = getClient().send(
                    request,
                    HttpResponse.BodyHandlers.ofFile(reponseFile.toPath())
            );
            debug.setResponse(response);

            var resultHttp = new ResponseDto.Response(response, requestTime);

            result.setRequestStatus(true);
            result.setHttpResponse(resultHttp);
        } catch (InterruptedException e){
            result.setRequestMessage("Request failed");
            log.error(e.getMessage(), e);
        } catch (ConnectException e){
            result.setRequestMessage("Connection refused");
            log.error(e.getMessage(), e);
        } catch (Throwable e) {
            result.setRequestMessage(e.getMessage());
            log.error(e.getMessage(), e);
        } finally {
            result.setRequestDebug(debug.assembly());
        }

        return result;
    }

    private HttpRequest buildBody(
            HttpRequest.Builder httpRequestBuilder
    ) throws IOException {
        if(!List.of(PUT, POST).contains(requestDto.getMethod())){
            return buildBodyEmpty(httpRequestBuilder);
        }

        return switch (requestDto.getBody().getType()){
            case RAW -> buildBodyRaw(httpRequestBuilder);
            case BINARY -> buildBodyBinary(httpRequestBuilder);
            case URL_ENCODED_FORM -> buildBodyUrlEncoded(httpRequestBuilder);
            case MULTIPART_FORM -> buildBodyMultipart(httpRequestBuilder);
            default -> buildBodyEmpty(httpRequestBuilder);
        };
    }

    private HttpRequest buildBodyEmpty(
            HttpRequest.Builder httpRequestBuilder
    ){
        httpRequestBuilder.method(
                requestDto.getMethod().getMethod(),
                HttpRequest.BodyPublishers.noBody()
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyRaw(
            HttpRequest.Builder httpRequestBuilder
    ){
        httpRequestBuilder.header(
                "Content-Type",
                requestDto.getBody()
                        .getRaw()
                        .getType()
                        .getContentType().toString()
        );

        debug.setRequestBodyString(requestDto.getBody().getRaw().getRaw());

        httpRequestBuilder.method(
                requestDto.getMethod().getMethod(),
                HttpRequest.BodyPublishers.ofString(
                        requestDto.getBody().getRaw().getRaw()
                )
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyBinary(
            HttpRequest.Builder httpRequestBuilder
    ) throws FileNotFoundException {
        httpRequestBuilder.header(
                "Content-Type",
                requestDto.getBody()
                        .getBinary()
                        .getContentType()
        );

        debug.setRequestBodyFile(new File(requestDto.getBody().getBinary().getFile()));

        httpRequestBuilder.method(
                requestDto.getMethod().getMethod(),
                HttpRequest.BodyPublishers.ofFile(
                        Path.of(requestDto.getBody().getBinary().getFile())
                )
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyUrlEncoded(
            HttpRequest.Builder httpRequestBuilder
    ) throws IOException {
        var form = new UrlEncodedFormBody(requestDto.getBody().getUrlEncodedForm());

        httpRequestBuilder.header(
                "Content-Type",
                form.getContentType()
        );

        String body = form.build();
        debug.setRequestBodyString(body);

        httpRequestBuilder.method(
                requestDto.getMethod().getMethod(),
                HttpRequest.BodyPublishers.ofString(body)
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyMultipart(
            HttpRequest.Builder httpRequestBuilder
    ) throws IOException {
        var form = new MultipartFormDataBody(requestDto.getBody().getMultiPartForm());

        httpRequestBuilder.header(
                "Content-Type",
                form.getContentType()
        );

        File body = form.build();
        debug.setRequestBodyFile(body);

        httpRequestBuilder.method(
                requestDto.getMethod().getMethod(),
                HttpRequest.BodyPublishers.ofFile(body.toPath())
        );

        return httpRequestBuilder.build();
    }
}
