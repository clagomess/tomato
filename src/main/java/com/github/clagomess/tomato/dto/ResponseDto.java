package com.github.clagomess.tomato.dto;

import com.github.clagomess.tomato.service.http.MediaType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    @Slf4j
    public static class Response {
        private Integer status;
        private String statusReason;
        private long bodySize;
        private long requestTime;
        private Map<String, List<String>> headers = new HashMap<>();
        private Map<String, String> cookies = new HashMap<>();
        private MediaType contentType;
        private File body;

        public String getBodyAsString(){
            int limit = 8192; // 8KB

            if(bodySize > limit){
                return "[Response content size execeds render limit of 8KB. Download instead]";
            }

            try (FileReader reader = new FileReader(body)){
                char[] buffer = new char[limit];
                int n = reader.read(buffer);
                return new String(buffer, 0, n);
            }catch (IOException e){
                log.error(e.getMessage(), e);
                return e.getMessage();
            }
        }
    }
}
