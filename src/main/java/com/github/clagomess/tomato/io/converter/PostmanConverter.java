package com.github.clagomess.tomato.io.converter;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.external.PostmanCollectionV210Dto;
import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.PostmanCollectionPumpMapper;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.github.clagomess.tomato.enums.PostmanJsonSchemaEnum.COLLECTION;

@Slf4j
@RequiredArgsConstructor
public class PostmanConverter {
    private final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();
    private final PostmanCollectionPumpMapper pumpMapper = PostmanCollectionPumpMapper.INSTANCE;
    private final CollectionRepository collectionDataService;
    private final RequestRepository requestDataService;

    public PostmanConverter() {
        this(
                new CollectionRepository(),
                new RequestRepository()
        );
    }

    public void pumpPostmanCollection(
            File destination,
            File postmanCollection
    ) throws IOException {
        var postmanCollectionSchema = JsonSchemaBuilder.getPostmanJsonSchema(COLLECTION);

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

    protected void pumpPostmanCollection(
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
