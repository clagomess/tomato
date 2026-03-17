package io.github.clagomess.tomato.dto.key;

import io.github.clagomess.tomato.dto.data.TomatoID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class TabKey {
    public final UUID uuid = UUID.randomUUID();
    private TomatoID requestId;
}
