package io.github.clagomess.tomato.ui.component.envtextfield;

import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PathVarMap {
    @Setter
    private List<KeyValueItemDto> urlPathParam;

    @Getter
    private final Map<String, String> injected = new HashMap<>();

    public PathVarMap(@Nullable EnvTextfieldOptions.PathVar pathVar) {
        this.urlPathParam = pathVar != null ?
                pathVar.urlPathParam() :
                List.of();
    }

    public boolean containsKey(String token) {
        if(urlPathParam.isEmpty()) return false;

        Optional<KeyValueItemDto> result = urlPathParam.stream()
                .filter(KeyValueItemDto::isSelected)
                .filter(item -> token.equals(":" + item.getKey()))
                .findFirst();

        if(result.isPresent()){
            injected.putIfAbsent(token, result.get().getValue());
            return true;
        }

        return false;
    }
}
