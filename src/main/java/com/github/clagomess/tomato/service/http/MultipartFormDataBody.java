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

        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))){
            for (var item : form) {
                if(!item.isSelected()) continue;
                if(StringUtils.isBlank(item.getKey())) continue;

                bos.write(String.format("--%s\r\n", boundary).getBytes());

                if(item.getType() == TEXT){
                    writeTextBoundary(
                            bos,
                            envs,
                            item.getKey(),
                            item.getValue()
                    );
                }

                if(item.getType() == FILE){
                    writeFileBoundary(bos, item.getKey(), item.getValue());
                }

                bos.write("\r\n".getBytes());
            }

            bos.write(("--" + boundary + "--\r\n").getBytes());
        }

        return file;
    }

    protected void writeTextBoundary(
            OutputStream os,
            List<EnvironmentDto.Env> envs,
            String key,
            String value
    ) throws IOException {
        os.write("Content-Type: text/plain\r\n".getBytes());
        os.write(String.format(
                "Content-Disposition: form-data; name=\"%s\"\r\n",
                key
        ).getBytes());
        os.write("\r\n".getBytes());

        if(value == null) return;

        if(envs != null){
            for(var env : envs) {
                value = value.replace(
                        String.format("{{%s}}", env.getKey()),
                        env.getValue()
                );
            }
        }

        os.write(value.getBytes());
    }

    protected void writeFileBoundary(
            OutputStream os,
            String key,
            String value
    ) throws IOException {
        if(value == null) throw new FileNotFoundException(key);

        var itemFile = new File(value);
        os.write("Content-Type: application/octet-stream\r\n".getBytes());
        os.write(String.format(
                "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n",
                key,
                itemFile.getName()
        ).getBytes());
        os.write("\r\n".getBytes());

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(itemFile))){
            byte[] buffer = new byte[8192];
            int n;

            while ((n = bis.read(buffer)) != -1) {
                os.write(buffer, 0, n);
            }
        }
    }
}
