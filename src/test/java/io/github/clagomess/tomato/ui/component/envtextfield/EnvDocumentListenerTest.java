package io.github.clagomess.tomato.ui.component.envtextfield;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Slf4j
class EnvDocumentListenerTest {

    @Test
    void patternEnv(){
        Matcher matcher = EnvDocumentListener.patternEnv.matcher("{{aaa}} {{ }} { asasas {} {{}}{{a}} t {{c}}{{d}}");

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

    @Test
    void patternVarPath(){
        Matcher matcher = EnvDocumentListener.patternPathVar.matcher("debukis?:aaasa=asas&:132xxx:&::-123123:w");

        List<String> found = new ArrayList<>();

        while (matcher.find()) {
            found.add(matcher.group());
            log.info("found: {}:{} - {}", matcher.start(), matcher.end(), matcher.group());
        }

        Assertions.assertThat(found)
                .containsOnly(
                        ":aaasa",
                        ":132xxx",
                        ":w"
                );
    }
}
