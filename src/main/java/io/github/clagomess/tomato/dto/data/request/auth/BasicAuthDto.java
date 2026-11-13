package io.github.clagomess.tomato.dto.data.request.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicAuthDto {
    private String username;
    private String password;
}
