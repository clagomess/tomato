package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;

public class HttpServiceTest {
    private static ClientAndServer mockServer;

    @BeforeAll
    public static void setup(){
        mockServer = ClientAndServer.startClientAndServer(8500);
    }

    @Test
    public void get_response_json() {
        mockServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/get_response_json")
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody("{\"foo\": \"bar\", \"true\", false, \"number\": 1}")
                );

        RequestDto request = new RequestDto();
        request.setUrl("http://localhost:8500/get_response_json");
        request.setMethod(HttpMethodEnum.GET);

        HttpService httpService = new HttpService();
        ResponseDto response = httpService.perform(request);

        Assertions.assertTrue(response.isRequestStatus());
        Assertions.assertNull(response.getRequestMessage());
        Assertions.assertEquals(200, response.getHttpResponse().getStatus());
        Assertions.assertEquals("OK", response.getHttpResponse().getStatusReason());
        Assertions.assertEquals(42, response.getHttpResponse().getBodySize());
        Assertions.assertEquals("application/json", response.getHttpResponse().getContentType().toString());
        Assertions.assertNotNull(response.getRequestDebug());
    }

    @AfterAll
    public static void terminate(){
        mockServer.stop();
    }
}
