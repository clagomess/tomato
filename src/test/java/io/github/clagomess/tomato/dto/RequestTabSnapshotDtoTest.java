package io.github.clagomess.tomato.dto;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class RequestTabSnapshotDtoTest {
    private final File workspaceDir = new File("target");
    private final RequestDto request = new RequestDto();
    private final RequestHeadDto requestHead;

    RequestTabSnapshotDtoTest() {
        requestHead = new RequestHeadDto();
        requestHead.setPath(new File("target/foo/request.json"));
    }

    @Nested
    class toSessionState {
        @Test
        void whenNew(){
            var snapshot = new RequestTabSnapshotDto(true, null, request);
            var state = snapshot.toSessionState(workspaceDir);

            assertNull(state.getFilepath());
            assertNotNull(state.getStaging());
        }

        @Test
        void whenModified(){
            var snapshot = new RequestTabSnapshotDto(true, requestHead, request);
            var state = snapshot.toSessionState(workspaceDir);

            assertEquals("foo/request.json", state.getFilepath());
            assertNotNull(state.getStaging());
        }

        @Test
        void whenOpened(){
            var snapshot = new RequestTabSnapshotDto(false, requestHead, request);
            var state = snapshot.toSessionState(workspaceDir);

            assertEquals("foo/request.json", state.getFilepath());
            assertNull(state.getStaging());
        }
    }

    @Test
    void replaceBasePath(){
        var snapshot = new RequestTabSnapshotDto(true, null, request);
        var result = snapshot.replaceBasePath(workspaceDir, requestHead.getPath());
        assertEquals("foo/request.json", result);
    }
}
