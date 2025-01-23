package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
class EnvMap {
    private final Map<String, String> avaliable = new HashMap<>();
    private final Map<String, String> injected = new HashMap<>();

    public void put(EnvironmentDto dto) {
        dto.getEnvs().forEach(env -> {
            avaliable.put("{{" + env.getKey() + "}}", env.getValue());
        });
    }

    public boolean containsKey(String token){
        if(avaliable.containsKey(token)){
            injected.putIfAbsent(token, avaliable.get(token));
            return true;
        }

        return false;
    }

    public void reset(){
        avaliable.clear();
        injected.clear();
    }
}
