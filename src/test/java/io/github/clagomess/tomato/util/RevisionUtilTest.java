package io.github.clagomess.tomato.util;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

@Slf4j
public class RevisionUtilTest {
    @Test
    public void newInstace() {
        val result = RevisionUtil.getInstance();
        log.info("{}", result);
    }
}
