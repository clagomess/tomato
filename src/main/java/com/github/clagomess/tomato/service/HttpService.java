package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.util.LoggerHandlerUtil;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContexts;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpService {
    private final LoggerHandlerUtil loggerHandler = new LoggerHandlerUtil();

    private ClientConfig getClientConfig(){
        ClientConfig config = new ClientConfig();
        config.property(ClientProperties.FOLLOW_REDIRECTS, true);
        config.property(ClientProperties.CONNECT_TIMEOUT, 1000 * 10);
        config.register(MultiPartFeature.class);
        config.register(getLogging());

//        if(restParam.getProxy() != null){ //@TODO: impl. use of proxy
//            config.connectorProvider(new ApacheConnectorProvider());
//            config.property(ClientProperties.PROXY_URI, restParam.getProxy().getUri());
//            config.property(ClientProperties.PROXY_USERNAME, restParam.getProxy().getUsername());
//            config.property(ClientProperties.PROXY_PASSWORD, restParam.getProxy().getPassword());
//        }

        return config;
    }

    private LoggingFeature getLogging(){
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.addHandler(loggerHandler);
        return new LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_TEXT, null);
    }

    private SSLContext getSslContext() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        return SSLContexts.custom().loadTrustMaterial(null, (TrustStrategy) (x509Certificates, authType) -> true).build();
    }

    private Client getClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.withConfig(getClientConfig());
        clientBuilder.sslContext(getSslContext());
        return clientBuilder.build();
    }

    private static Entity<?> buildEntity(RequestDto dto){
        switch (dto.getBody().getBodyType()){
            case URL_ENCODED_FORM:
                return Entity.form(dto.getBody().toUrlEncodedForm());
            case MULTIPART_FORM:
                return Entity.entity(dto.getBody().toMultiPartForm(), MediaType.TEXT_PLAIN_TYPE);
            case RAW:
                return null; //Entity.entity(dto.getBody().getRaw(), dto.getBody().getRawContentType()); //@TODO: must be checked
            case BINARY:
                return null; //Entity.entity(new File(dto.getBody().getBinaryFilePath()), dto.getBody().getBinaryContentType()); //@TODO: must be checked
            default:
                return Entity.text(null);
        }
    }

    public ResponseDto perform(RequestDto dto){
        ResponseDto result = new ResponseDto(dto.getId());

        try {
            WebTarget webTarget = getClient().target(dto.getUrl());
            Invocation.Builder invocationBuilder = webTarget.request();

            // set headers
            // invocationBuilder.headers(restParam.getHeader()); //@TODO: impl use of header

            // set cookies
            // invocationBuilder.cookie(item.getKey(), item.getValue()); //@TODO: impl use of cookies

            Response response;
            long requestTime = System.currentTimeMillis();

            switch (dto.getMethod()){
                case POST:
                    response = invocationBuilder.post(buildEntity(dto));
                    break;
                case PUT:
                    response = invocationBuilder.put(buildEntity(dto));
                    break;
                case DELETE:
                    response = invocationBuilder.delete();
                    break;
                case GET:
                default:
                    response = invocationBuilder.get();
                    break;
            }
            requestTime = System.currentTimeMillis() - requestTime;

            String responseContent = response.readEntity(String.class);

            result.setRequestStatus(true);
            result.setRequestDebug(loggerHandler.getLogText().toString());
            result.setHttpResponse(new ResponseDto.Response());
            result.getHttpResponse().setRequestTime(requestTime);
            result.getHttpResponse().setBodySize(responseContent.length());
            result.getHttpResponse().setStatus(response.getStatus());
            result.getHttpResponse().setStatusReason(response.getStatusInfo().getReasonPhrase());
            result.getHttpResponse().setHeaders(response.getStringHeaders());
            result.getHttpResponse().setContentType(response.getMediaType());
            result.getHttpResponse().setBody(responseContent);
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e){
            result.setRequestMessage(e.getMessage());
            e.printStackTrace(); //@TODO: refactor
        }

        return result;
    }
}
