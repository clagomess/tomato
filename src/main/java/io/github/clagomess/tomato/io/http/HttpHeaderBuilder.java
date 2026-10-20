package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;

import java.net.http.HttpRequest;

import static io.github.clagomess.tomato.util.RevisionUtil.DEPLOY_TAG;

public class HttpHeaderBuilder extends RequestBuilder {
    private final HttpRequest.Builder httpRequestBuilder;
    private final RequestDto request;

     public HttpHeaderBuilder(
             HttpRequest.Builder httpRequestBuilder,
             RequestHeadDto requestHead,
             RequestDto request
     ) {
        super(requestHead);
        this.httpRequestBuilder = httpRequestBuilder;
        this.request = request;
    }


    public void build() {
        httpRequestBuilder.setHeader(
                "User-Agent",
                "Tomato/" + DEPLOY_TAG
        );

        buildHeaders(request.getHeaders())
                .forEach(header -> httpRequestBuilder.setHeader(
                        header.getKey(),
                        header.getValue()
                ));

        buildCookies(request.getCookies())
                .forEach(cookie -> httpRequestBuilder.header(
                        "Cookie",
                        cookie.getKey() + "=" + cookie.getValue()
                ));
    }
}
