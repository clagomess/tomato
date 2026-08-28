package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.ResponseDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SNIHostName;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static io.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static io.github.clagomess.tomato.enums.HttpMethodEnum.PUT;
import static io.github.clagomess.tomato.io.http.MediaType.APPLICATION_FORM_URLENCODED_TYPE;
import static io.github.clagomess.tomato.io.http.MediaType.HTTP_CONTENT_TYPE;

@Slf4j
@RequiredArgsConstructor
public class HttpService {
    private final RequestHeadDto requestHead;
    private final RequestDto requestDto;
    private final HttpDebug debug;

    public HttpService(
            @Nullable RequestHeadDto requestHead,
            RequestDto requestDto
    ) {
        this.requestHead = requestHead;
        this.requestDto = requestDto;
        this.debug = new HttpDebug();
    }

    public static File createTempFile() throws IOException {
        var file = File.createTempFile("tomato-http-", ".bin");
        file.deleteOnExit();
        return file;
    }

    private HttpClient getClient(
            @Nullable URI originalUri
    ) throws NoSuchAlgorithmException, KeyManagementException {
        var sslContext = new SSLContextBuilder(debug).build();

        var builder = HttpClient.newBuilder()
                .sslContext(sslContext)
                .executor(ForkJoinPool.commonPool());

        if(originalUri != null){
            var sslParameters = sslContext.getDefaultSSLParameters();
            sslParameters.setServerNames(List.of(new SNIHostName(originalUri.getHost())));
            builder.sslParameters(sslParameters);
        }

        return builder.build();
    }

    private static String hostHeader(URI uri){
        return uri.getPort() > 0
                ? uri.getHost() + ":" + uri.getPort()
                : uri.getHost();
    }

    public ResponseDto perform(){
        ResponseDto result = new ResponseDto(requestDto.getId());

        try {
            URI uri = new UrlBuilder(requestDto).buildUri();

            ResponseDto.Response resultHttp;

            if(requestDto.getConfig().getProxyId() != null){
                resultHttp = new SSHProxyWrapper().wrap(
                        requestDto.getConfig().getProxyId(),
                        uri,
                        this::perform
                );
            }else{
                resultHttp = perform(uri);
            }

            result.setRequestStatus(true);
            result.setHttpResponse(resultHttp);
        } catch (InterruptedException e){
            result.setRequestMessage("Request failed");
            log.error(e.getMessage(), e);
        } catch (ConnectException e){
            result.setRequestMessage("Connection refused");
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            result.setRequestMessage(e.getMessage());
            log.error(e.getMessage(), e);
        } finally {
            result.setRequestDebug(debug.assembly());
            result.setRequestCertIssue(debug.getCertIssue());
        }

        return result;
    }

    protected ResponseDto.Response perform(URI uri) throws Exception {
        return perform(uri, null);
    }

    protected ResponseDto.Response perform(
            URI uri,
            @Nullable URI originalUri
    ) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(uri);

        // set headers
        new HttpHeaderBuilder(requestBuilder, requestHead, requestDto).build();

        if(originalUri != null){
            requestBuilder.setHeader("Host", hostHeader(originalUri));
        }

        HttpRequest request = buildBody(requestBuilder);
        debug.setRequest(request);

        var responseFile = createTempFile();
        debug.setResponseBodyFile(responseFile);

        long requestTime = System.currentTimeMillis();

        HttpResponse<Path> response = getClient(originalUri).send(
                request,
                HttpResponse.BodyHandlers.ofFile(responseFile.toPath())
        );
        debug.setResponse(response);

        return new ResponseDto.Response(response, requestTime);
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
                requestDto.getMethod().name(),
                HttpRequest.BodyPublishers.noBody()
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyRaw(
            HttpRequest.Builder httpRequestBuilder
    ){
        if(requestDto.getHeaders().stream()
                .noneMatch(item ->
                        HTTP_CONTENT_TYPE.equalsIgnoreCase(item.getKey())
                )
        ) {
            httpRequestBuilder.header(
                    HTTP_CONTENT_TYPE,
                    requestDto.getBody()
                            .getRaw()
                            .getType()
                            .getContentType().toString()
            );
        }

        debug.setRequestBodyString(requestDto.getBody().getRaw().getRaw());

        httpRequestBuilder.method(
                requestDto.getMethod().name(),
                HttpRequest.BodyPublishers.ofString(
                        requestDto.getBody().getRaw().getRaw(),
                        requestDto.getBody().getCharset()
                )
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyBinary(
            HttpRequest.Builder httpRequestBuilder
    ) throws FileNotFoundException {
        httpRequestBuilder.header(
                HTTP_CONTENT_TYPE,
                requestDto.getBody()
                        .getBinary()
                        .getContentType()
        );

        debug.setRequestBodyFile(new File(requestDto.getBody().getBinary().getFile()));

        httpRequestBuilder.method(
                requestDto.getMethod().name(),
                HttpRequest.BodyPublishers.ofFile(
                        Path.of(requestDto.getBody().getBinary().getFile())
                )
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyUrlEncoded(
            HttpRequest.Builder httpRequestBuilder
    ) throws IOException {
        var form = new UrlEncodedFormBody(requestHead, requestDto.getBody());

        httpRequestBuilder.header(
                HTTP_CONTENT_TYPE,
                APPLICATION_FORM_URLENCODED_TYPE
        );

        String body = form.build();
        debug.setRequestBodyString(body);

        httpRequestBuilder.method(
                requestDto.getMethod().name(),
                HttpRequest.BodyPublishers.ofString(body)
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyMultipart(
            HttpRequest.Builder httpRequestBuilder
    ) throws IOException {
        var form = new MultipartFormDataBody(requestHead, requestDto.getBody());

        httpRequestBuilder.header(
                HTTP_CONTENT_TYPE,
                form.getContentType()
        );

        File body = form.build();
        debug.setRequestBodyFile(body);

        httpRequestBuilder.method(
                requestDto.getMethod().name(),
                HttpRequest.BodyPublishers.ofFile(body.toPath())
        );

        return httpRequestBuilder.build();
    }
}
