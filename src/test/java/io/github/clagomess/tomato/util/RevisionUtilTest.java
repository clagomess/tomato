package io.github.clagomess.tomato.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static io.github.clagomess.tomato.util.RevisionUtil.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class RevisionUtilTest {
    @Test
    void static_constructor() {
        assertNotNull(DEPLOY_DATE);
        assertNotNull(DEPLOY_COMMIT);
        assertNotNull(DEPLOY_TAG);
        assertNotNull(REVISION);
        log.info(REVISION);
    }
}
