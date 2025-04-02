package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class EnvMap {
    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();

    @Getter
    private final Map<String, String> injected = new HashMap<>();

    public boolean containsKey(String token) throws IOException {
        Optional<EnvironmentDto> current = environmentRepository.getWorkspaceSessionEnvironment();
        if(current.isEmpty()) return false;

        Optional<EnvironmentItemDto> result = current.get().getEnvs().parallelStream()
                .filter(env -> token.equals("{{" + env.getKey() + "}}"))
                .findFirst();

        if(result.isPresent()){
            injected.putIfAbsent(token, result.get().getValue());
            return true;
        }

        return false;
    }
}
