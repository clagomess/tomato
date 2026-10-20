package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.request.BodyDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;

public class UrlEncodedFormBody extends RequestBuilder {
    private final BodyDto body;

    public UrlEncodedFormBody(
            @Nullable RequestHeadDto requestHead,
            BodyDto body
    ) {
        super(requestHead);
        this.body = body;
    }

    public String build() throws IOException {
        StringBuilder urlEncoded = new StringBuilder();
        AtomicBoolean first = new AtomicBoolean(true);

        buildUrlEncodedForm(body.getUrlEncodedForm())
                .forEach(item -> {
                    if(!first.getAndSet(false)) urlEncoded.append("&");

                    urlEncoded.append(URLEncoder.encode(
                            item.getKey(),
                            body.getCharset()
                    ));
                    urlEncoded.append("=");
                    urlEncoded.append(URLEncoder.encode(
                            item.getValue(),
                            body.getCharset()
                    ));
                });

        return urlEncoded.toString();
    }
}
