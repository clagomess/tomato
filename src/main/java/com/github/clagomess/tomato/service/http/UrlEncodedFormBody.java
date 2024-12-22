package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.dto.data.RequestDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class UrlEncodedFormBody {
    private final List<RequestDto.KeyValueItem> form;
    @Getter
    private final String contentType = "application/x-www-form-urlencoded";

    public String build(){
        StringBuilder urlEncoded = new StringBuilder();
        boolean first = true;

        for(var item : form){
            if(!item.isSelected()) continue;
            if(StringUtils.isBlank(item.getKey())) continue;

            if(!first) urlEncoded.append("&");
            urlEncoded.append(item.getKey()).append("=");

            if(item.getValue() != null) {
                urlEncoded.append(URLEncoder.encode(
                        item.getValue(),
                        StandardCharsets.UTF_8
                ));
            }

            first = false;
        }

        return urlEncoded.toString();
    }
}
