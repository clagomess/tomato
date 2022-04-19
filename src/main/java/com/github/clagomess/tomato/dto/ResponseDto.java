package com.github.clagomess.tomato.dto;

import lombok.Data;

import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    public static class Response {
        private Integer status;
        private String statusReason;
        private Integer bodySize;
        private long requestTime;
        private Map<String, List<String>> headers;
        private Map<String, String> cookies;
        private MediaType contentType;
        private String body;
    }
}
