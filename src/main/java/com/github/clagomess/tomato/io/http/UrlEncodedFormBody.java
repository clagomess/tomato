package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.request.BodyDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class UrlEncodedFormBody {
    private final EnvironmentRepository environmentRepository;
    private final BodyDto body;

    @Getter
    private final String contentType = "application/x-www-form-urlencoded";

    public UrlEncodedFormBody(BodyDto body) {
        this(
                new EnvironmentRepository(),
                body
        );
    }

    public String build() throws IOException {
        StringBuilder urlEncoded = new StringBuilder();
        boolean first = true;

        List<KeyValueItemDto> envs = environmentRepository.getWorkspaceSessionEnvironment()
                .map(EnvironmentDto::getEnvs)
                .orElse(Collections.emptyList());

        for(var item : body.getUrlEncodedForm()){
            if(!item.isSelected()) continue;
            if(StringUtils.isBlank(item.getKey())) continue;

            if(!first) urlEncoded.append("&");
            urlEncoded.append(item.getKey()).append("=");
            urlEncoded.append(buildValue(envs, item.getValue()));

            first = false;
        }

        return urlEncoded.toString();
    }

    protected String buildValue(
            List<KeyValueItemDto> envs,
            String value
    ){
        if(value == null) return "";

        if(envs != null){
            for(var env : envs) {
                value = value.replace(
                        String.format("{{%s}}", env.getKey()),
                        env.getValue()
                );
            }
        }

        return URLEncoder.encode(value, body.getCharset());
    }
}
