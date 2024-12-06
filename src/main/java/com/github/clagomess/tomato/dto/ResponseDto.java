package com.github.clagomess.tomato.dto;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ResponseDto {
    private UUID requestId;
    private LocalDateTime createTime = LocalDateTime.now();

    private boolean requestStatus = false;
    private String requestMessage;
    private String requestDebug;
    private Response httpResponse;

    public ResponseDto(UUID requestId) {
        this.requestId = requestId;
    }

    @Data
    public static class Response {
        private Integer status;
        private String statusReason;
        private Integer bodySize;
        private long requestTime;
        private Map<String, List<String>> headers = new HashMap<>();
        private Map<String, String> cookies = new HashMap<>();
        private MediaType contentType;
        private byte[] body;

        public String getBodyAsString(){
            return new String(body);
        }

        public void setCookies(Map<String, NewCookie> map){
            if(map == null || map.isEmpty()) return;
            map.forEach((key, value) -> cookies.put(key, value.getValue()));
        }
    }
}
