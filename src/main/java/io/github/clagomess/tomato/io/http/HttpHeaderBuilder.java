package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import lombok.RequiredArgsConstructor;

import java.net.http.HttpRequest;

import static io.github.clagomess.tomato.util.RevisionUtil.DEPLOY_TAG;

@RequiredArgsConstructor
public class HttpHeaderBuilder {
    private final HttpRequest.Builder httpRequestBuilder;
    private final RequestHeadDto requestHead;
    private final RequestDto request;

    public void build() {
        var requestBuilder = new RequestBuilder(requestHead);

        httpRequestBuilder.setHeader(
                "User-Agent",
                "Tomato/" + DEPLOY_TAG
        );

        requestBuilder.buildHeaders(request.getHeaders())
                .forEach(header -> httpRequestBuilder.setHeader(
                        header.getKey(),
                        header.getValue()
                ));

        requestBuilder.buildCookies(request.getCookies())
                .forEach(cookie -> httpRequestBuilder.header(
                        "Cookie",
                        cookie.getKey() + "=" + cookie.getValue()
                ));
    }
}
