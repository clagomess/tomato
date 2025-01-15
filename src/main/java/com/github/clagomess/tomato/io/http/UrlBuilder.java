package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class UrlBuilder {
    private final EnvironmentRepository environmentRepository;
    private final String url;

    public UrlBuilder(String url) {
        this.url = url;
        this.environmentRepository = new EnvironmentRepository();
    }

    public URI buildUri() throws IOException {
        Optional<EnvironmentDto> current = environmentRepository.getWorkspaceSessionEnvironment();
        if(current.isEmpty()){
            return URI.create(url);
        }

        String bufferUrl = url;

        for(var env : current.get().getEnvs()) {
            bufferUrl = bufferUrl.replace(
                    String.format("{{%s}}", env.getKey()),
                    env.getValue()
            );
        }

        return URI.create(bufferUrl);
    }

    protected Map<String, String> getQueryParams() {
        int idx = url.indexOf('?');
        if(idx == -1) return Map.of();

        String queryParam = url.substring(idx + 1);

        if(StringUtils.isBlank(queryParam)) return Map.of();

        if(queryParam.indexOf('=') == -1){
            return Map.of(queryParam, "");
        }

        var result = new HashMap<String, String>();

        Arrays.stream(queryParam.split("&"))
                .map(item -> item.split("="))
                .forEach(keyvalue -> result.put(
                        keyvalue[0],
                        keyvalue.length == 2 ? keyvalue[1] : ""
                ));

        return result;
    }

    public void updateQueryParam(List<RequestDto.KeyValueItem> query) {
        Map<String, String> queryParam = getQueryParams();

        if (queryParam.isEmpty()) {
            query.forEach(item -> item.setSelected(false));
            return;
        }

        for(var param : queryParam.entrySet()){
            Optional<RequestDto.KeyValueItem> kvitem = query.stream()
                    .filter(item -> Objects.equals(item.getKey(), param.getKey()))
                    .findFirst();

            kvitem.ifPresent(item -> {
                item.setSelected(true);
                item.setValue(param.getValue());
            });

            if(kvitem.isEmpty()){
                query.add(new RequestDto.KeyValueItem(
                        param.getKey(),
                        param.getValue()
                ));
            }
        }

        query.removeIf(item -> !queryParam.containsKey(item.getKey()));
    }

    public String recreateUrl(List<RequestDto.KeyValueItem> query){
        int idx = url.indexOf('?');
        StringBuilder urlBuffer = new StringBuilder(idx == -1 ? url : url.substring(0, idx));

        if(query.isEmpty()) return urlBuffer.toString();

        AtomicBoolean first = new AtomicBoolean(true);
        query.stream()
                .filter(RequestDto.KeyValueItem::isSelected)
                .filter(param -> StringUtils.isNotBlank(param.getKey()))
                .forEach(param -> {
                    if(first.get()){
                        urlBuffer.append("?");
                        first.set(false);
                    }else{
                        urlBuffer.append('&');
                    }

                    urlBuffer.append(param.getKey());
                    urlBuffer.append("=");

                    urlBuffer.append(URLEncoder.encode(
                            param.getValue() == null ? "" : param.getValue(),
                            StandardCharsets.UTF_8
                    ));
                });

        return urlBuffer.toString();
    }
}
