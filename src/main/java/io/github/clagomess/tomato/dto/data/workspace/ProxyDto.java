package io.github.clagomess.tomato.dto.data.workspace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.clagomess.tomato.dto.data.TomatoID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProxyDto implements Comparable<ProxyDto> {
    private TomatoID id = new TomatoID();
    private String host;
    private Integer port = 22;
    private String username;
    private String password;

    @Override
    public int compareTo(ProxyDto o) {
        return StringUtils.compareIgnoreCase(this.host, o.host, true);
    }
}
