package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.enums.HttpStatusEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Setter
public class HttpDebug {
    private HttpRequest request;
    private String requestBodyString;
    private File requestBodyFile;

    private HttpResponse<Path> response;
    private File responseBodyFile;

    private final int defaultLimitBody = 300;

    public String assembly(){
        StringBuilder result = new StringBuilder();
        result.append("> ");
        result.append(request.method());
        result.append(" ");
        result.append(request.uri());
        result.append("\n");

        request.headers().map().forEach((key, value) -> {
            result.append("> ");
            result.append(assemblyHeaderItem(key, value));
        });

        result.append("\n");

        if(requestBodyString != null){
            result.append(assemblyBody(requestBodyString, defaultLimitBody)).append("\n");
        }

        if(requestBodyFile != null){
            result.append(assemblyBody(requestBodyFile, defaultLimitBody)).append("\n");
        }

        if(response == null) return result.toString();

        result.append("\n");
        result.append("< ");
        result.append(response.version());
        result.append(" ");
        result.append(response.statusCode());
        result.append(" ");
        result.append(HttpStatusEnum.getReasonPhrase(response.statusCode()));
        result.append("\n");

        response.headers().map().forEach((key, value) -> {
            result.append("< ");
            result.append(assemblyHeaderItem(key, value));
        });

        result.append("\n");

        if(responseBodyFile != null){
            result.append(assemblyBody(responseBodyFile, defaultLimitBody)).append("\n");
        }

        return result.toString();
    }

    protected StringBuilder assemblyHeaderItem(String key, List<String> value){
        StringBuilder result = new StringBuilder();

        for (var item : value) {
            result.append(key);
            result.append(": ");
            result.append(item);
            result.append("\n");
        }

        return result;
    }

    protected String assemblyBody(String body, int limit){
        long size = body.length();

        if(size > limit){
            return body.substring(0, limit) + String.format(
                    "\n[more %s bytes]",
                    size - limit
            );
        }else{
            return body;
        }
    }

    protected String assemblyBody(File body, int limit){
        long fileSize = body.length();
        if(fileSize == 0) return "";

        StringBuilder result = new StringBuilder();

        try (FileReader reader = new FileReader(body)){
            char[] buffer = new char[limit];
            int n = reader.read(buffer);
            result.append(buffer, 0, n);

            if(fileSize > limit){
                result.append(String.format(
                        "\n[more %s bytes]",
                        fileSize - limit
                ));
            }

            return result.toString();
        }catch (IOException e){
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
}
