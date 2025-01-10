package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class HttpService {
    private final RequestDto requestDto;
    private final HttpDebug debug;
    private final EnvironmentRepository environmentRepository;

    public HttpService(RequestDto requestDto) {
        this(
                requestDto,
                new HttpDebug(),
                new EnvironmentRepository()
        );
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
                    .uri(buildUri(requestDto.getUrl()));

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

    protected URI buildUri(String url) throws IOException {
        Optional<EnvironmentDto> current = environmentRepository.getWorkspaceSessionEnvironment();

        if(current.isPresent()) {
            for(var env : current.get().getEnvs()) {
                url = url.replace(
                        String.format("{{%s}}", env.getKey()),
                        env.getValue()
                );
            }
        }

        return URI.create(url);
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
