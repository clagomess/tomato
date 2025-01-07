package com.github.clagomess.tomato.dto;

import com.github.clagomess.tomato.enums.HttpStatusEnum;
import com.github.clagomess.tomato.service.http.MediaType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class ResponseDto {
    private String requestId;
    private LocalDateTime createTime = LocalDateTime.now();

    private boolean requestStatus = false;
    private String requestMessage;
    private String requestDebug;
    private Response httpResponse;

    public ResponseDto(String requestId) {
        this.requestId = requestId;
    }

    @Data
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
            this.cookies = Map.of(); //@TODO: impl. parse cookies?

            Optional<String> contentType = response.headers().firstValue("content-type");
            this.contentType = contentType.map(MediaType::new).orElse(MediaType.WILDCARD);

            this.body = response.body().toFile();
            this.bodySize = body.length();

            buildBodyString();
        }

        protected void buildBodyString() {
            int limit = 8192; // 8KB

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
                < Response content size execeds render limit of 8KB.
                < Download instead
                """;
                return;
            }

            try (FileReader reader = new FileReader(body)){
                char[] buffer = new char[limit];
                int n = reader.read(buffer);
                bodyAsString = new String(buffer, 0, n);
            }catch (IOException e){
                log.error(e.getMessage(), e);
                renderBodyByContentType = false;
                bodyAsString = e.getMessage();
            }
        }
    }
}
