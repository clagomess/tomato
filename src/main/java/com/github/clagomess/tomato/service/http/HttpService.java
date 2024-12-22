package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.HttpStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static com.github.clagomess.tomato.enums.HttpMethodEnum.PUT;

@Slf4j
public class HttpService {
    private final RequestDto requestDto;
    private final HttpDebug debug = new HttpDebug();

    public HttpService(RequestDto requestDto) {
        this.requestDto = requestDto;
    }

    private HttpClient getClient() {
        // @TODO: check ssl
        // @TODO: FOLLOW_REDIRECTS
        // @TODO: TIMEOUT
        return HttpClient.newHttpClient();
    }

    public ResponseDto perform(){
        ResponseDto result = new ResponseDto(requestDto.getId());

        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(requestDto.getUrl()));

            // set headers
            requestBuilder.setHeader("User-Agent", "Tomato/0.0.1"); //@TODO: get from project
            requestDto.getHeaders().stream()
                    .filter(RequestDto.KeyValueItem::isSelected)
                    .forEach(header -> requestBuilder.setHeader(
                            header.getKey(),
                            header.getValue()
                    ));

            // set cookies
            requestDto.getCookies().stream()
                    .filter(RequestDto.KeyValueItem::isSelected)
                    .forEach(cookie -> requestBuilder.header(
                            "Cookie",
                            cookie.getKey() + "=" + cookie.getValue()
                    ));

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

            var resultHttp = new ResponseDto.Response();
            resultHttp.setRequestTime(System.currentTimeMillis() - requestTime);

            resultHttp.setBody(reponseFile);
            resultHttp.setBodySize(reponseFile.length());
            resultHttp.setStatus(response.statusCode());
            resultHttp.setStatusReason(HttpStatusEnum.getReasonPhrase(response.statusCode()));
            resultHttp.setHeaders(response.headers().map());
//            resultHttp.setCookies(response.getCookies()); //@TODO: impl. parse cookies?

            Optional<String> contentType = response.headers().firstValue("content-type");
            if (contentType.isPresent()) {
                resultHttp.setContentType(new MediaType(contentType.get()));
            } else {
                resultHttp.setContentType(MediaType.WILDCARD);
            }

            result.setRequestStatus(true);
            result.setHttpResponse(resultHttp);
        } catch (ConnectException e){
            result.setRequestMessage("Connection refused");
            log.error(log.getName(), e);
        } catch (Throwable e) {
            result.setRequestMessage(e.getMessage());
            log.error(log.getName(), e);
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
    ){
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
