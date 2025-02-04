package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UrlBuilder {
    private final RequestDto request;
    private final Map<String, String> environment;

    public UrlBuilder(EnvironmentRepository environmentRepository, RequestDto request) throws IOException {
        this.request = request;

        Optional<EnvironmentDto> currentEnv = environmentRepository.getWorkspaceSessionEnvironment();
        if(currentEnv.isPresent() && !currentEnv.get().getEnvs().isEmpty()){
            environment = new HashMap<>(currentEnv.get().getEnvs().size());

            currentEnv.get().getEnvs().forEach(item -> environment.put(
                    String.format("{{%s}}", item.getKey()),
                    item.getValue()
            ));
        }else{
            environment = Collections.emptyMap();
        }
    }

    public UrlBuilder(RequestDto request) throws IOException {
        this(new EnvironmentRepository(), request);
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

        for(var env : environment.entrySet()) {
            if(!paramValue.contains(env.getKey())) continue;

            paramValue = paramValue.replace(env.getKey(), env.getValue());
        }

        return URLEncoder.encode(paramValue, request.getUrlParam().getCharset());
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

            urlBuilder.append(param.getKey());
            urlBuilder.append("=");
            urlBuilder.append(buildEncodedParamValue(param.getValue()));

            isFirstParam = false;
        }
    }
}
