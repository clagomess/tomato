package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.ResponseDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
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
public class HttpService extends RequestBuilder {
    private final HttpDebug debug;
    private final RequestDto request;

    public HttpService(
            @Nullable RequestHeadDto requestHead,
            RequestDto request
    ) {
        super(requestHead);
        this.request = request;
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
        ResponseDto result = new ResponseDto(request.getId());

        try {
            URI uri = buildUri(request);
            ResponseDto.Response resultHttp;

            if(request.getConfig().getProxyId() != null){
                resultHttp = new SSHProxyWrapper().wrap(
                        request.getConfig().getProxyId(),
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
        new HttpHeaderBuilder(requestBuilder, requestHead, request).build();

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
        if(!List.of(PUT, POST).contains(request.getMethod())){
            return buildBodyEmpty(httpRequestBuilder);
        }

        return switch (request.getBody().getType()){
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
                request.getMethod().name(),
                HttpRequest.BodyPublishers.noBody()
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyRaw(
            HttpRequest.Builder httpRequestBuilder
    ){
        if(request.getHeaders().stream()
                .noneMatch(item ->
                        HTTP_CONTENT_TYPE.equalsIgnoreCase(item.getKey())
                )
        ) {
            httpRequestBuilder.header(
                    HTTP_CONTENT_TYPE,
                    request.getBody()
                            .getRaw()
                            .getType()
                            .getContentType().toString()
            );
        }

        debug.setRequestBodyString(request.getBody().getRaw().getRaw());

        httpRequestBuilder.method(
                request.getMethod().name(),
                HttpRequest.BodyPublishers.ofString(
                        request.getBody().getRaw().getRaw(),
                        request.getBody().getCharset()
                )
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyBinary(
            HttpRequest.Builder httpRequestBuilder
    ) throws FileNotFoundException {
        httpRequestBuilder.header(
                HTTP_CONTENT_TYPE,
                request.getBody()
                        .getBinary()
                        .getContentType()
        );

        var file = buildBodyBinary(request.getBody().getBinary().getFile());

        debug.setRequestBodyFile(file);

        httpRequestBuilder.method(
                request.getMethod().name(),
                HttpRequest.BodyPublishers.ofFile(file.toPath())
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyUrlEncoded(
            HttpRequest.Builder httpRequestBuilder
    ) throws IOException {
        var form = new UrlEncodedFormBody(requestHead, request.getBody());

        httpRequestBuilder.header(
                HTTP_CONTENT_TYPE,
                APPLICATION_FORM_URLENCODED_TYPE
        );

        String body = form.build();
        debug.setRequestBodyString(body);

        httpRequestBuilder.method(
                request.getMethod().name(),
                HttpRequest.BodyPublishers.ofString(body)
        );

        return httpRequestBuilder.build();
    }

    private HttpRequest buildBodyMultipart(
            HttpRequest.Builder httpRequestBuilder
    ) throws IOException {
        var form = new MultipartFormDataBody(requestHead, request.getBody());

        httpRequestBuilder.header(
                HTTP_CONTENT_TYPE,
                form.getContentType()
        );

        File body = form.build();
        debug.setRequestBodyFile(body);

        httpRequestBuilder.method(
                request.getMethod().name(),
                HttpRequest.BodyPublishers.ofFile(body.toPath())
        );

        return httpRequestBuilder.build();
    }
}
