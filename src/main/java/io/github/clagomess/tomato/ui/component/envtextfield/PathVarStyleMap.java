package io.github.clagomess.tomato.ui.component.envtextfield;

import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PathVarStyleMap implements StyleMap {
    protected static final Pattern pattern = Pattern.compile("(:[a-z]\\w*)");
    private static final SimpleAttributeSet filledStyle = StyleMap.getBlueStyle();
    private static final SimpleAttributeSet notFilledStyle = StyleMap.getRedStyle();

    @Getter
    private final HashMap<String, String> injected = new HashMap<>();

    @Setter
    private List<KeyValueItemDto> urlPathParam;

    public PathVarStyleMap(EnvTextfieldOptions.@NonNull PathVar pathVar) {
        this.urlPathParam = pathVar.urlPathParam();
    }

    protected boolean containsKey(String token) {
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

    public void update(
            StyledDocument document,
            String text
    ) throws IOException {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String token = matcher.group();
            if(log.isDebugEnabled()) log.debug("PathVar-Token found: {}", token);

            document.setCharacterAttributes(
                    matcher.start(),
                    token.length(),
                    containsKey(token) ? filledStyle : notFilledStyle,
                    true
            );
        }
    }
}
