package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlBuilder {
    private final RequestDto request;
    private final Map<String, String> environment;

    protected UrlBuilder(
            EnvironmentPublisher environmentPublisher,
            RequestDto request
    ) {
        this.request = request;

        List<EnvironmentItemDto> envs = environmentPublisher.getCurrentEnvs()
                .request();

        environment = new HashMap<>(envs.size());

        envs.forEach(item -> environment.put(
                String.format("{{%s}}", item.getKey()),
                item.getValue()
        ));
    }

    public UrlBuilder(RequestDto request){
        this(EnvironmentPublisher.getInstance(), request);
    }

    public URI buildUri() {
        StringBuilder urlBuilder = new StringBuilder(request.getUrl());

        buildUrlEnvironment(urlBuilder);
        buildPathVariables(urlBuilder);
        buildQueryParams(urlBuilder);

        return URI.create(urlBuilder.toString());
    }

    protected void buildUrlEnvironment(StringBuilder urlBuilder) {
        if(environment.isEmpty()) return;

        for(var env : environment.entrySet()) {
            int idx = 0;

            while ((idx = urlBuilder.indexOf(env.getKey(), idx)) != -1){
                urlBuilder.replace(
                        idx,
                        idx + env.getKey().length(),
                        env.getValue()
                );
            }
        }
    }

    protected String buildEncodedParamValue(String paramValue){
        if(paramValue == null) return "";
        StringBuilder encodedParamValue = new StringBuilder(paramValue);

        for(var env : environment.entrySet()) {
            if(!paramValue.contains(env.getKey())) continue;
            int idx = 0;

            while ((idx = encodedParamValue.indexOf(env.getKey(), idx)) != -1){
                encodedParamValue.replace(
                        idx,
                        idx + env.getKey().length(),
                        env.getValue()
                );
            }
        }

        return URLEncoder.encode(
                encodedParamValue.toString(),
                request.getUrlParam().getCharset()
        );
    }

    protected void buildPathVariables(StringBuilder urlBuilder) {
        if(request.getUrlParam().getPath().isEmpty()) return;

        var list = request.getUrlParam().getPath().stream()
                .filter(KeyValueItemDto::isSelected)
                .filter(item -> StringUtils.isNotBlank(item.getKey()))
                .sorted(Collections.reverseOrder())
                .toList();

        for(var param : list) {
            var key = ":" + param.getKey();
            int idx = 0;

            while ((idx = urlBuilder.indexOf(key, idx)) != -1){
                var value = buildEncodedParamValue(param.getValue());
                urlBuilder.replace(idx, idx + key.length(), value);
            }
        }
    }

    protected void buildQueryParams(StringBuilder urlBuilder) {
        if(request.getUrlParam().getQuery().isEmpty()) return;

        var list = request.getUrlParam().getQuery().stream()
                .filter(KeyValueItemDto::isSelected)
                .filter(item -> StringUtils.isNotBlank(item.getKey()))
                .sorted()
                .toList();

        if(list.isEmpty()) return;

        int idx = urlBuilder.indexOf("?");
        boolean isFirstParam;

        if(idx == -1){
            urlBuilder.append("?");
            isFirstParam = true;
        }else{
            isFirstParam = urlBuilder.length() == idx + 1;
        }

        for(var param : list) {
            if(!isFirstParam) urlBuilder.append("&");

            urlBuilder.append(URLEncoder.encode(
                    param.getKey(),
                    request.getUrlParam().getCharset()
            ));
            urlBuilder.append("=");
            urlBuilder.append(buildEncodedParamValue(param.getValue()));

            isFirstParam = false;
        }
    }
}
