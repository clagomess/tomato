package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.util.RevisionUtil;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.http.HttpRequest;

@RequiredArgsConstructor
public class HttpHeaderBuilder {
    private final HttpRequest.Builder httpRequestBuilder;
    private final RequestDto request;

    public void build() throws IOException {
        var requestBuilder = new RequestBuilder();

        httpRequestBuilder.setHeader(
                "User-Agent",
                "Tomato/" + RevisionUtil.getInstance().getDeployTag()
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
