package io.github.clagomess.tomato.controller.request;

import io.github.clagomess.tomato.dto.RequestPropertiesDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestPropertiesFrameControllerTest {
    private final RequestRepository requestRepositoryMock = Mockito.mock(RequestRepository.class);
    private final RequestPropertiesFrameController controller = new RequestPropertiesFrameController(
            requestRepositoryMock
    );

    @Test
    void properties() {
        var requestHead = new RequestHeadDto();
        var expected = new RequestPropertiesDto(
                "AAAAAAAA",
                "my-request",
                "/home/.tomato/data/request-AAAAAAAA.json",
                "512B",
                "2025-01-02 03:04:05"
        );

        Mockito.doReturn(expected)
                .when(requestRepositoryMock)
                .properties(requestHead);

        assertEquals(expected, controller.properties(requestHead));
    }
}
