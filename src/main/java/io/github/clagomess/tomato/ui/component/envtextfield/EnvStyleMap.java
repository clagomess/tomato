package io.github.clagomess.tomato.ui.component.envtextfield;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.ui.component.ColorConstant;
import lombok.extern.slf4j.Slf4j;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.clagomess.tomato.io.repository.EnvironmentRepository.SYSENV_KEYS;

@Slf4j
class EnvStyleMap implements StyleMap {
    protected static final Pattern pattern = Pattern.compile("(\\{\\{.+?}}?)");
    private final SimpleAttributeSet filledStyle = new SimpleAttributeSet();
    private final SimpleAttributeSet notFilledStyle = new SimpleAttributeSet();

    private final EnvironmentRepository environmentRepository;

    public EnvStyleMap() {
        this(new EnvironmentRepository());
    }

    public EnvStyleMap(EnvironmentRepository environmentRepository) {
        this.environmentRepository = environmentRepository;
        StyleConstants.setForeground(filledStyle, ColorConstant.GREEN);
        StyleConstants.setForeground(notFilledStyle, ColorConstant.RED);
    }

    protected boolean containsKey(String token) throws IOException {
        String key = token.substring(2).substring(0, token.length() - 4);

        if(SYSENV_KEYS.contains(key)){
            injected.putIfAbsent(token, null);
            return true;
        }

        Optional<EnvironmentDto> current = environmentRepository.getWorkspaceSessionEnvironment();
        if(current.isEmpty()) return false;

        Optional<EnvironmentItemDto> result = current.get().getEnvs().parallelStream()
                .filter(env -> Objects.equals(env.getKey(), key))
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
            if(log.isDebugEnabled()) log.debug("Env-Token found: {}", token);

            document.setCharacterAttributes(
                    matcher.start(),
                    token.length(),
                    containsKey(token) ? filledStyle : notFilledStyle,
                    true
            );
        }
    }
}
