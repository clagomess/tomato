package com.github.clagomess.tomato.dto;

import com.github.clagomess.tomato.service.http.MediaType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
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
        private Map<String, List<String>> headers = new HashMap<>();
        private Map<String, String> cookies = new HashMap<>();
        private MediaType contentType;
        private byte[] body; //@TODO: must be a File

        public String getBodyAsString(){
            // @TODO: check charset, content-type (not-binary)
            return new String(body);
        }
    }
}
