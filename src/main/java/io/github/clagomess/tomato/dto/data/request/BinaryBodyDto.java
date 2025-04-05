package io.github.clagomess.tomato.dto.data.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import static io.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinaryBodyDto {
    private String contentType = APPLICATION_OCTET_STREAM_TYPE;
    private String file;
}
