package io.github.clagomess.tomato.controller.component.envtextfield;

import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.table.KeyValueTMDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ViewInjectedEnvironmentFrameController {
    private static final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    public Stream<KeyValueTMDto> getKeyValueStream(
            @Nullable RequestHeadDto requestHead,
            Map<String, String> injected
    ) {
        if(injected == null || injected.isEmpty()) return Stream.empty();

        var containsEmptyValue = injected.entrySet().stream()
                .anyMatch(entry -> StringUtils.isBlank(entry.getValue()));

        if(containsEmptyValue && requestHead != null){
            Map<String, String> envs = getEnvsAsMap(requestHead);

            for(var entry : envs.entrySet()){
                var key = "{{" + entry.getKey() + "}}";
                if(!injected.containsKey(key)) continue;
                injected.put(key, entry.getValue());
            }
        }

        return injected.entrySet().stream()
                .map(entry -> new KeyValueTMDto(
                        entry.getKey(),
                        entry.getValue()
                ));
    }

    protected Map<String, String> getEnvsAsMap(RequestHeadDto requestHead){
        return environmentPublisher.getCurrentEnvs()
                .request(requestHead.getParent())
                .stream()
                .collect(Collectors.toMap(
                        EnvironmentItemDto::getKey,
                        EnvironmentItemDto::getValue
                ));
    }
}
