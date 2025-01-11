package com.github.clagomess.tomato.dto.data;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class WorkspaceSessionDtoTest {
    @Test
    public void equalsHashCode(){
        var dtoA = new WorkspaceSessionDto();
        dtoA.setId("aaa");

        var dtoB = new WorkspaceSessionDto();
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }
}
