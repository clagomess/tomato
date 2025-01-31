package com.github.clagomess.tomato.dto;

import com.github.clagomess.tomato.enums.HttpStatusEnum;
import com.github.clagomess.tomato.io.http.MediaType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString(of = {"requestId"})
public class ResponseDto {
    private String requestId;
    private LocalDateTime createTime = LocalDateTime.now();

    private boolean requestStatus = false;
    private String requestMessage;
    private String requestDebug;
    private String requestCertIssue;
    private Response httpResponse;

    public ResponseDto(String requestId) {
        this.requestId = requestId;
    }

    @Getter
    @Setter
    @Slf4j
    public static class Response {
        private final long requestTime;
        private final Integer status;
        private final String statusReason;
        private final Map<String, List<String>> headers;
        private final Map<String, String> cookies;
        private final MediaType contentType;

        private final File body;
        private final long bodySize;
        private String bodyAsString = "< Empty Body";
        private boolean renderBodyByContentType = true;

        public Response(
                HttpResponse<Path> response,
                long initRequestTime
        ) {
            this.requestTime = System.currentTimeMillis() - initRequestTime;
            this.status = response.statusCode();
            this.statusReason = HttpStatusEnum.getReasonPhrase(this.status);
            this.headers = response.headers().map();
            this.cookies = parseSetCookies(this.headers);
            this.contentType = new MediaType(response.headers());
            this.body = response.body().toFile();
            this.bodySize = body.length();

            buildBodyString();
        }

        protected Map<String, String> parseSetCookies(
                Map<String, List<String>> headers
        ){
            var cookieSet = headers.entrySet().stream()
                    .filter(entry -> "set-cookie".equalsIgnoreCase(entry.getKey()))
                    .collect(Collectors.toSet());

            if(cookieSet.isEmpty()) return Map.of();

            Map<String, String> result = new HashMap<>(cookieSet.size());
            cookieSet.forEach(entry -> {
                for(String value : entry.getValue()) {
                    var param = value.split("=");
                    var cookieKey = param[0];
                    var cookieValue = param[1].split(";")[0];

                    result.put(cookieKey, cookieValue);
                }
            });

            return result;
        }

        protected void buildBodyString() {
            int limit = 131_072; // 128KB

            if(bodySize == 0){
                renderBodyByContentType = false;
                return;
            }

            if(!contentType.isString()){
                renderBodyByContentType = false;
                bodyAsString = """
                < Binary content.
                < Download instead
                """;
                return;
            }

            if(bodySize > limit){
                renderBodyByContentType = false;
                bodyAsString = """
                < Response content size execeds render limit of 128KB.
                < Download instead
                """;
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(
                    body,
                    contentType.getCharsetOrDefault()
            ))){
                char[] buffer = new char[limit];
                int n = br.read(buffer);
                bodyAsString = new String(buffer, 0, n);
            }catch (IOException e){
                log.error(e.getMessage(), e);
                renderBodyByContentType = false;
                bodyAsString = e.getMessage();
            }
        }
    }
}
