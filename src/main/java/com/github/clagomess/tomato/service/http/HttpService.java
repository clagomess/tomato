package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.HttpStatusEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static com.github.clagomess.tomato.enums.HttpMethodEnum.PUT;

@Slf4j
public class HttpService {
    @Getter
    private static final HttpService instance = new HttpService();
    private HttpService() {}

    private final HttpLogCollectorUtil httpLogCollectorUtil = new HttpLogCollectorUtil();

    private HttpClient getClient() {
        // @TODO: check ssl
        // @TODO: FOLLOW_REDIRECTS
        // @TODO: TIMEOUT
        return HttpClient.newHttpClient();
    }

    public ResponseDto perform(RequestDto dto){
        ResponseDto result = new ResponseDto(dto.getId());

        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(dto.getUrl()));

            // set headers
            requestBuilder.setHeader("User-Agent", "Tomato/0.0.1"); //@TODO: get from project
            dto.getHeaders().stream()
                    .filter(RequestDto.KeyValueItem::isSelected)
                    .forEach(header -> requestBuilder.setHeader(
                            header.getKey(),
                            header.getValue()
                    ));

            // set cookies
            dto.getCookies().stream()
                    .filter(RequestDto.KeyValueItem::isSelected)
                    .forEach(cookie -> requestBuilder.header(
                            "Cookie",
                            cookie.getKey() + "=" + cookie.getValue()
                    ));

            HttpRequest request = buildBody(requestBuilder, dto);
            httpLogCollectorUtil.flush();

            var reponseFile = File.createTempFile("tomato-reponse-", ".bin");
            reponseFile.deleteOnExit();

            long requestTime = System.currentTimeMillis();

            HttpResponse<Path> response = getClient().send(
                    request,
                    HttpResponse.BodyHandlers.ofFile(reponseFile.toPath())
            );

            var resultHttp = new ResponseDto.Response();
            resultHttp.setRequestTime(System.currentTimeMillis() - requestTime);

            resultHttp.setBody(reponseFile);
            resultHttp.setBodySize(Files.size(reponseFile.toPath()));
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
            result.setRequestDebug(httpLogCollectorUtil.getLogText().toString());
        }

        return result;
    }

    private HttpRequest buildBody(
            HttpRequest.Builder httpRequestBuilder,
            RequestDto dto
    ) throws IOException {
        if(!List.of(PUT, POST).contains(dto.getMethod())){
            return buildBodyEmpty(httpRequestBuilder, dto);
        }

        return switch (dto.getBody().getType()){
            case RAW -> buildBodyRaw(httpRequestBuilder, dto);
            case BINARY -> buildBodyBinary(httpRequestBuilder, dto);
            case URL_ENCODED_FORM -> buildBodyUrlEncoded(httpRequestBuilder, dto);
            case MULTIPART_FORM -> buildBodyMultipart(httpRequestBuilder, dto);
            default -> buildBodyEmpty(httpRequestBuilder, dto);
        };
    }

    private HttpRequest buildBodyEmpty(
            HttpRequest.Builder httpRequestBuilder,
            RequestDto dto
    ){
        httpRequestBuilder.method(
                dto.getMethod().getMethod(),
                HttpRequest.BodyPublishers.noBody()
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyRaw(
            HttpRequest.Builder httpRequestBuilder,
            RequestDto dto
    ){
        httpRequestBuilder.header(
                "Content-Type",
                dto.getBody()
                        .getRaw()
                        .getType()
                        .getContentType().toString()
        );

        httpRequestBuilder.method(
                dto.getMethod().getMethod(),
                HttpRequest.BodyPublishers.ofString(
                        dto.getBody().getRaw().getRaw()
                )
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyBinary(
            HttpRequest.Builder httpRequestBuilder,
            RequestDto dto
    ) throws FileNotFoundException {
        httpRequestBuilder.header(
                "Content-Type",
                dto.getBody()
                        .getBinary()
                        .getContentType()
        );

        httpRequestBuilder.method(
                dto.getMethod().getMethod(),
                HttpRequest.BodyPublishers.ofFile(
                        Path.of(dto.getBody().getBinary().getFile())
                )
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyUrlEncoded(
            HttpRequest.Builder httpRequestBuilder,
            RequestDto dto
    ){
        var form = new UrlEncodedFormBody(dto.getBody().getUrlEncodedForm());

        httpRequestBuilder.header(
                "Content-Type",
                form.getContentType()
        );

        httpRequestBuilder.method(
                dto.getMethod().getMethod(),
                form.getBodyPublisher()
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyMultipart(
            HttpRequest.Builder httpRequestBuilder,
            RequestDto dto
    ) throws IOException {
        var form = new MultipartFormDataBody(dto.getBody().getMultiPartForm());

        httpRequestBuilder.header(
                "Content-Type",
                form.getContentType()
        );

        httpRequestBuilder.method(
                dto.getMethod().getMethod(),
                form.getBodyPublisher()
        );

        return httpRequestBuilder.build();
    }
}
