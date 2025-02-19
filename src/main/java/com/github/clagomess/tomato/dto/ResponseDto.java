package com.github.clagomess.tomato.dto;

import com.github.clagomess.tomato.enums.HttpStatusEnum;
import com.github.clagomess.tomato.io.http.HttpService;
import com.github.clagomess.tomato.io.http.MediaType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

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
        private final String bodyDownloadFileName;

        private File body;
        private long bodySize;
        private String bodyAsString = "< Empty Body";
        private boolean renderBodyByContentType = true;

        public Response(
                HttpResponse<Path> response,
                long initRequestTime
        ) throws IOException {
            this.requestTime = System.currentTimeMillis() - initRequestTime;
            this.status = response.statusCode();
            this.statusReason = HttpStatusEnum.getReasonPhrase(this.status);
            this.headers = response.headers().map();
            this.cookies = parseSetCookies(this.headers);
            this.contentType = new MediaType(response.headers());
            this.bodyDownloadFileName = parseBodyDownloadFileName(response);

            setBody(convertIfGziped(
                    response.headers(),
                    response.body().toFile()
            ));
        }

        public File convertIfGziped(HttpHeaders headers, File response) throws IOException {
            if(!"gzip".equals(headers.firstValue("content-encoding").orElse(null))){
                return response;
            }

            var newResponseFile = HttpService.createTempFile();

            try(
                    var fis = new FileInputStream(response);
                    var gis = new GZIPInputStream(fis);
                    var bis = new BufferedInputStream(gis);
                    var fos = new FileOutputStream(newResponseFile);
                    var bos = new BufferedOutputStream(fos)
            ){
                bis.transferTo(bos);
            }

            return newResponseFile;
        }

        public void setBody(File body) {
            this.body = body;
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

        protected String parseBodyDownloadFileName(
                HttpResponse<Path> httpResponse
        ){
            Optional<String> contentDisposition = httpResponse.headers()
                    .firstValue("Content-Disposition");

            if(contentDisposition.isPresent()){
                var filename = Arrays.stream(contentDisposition.get().split(";"))
                        .filter(StringUtils::isNotBlank)
                        .filter(param -> param.contains("filename"))
                        .map(param -> param.replace("filename=", ""))
                        .collect(Collectors.joining(""));

                if(StringUtils.isNotBlank(filename)) return filename;
            }

            String[] paths = httpResponse.uri().getPath().split("/");
            if(paths.length > 0 && paths[paths.length-1].contains(".")){
                return paths[paths.length-1];
            }

            return "response.bin";
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
