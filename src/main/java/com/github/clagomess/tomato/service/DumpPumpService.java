package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.external.PostmanCollectionV210Dto;
import com.github.clagomess.tomato.mapper.PumpMapper;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
public class DumpPumpService {
    private DumpPumpService() {}
    private static final DumpPumpService instance = new DumpPumpService();
    public synchronized static DumpPumpService getInstance(){
        return instance;
    }

    private final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();
    private final PumpMapper pumpMapper = PumpMapper.INSTANCE;

    private final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(
            SpecVersion.VersionFlag.V4
    );

    private final CollectionDataService collectionDataService = CollectionDataService.getInstance();
    private final RequestDataService requestDataService = RequestDataService.getInstance();

    @Getter
    private final JsonSchema postmanCollectionSchema = factory.getSchema(getClass().getResourceAsStream(
            "postman.collection.v2.1.0.schema.json"
    ));

    public void pumpPostmanCollection(
            File destination,
            File postmanCollection
    ) throws IOException {
        Set<ValidationMessage> validations  = postmanCollectionSchema.validate(
                mapper.readTree(postmanCollection)
        );

        if(!validations.isEmpty()) throw new IOException(validations.toString());

        PostmanCollectionV210Dto postmanCollectionV210 = mapper.readValue(
                postmanCollection,
                PostmanCollectionV210Dto.class
        );

        // validate parse
        postmanCollectionSchema.validate(mapper.readTree(
                mapper.writeValueAsString(postmanCollectionV210)
        ));

        List<PostmanCollectionV210Dto.Item> itens = postmanCollectionV210.getItem();
        if(itens.isEmpty()) throw new IOException("Empty postman collection");

        pumpPostmanCollection(destination, itens);
    }

    public void pumpPostmanCollection(
            File destination,
            List<PostmanCollectionV210Dto.Item> itens
    ) throws IOException {
        for(var item : itens) {
            log.debug("processing item: {}", item.getName());

            if(item.getRequest() == null){
                CollectionDto collection = pumpMapper.toCollectionDto(item);
                File collectionDir = collectionDataService.save(destination, collection);
                pumpPostmanCollection(collectionDir, item.getItem());
                continue;
            }

            RequestDto request = pumpMapper.toRequestDto(item);
            requestDataService.save(destination, request);
        }
    }
}
