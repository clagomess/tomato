package io.github.clagomess.tomato.ui.component.envtextfield;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
class EnvDocumentListenerTest {

    @Test
    void patternEnv(){
        Pattern pattern = new EnvDocumentListener(null).patternEnv;

        Matcher matcher = pattern.matcher("{{aaa}} {{ }} { asasas {} {{}}{{a}} t {{c}}{{d}}");

        List<String> found = new ArrayList<>();

        while (matcher.find()) {
            found.add(matcher.group());
            log.info("found: {}:{} - {}", matcher.start(), matcher.end(), matcher.group());
        }

        Assertions.assertThat(found)
                .containsOnly(
                        "{{aaa}}",
                        "{{ }}",
                        "{{}}",
                        "{{a}}",
                        "{{c}}",
                        "{{d}}"
                );
    }
}
