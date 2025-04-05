package io.github.clagomess.tomato.dto;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RequestTabSnapshotDtoTest {
    private final RequestDto request = new RequestDto();
    private final RequestHeadDto head;

    public RequestTabSnapshotDtoTest() {
        head = new RequestHeadDto();
        head.setPath(new File("target"));
    }

    @Test
    public void toSessionState_new(){
        var snapshot = new RequestTabSnapshotDto(true, null, request);
        var state = snapshot.toSessionState();
        assertNull(state.getFilepath());
        assertNotNull(state.getStaging());
    }

    @Test
    public void toSessionState_modified(){
        var snapshot = new RequestTabSnapshotDto(true, head, request);
        var state = snapshot.toSessionState();
        assertNotNull(state.getFilepath());
        assertNotNull(state.getStaging());
    }

    @Test
    public void toSessionState_opened(){
        var snapshot = new RequestTabSnapshotDto(false, head, request);
        var state = snapshot.toSessionState();
        assertNotNull(state.getFilepath());
        assertNull(state.getStaging());
    }
}
