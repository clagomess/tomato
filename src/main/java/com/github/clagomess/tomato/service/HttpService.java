package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.util.HttpLogCollectorUtil;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContexts;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.net.ssl.SSLContext;
import java.util.logging.Level;

@Slf4j
public class HttpService {
    private static final HttpService instance = new HttpService();
    public synchronized static HttpService getInstance(){
        return instance;
    }

    private final Client client;
    private final HttpLogCollectorUtil httpLogCollectorUtil = new HttpLogCollectorUtil();
    private HttpService() {
        ClientConfig config = new ClientConfig();
        config.property(ClientProperties.FOLLOW_REDIRECTS, true);
        config.property(ClientProperties.CONNECT_TIMEOUT, 1000 * 10);
        config.register(MultiPartFeature.class);
        config.register(new LoggingFeature(
                httpLogCollectorUtil,
                Level.INFO,
                LoggingFeature.Verbosity.PAYLOAD_TEXT,
                600
        ));

//        if(restParam.getProxy() != null){ //@TODO: impl. use of proxy
//            config.connectorProvider(new ApacheConnectorProvider());
//            config.property(ClientProperties.PROXY_URI, restParam.getProxy().getUri());
//            config.property(ClientProperties.PROXY_USERNAME, restParam.getProxy().getUsername());
//            config.property(ClientProperties.PROXY_PASSWORD, restParam.getProxy().getPassword());
//        }

        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.withConfig(config);
        clientBuilder.sslContext(getSslContext());

        client = clientBuilder.build();
    }

    private static SSLContext getSslContext() {
        try {
            return SSLContexts.custom().loadTrustMaterial(
                    null,
                    (TrustStrategy) (x509Certificates, authType) -> true
            ).build();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static Entity<?> buildEntity(RequestDto dto){
        return switch (dto.getBody().getType()) {
            case URL_ENCODED_FORM -> Entity.form(
                    dto.getBody().toUrlEncodedForm()
            );
            case MULTIPART_FORM -> Entity.entity(
                    dto.getBody().toMultiPartForm(),
                    MediaType.MULTIPART_FORM_DATA_TYPE
            );
            case RAW -> Entity.entity(
                    dto.getBody().getRaw().getRaw(),
                    dto.getBody().getRaw().getType().getContentType()
            );
            case BINARY -> Entity.entity(
                    dto.getBody().getBinary().getFile(),
                    dto.getBody().getBinary().getContentType()
            );
            default -> Entity.text(null);
        };
    }

    public ResponseDto perform(RequestDto dto){
        ResponseDto result = new ResponseDto(dto.getId());

        try {
            Invocation.Builder invocationBuilder = client.target(dto.getUrl()).request();

            // set headers
            invocationBuilder.headers(dto.toMultivaluedMapHeaders());

            // set cookies
            dto.getCookies().forEach(item -> invocationBuilder.cookie(item.getKey(), item.getValue()));

            httpLogCollectorUtil.flush();
            long requestTime = System.currentTimeMillis();

            try(Response response = performRequest(invocationBuilder, dto)){
                var resultHttp = new ResponseDto.Response();
                resultHttp.setRequestTime(System.currentTimeMillis() - requestTime);

                resultHttp.setBody(response.readEntity(byte[].class));
                resultHttp.setBodySize(resultHttp.getBody().length);
                resultHttp.setStatus(response.getStatus());
                resultHttp.setStatusReason(response.getStatusInfo().getReasonPhrase());
                resultHttp.setHeaders(response.getStringHeaders());
                resultHttp.setCookies(response.getCookies());
                resultHttp.setContentType(response.getMediaType());

                result.setRequestStatus(true);
                result.setHttpResponse(resultHttp);
            }
        } catch (Throwable e) {
            result.setRequestMessage(e.getMessage());
            log.error(log.getName(), e);
        } finally {
            result.setRequestDebug(httpLogCollectorUtil.getLogText().toString());
        }

        return result;
    }

    private Response performRequest(Invocation.Builder invocationBuilder, RequestDto dto){
        return switch (dto.getMethod()) {
            case POST -> invocationBuilder.post(buildEntity(dto));
            case PUT -> invocationBuilder.put(buildEntity(dto));
            case DELETE -> invocationBuilder.delete();
            default -> invocationBuilder.get();
        };
    }
}
