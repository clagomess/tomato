package com.github.clagomess.tomato.io.converter;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.external.PostmanCollectionV210Dto;
import com.github.clagomess.tomato.dto.external.PostmanEnvironmentDto;
import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.PostmanCollectionPumpMapper;
import com.github.clagomess.tomato.mapper.PostmanEnvironmentPumpMapper;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.github.clagomess.tomato.enums.PostmanJsonSchemaEnum.COLLECTION;
import static com.github.clagomess.tomato.enums.PostmanJsonSchemaEnum.ENVIRONMENT;

@Slf4j
@RequiredArgsConstructor
public class PostmanConverter {
    private final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();
    private final PostmanCollectionPumpMapper colectionPumpMapper = PostmanCollectionPumpMapper.INSTANCE;
    private final PostmanEnvironmentPumpMapper environmentPumpMapper = PostmanEnvironmentPumpMapper.INSTANCE;
    private final CollectionRepository collectionRepository;
    private final RequestRepository requestRepository;
    private final EnvironmentRepository environmentRepository;

    public PostmanConverter() {
        this(
                new CollectionRepository(),
                new RequestRepository(),
                new EnvironmentRepository()
        );
    }

    public void pumpCollection(
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

        pumpCollection(destination, itens);
    }

    protected void pumpCollection(
            File destination,
            List<PostmanCollectionV210Dto.Item> itens
    ) throws IOException {
        for(var item : itens) {
            log.debug("processing item: {}", item.getName());

            if(item.getRequest() == null){
                CollectionDto collection = colectionPumpMapper.toCollectionDto(item);
                File collectionDir = collectionRepository.save(destination, collection);
                pumpCollection(collectionDir, item.getItem());
                continue;
            }

            RequestDto request = colectionPumpMapper.toRequestDto(item);
            requestRepository.save(destination, request);
        }
    }

    public String pumpEnvironment(
            File postmanEnvironment
    ) throws IOException {
        var schema = JsonSchemaBuilder.getPostmanJsonSchema(ENVIRONMENT);

        Set<ValidationMessage> validations  = schema.validate(
                mapper.readTree(postmanEnvironment)
        );

        if(!validations.isEmpty()) throw new IOException(validations.toString());

        PostmanEnvironmentDto postmanEnvironmentDto = mapper.readValue(
                postmanEnvironment,
                PostmanEnvironmentDto.class
        );

        // validate parse
        schema.validate(mapper.readTree(
                mapper.writeValueAsString(postmanEnvironmentDto)
        ));

        EnvironmentDto result = environmentPumpMapper.toEnvironmentDto(postmanEnvironmentDto);

        environmentRepository.save(result);

        return result.getId();
    }
}
