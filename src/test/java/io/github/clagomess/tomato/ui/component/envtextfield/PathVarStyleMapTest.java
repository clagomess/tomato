package io.github.clagomess.tomato.ui.component.envtextfield;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Slf4j
class PathVarStyleMapTest {

    @Test
    void pattern(){
        Matcher matcher = PathVarStyleMap.pattern.matcher("debukis:8000?:aaasa=asas&:x132xx:&::-123123:w");

        List<String> found = new ArrayList<>();

        while (matcher.find()) {
            found.add(matcher.group());
            log.info("found: {}:{} - {}", matcher.start(), matcher.end(), matcher.group());
        }

        Assertions.assertThat(found)
                .containsOnly(
                        ":aaasa",
                        ":x132xx",
                        ":w"
                );
    }
}
