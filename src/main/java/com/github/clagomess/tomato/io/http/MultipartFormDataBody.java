package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.request.BodyDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Collections;
import java.util.List;

import static com.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;
import static com.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.TEXT;

@RequiredArgsConstructor
public class MultipartFormDataBody {
    private final EnvironmentRepository environmentRepository;

    private final String boundary;
    private final BodyDto body;

    public MultipartFormDataBody(BodyDto body) {
        this(
                new EnvironmentRepository(),
                "tomato-" + System.currentTimeMillis(),
                body
        );
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    public File build() throws IOException {
        var file = HttpService.createTempFile();

        List<KeyValueItemDto> envs = environmentRepository.getWorkspaceSessionEnvironment()
                .map(EnvironmentDto::getEnvs)
                .orElse(Collections.emptyList());

        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))){
            for (var item : body.getMultiPartForm()) {
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
            List<KeyValueItemDto> envs,
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

        os.write(value.getBytes(body.getCharset()));
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
