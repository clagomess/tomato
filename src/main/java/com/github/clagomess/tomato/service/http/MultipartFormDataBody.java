package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.service.EnvironmentDataService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Collections;
import java.util.List;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.FILE;
import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;

@RequiredArgsConstructor
public class MultipartFormDataBody {
    private final EnvironmentDataService environmentDataService;

    private final String boundary;
    private final List<RequestDto.KeyValueItem> form;

    public MultipartFormDataBody(List<RequestDto.KeyValueItem> form) {
        this(
                new EnvironmentDataService(),
                "tomato-" + System.currentTimeMillis(),
                form
        );
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    public File build() throws IOException {
        var file = File.createTempFile("tomato-request-", ".bin");
        file.deleteOnExit();

        List<EnvironmentDto.Env> envs = environmentDataService.getWorkspaceSessionEnvironment()
                .map(EnvironmentDto::getEnvs)
                .orElse(Collections.emptyList());

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            for (var item : form) {
                if(!item.isSelected()) continue;
                if(StringUtils.isBlank(item.getKey())) continue;

                writer.write(String.format("--%s\r\n", boundary));

                if(item.getType() == TEXT){
                    writeTextBoundary(
                            writer,
                            envs,
                            item.getKey(),
                            item.getValue()
                    );
                }

                if(item.getType() == FILE){
                    writeFileBoundary(writer, item.getKey(), item.getValue());
                }

                writer.write("\r\n");
            }

            writer.write("--" + boundary + "--\r\n");
        }

        return file;
    }

    protected void writeTextBoundary(
            Writer writer,
            List<EnvironmentDto.Env> envs,
            String key,
            String value
    ) throws IOException {
        writer.write("Content-Type: text/plain\r\n");
        writer.write(String.format(
                "Content-Disposition: form-data; name=\"%s\"\r\n",
                key
        ));
        writer.write("\r\n");

        if(value == null) return;

        if(envs != null){
            for(var env : envs) {
                value = value.replace(
                        String.format("{{%s}}", env.getKey()),
                        env.getValue()
                );
            }
        }

        writer.write(value);
    }

    protected void writeFileBoundary(
            Writer writer,
            String key,
            String value
    ) throws IOException {
        if(value == null) throw new FileNotFoundException(key);

        var itemFile = new File(value);
        writer.write("Content-Type: application/octet-stream\r\n");
        writer.write(String.format(
                "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n",
                key,
                itemFile.getName()
        ));
        writer.write("\r\n");
        try(BufferedReader reader = new BufferedReader(new FileReader(itemFile))){
            char[] buffer = new char[8192];
            int n;

            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }
    }
}
