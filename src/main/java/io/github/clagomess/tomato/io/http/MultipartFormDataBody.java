package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.request.BodyDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;
import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.TEXT;

@RequiredArgsConstructor
public class MultipartFormDataBody {
    private final String boundary;
    private final BodyDto body;

    public MultipartFormDataBody(BodyDto body) {
        this.boundary = "tomato-" + System.currentTimeMillis();
        this.body = body;
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    public File build() throws IOException {
        var file = HttpService.createTempFile();
        var requestBuilder = new RequestBuilder();

        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))){
            var form = requestBuilder.buildMultipartFormData(body.getMultiPartForm()).toList();

            for (var item : form) {
                bos.write(String.format("--%s\r\n", boundary).getBytes());

                if(item.getType() == TEXT){
                    writeTextBoundary(
                            bos,
                            item.getKey(),
                            item.getValue()
                    );
                }

                if(item.getType() == FILE){
                    writeFileBoundary(bos, item);
                }

                bos.write("\r\n".getBytes());
            }

            bos.write(("--" + boundary + "--\r\n").getBytes());
        }

        return file;
    }

    protected void writeTextBoundary(
            OutputStream os,
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

        os.write(value.getBytes(body.getCharset()));
    }

    protected void writeFileBoundary(
            OutputStream os,
            FileKeyValueItemDto item
    ) throws IOException {
        if(StringUtils.isBlank(item.getValue())) throw new FileNotFoundException(item.getKey());

        var itemFile = new File(item.getValue());
        os.write(String.format(
                "Content-Type: %s\r\n",
                item.getValueContentType()
        ).getBytes());
        os.write(String.format(
                "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n",
                item.getKey(),
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
