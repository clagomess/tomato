package com.github.clagomess.tomato.dto.external;

import com.github.clagomess.tomato.service.DumpPumpService;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class PostmanCollectionV210DtoTest {
    private final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();
    private final DumpPumpService dumpPumpService = DumpPumpService.getInstance();

    @ParameterizedTest
    @ValueSource(strings = {
            "auth.postman.collection.v2.1.0.json",
            "without-auth.postman.collection.v2.1.0.json",
            "body-raw.postman.collection.v2.1.0.json",
            "formdata-file.postman.collection.v2.1.0.json",
    })
    public void pumpDumpTest(String filename) throws IOException {
        var url = getClass().getResourceAsStream(String.format(
                "PostmanCollectionV210DtoTest/%s",
                filename
        ));
        var result = mapper.readValue(url, PostmanCollectionV210Dto.class);

        // dump validate
        var resultDump = mapper.writeValueAsString(result);

        Set<ValidationMessage> validations = dumpPumpService.getPostmanCollectionSchema()
                .validate(mapper.readTree(resultDump));

        Assertions.assertThat(validations).isEmpty();
    }
}
