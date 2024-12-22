package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.dto.data.RequestDto;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.List;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.FILE;
import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;

public class MultipartFormDataBody {
    private final String boundary;
    private final List<RequestDto.KeyValueItem> form;

    public MultipartFormDataBody(List<RequestDto.KeyValueItem> form) {
        this.boundary = "tomato-" + System.currentTimeMillis();
        this.form = form;
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    public File build() throws IOException {
        var file = File.createTempFile("tomato-request-", ".bin");
        file.deleteOnExit();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            for (var item : form) {
                if(!item.isSelected()) continue;
                if(StringUtils.isBlank(item.getKey())) continue;

                writer.write(String.format("--%s\r\n", boundary));

                if(item.getType() == TEXT){
                    writer.write("Content-Type: text/plain\r\n");
                    writer.write(String.format(
                            "Content-Disposition: form-data; name=\"%s\"\r\n",
                            item.getKey()
                    ));
                    writer.write("\r\n");
                    if(item.getValue() != null) writer.write(item.getValue());
                }

                if(item.getType() == FILE){
                    if(item.getValue() == null) throw new FileNotFoundException(item.getKey());

                    var itemFile = new File(item.getValue());
                    writer.write("Content-Type: application/octet-stream\r\n");
                    writer.write(String.format(
                            "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n",
                            item.getKey(),
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

                writer.write("\r\n");
            }

            writer.write("--" + boundary + "--\r\n");
        }

        return file;
    }
}
