package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.mapper.RequestMapper;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;

import static com.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static com.github.clagomess.tomato.enums.HttpMethodEnum.PUT;

@Slf4j
public class HttpService {
    @Getter
    private static final HttpService instance = new HttpService();
    private HttpService() {}

    private final RequestMapper mapper = RequestMapper.INSTANCE; //@TODO: check possible unused methods
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
            dto.getHeaders().forEach(header -> requestBuilder.header(
                    header.getKey(),
                    header.getValue()
            ));

            // set cookies
//            dto.getCookies().forEach(item -> invocationBuilder.cookie(item.getKey(), item.getValue())); //@TODO: impl. send cookie

            HttpRequest request = buildBody(requestBuilder, dto);
            httpLogCollectorUtil.flush();

            long requestTime = System.currentTimeMillis();

            HttpResponse<byte[]> response = getClient().send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray()
            );

            var resultHttp = new ResponseDto.Response();
            resultHttp.setRequestTime(System.currentTimeMillis() - requestTime);

            resultHttp.setBody(response.body());
            resultHttp.setBodySize(resultHttp.getBody().length);
            resultHttp.setStatus(response.statusCode());
            resultHttp.setStatusReason("FOO");
            resultHttp.setHeaders(response.headers().map());
//            resultHttp.setCookies(response.getCookies()); //@TODO: impl. parse cookies?
            resultHttp.setContentType(MediaType.TEXT_PLAIN_TYPE); //@TODO: impl. response content-type

            result.setRequestStatus(true);
            result.setHttpResponse(resultHttp);
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
        throw new RuntimeException("Not implemented yet"); //@TODO: impl URL_ENCODED
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
