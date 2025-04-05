package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.request.BodyDto;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class UrlEncodedFormBody {
    private final BodyDto body;

    public String build() throws IOException {
        var requestBuilder = new RequestBuilder();

        StringBuilder urlEncoded = new StringBuilder();
        AtomicBoolean first = new AtomicBoolean(true);

        requestBuilder.buildUrlEncodedForm(body.getUrlEncodedForm())
                .forEach(item -> {
                    if(!first.getAndSet(false)) urlEncoded.append("&");

                    urlEncoded.append(item.getKey()).append("=");
                    urlEncoded.append(URLEncoder.encode(
                            item.getValue(),
                            body.getCharset()
                    ));
                });

        return urlEncoded.toString();
    }
}
