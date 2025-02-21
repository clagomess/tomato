package com.github.clagomess.tomato.io.converter;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.external.PostmanCollectionV210Dto;
import com.github.clagomess.tomato.dto.external.PostmanEnvironmentDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.PostmanCollectionDumpMapper;
import com.github.clagomess.tomato.mapper.PostmanCollectionPumpMapper;
import com.github.clagomess.tomato.mapper.PostmanEnvironmentDumpMapper;
import com.github.clagomess.tomato.mapper.PostmanEnvironmentPumpMapper;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.TestOnly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.github.clagomess.tomato.enums.PostmanJsonSchemaEnum.COLLECTION;
import static com.github.clagomess.tomato.enums.PostmanJsonSchemaEnum.ENVIRONMENT;

@Slf4j
public class PostmanConverter extends AbstractConverter {
    public PostmanConverter() {}

    @TestOnly
    public PostmanConverter(
            CollectionRepository collectionRepository,
            RequestRepository requestRepository,
            EnvironmentRepository environmentRepository
    ) {
        super(collectionRepository, requestRepository, environmentRepository);
    }

    @Override
    public String getConverterName() {
        return "Postman Collection v2.1.0";
    }

    @Override
    public CollectionDto pumpCollection(
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

        // create a 'root' collection
        CollectionDto root = new CollectionDto(postmanCollectionV210.getInfo().getName());
        File collectionDir = collectionRepository.save(destination, root);

        pumpCollection(collectionDir, itens);

        return root;
    }

    protected void pumpCollection(
            File destination,
            List<PostmanCollectionV210Dto.Item> itens
    ) throws IOException {
        for(var item : itens) {
            if(log.isDebugEnabled()) log.debug("processing item: {}", item.getName());

            if(item.getRequest() == null){
                CollectionDto collection = PostmanCollectionPumpMapper.INSTANCE.toCollectionDto(item);
                File collectionDir = collectionRepository.save(destination, collection);
                pumpCollection(collectionDir, item.getItem());
                continue;
            }

            RequestDto request = PostmanCollectionPumpMapper.INSTANCE.toRequestDto(item);
            requestRepository.save(destination, request);
        }
    }

    @Override
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

        EnvironmentDto result = PostmanEnvironmentPumpMapper.INSTANCE.toEnvironmentDto(postmanEnvironmentDto);

        environmentRepository.save(result);

        return result.getId();
    }

    @Override
    public void dumpCollection(
            File destination,
            CollectionTreeDto collectionTree
    ) throws IOException {
        PostmanCollectionV210Dto postmanCollection = new PostmanCollectionV210Dto();
        postmanCollection.setInfo(new PostmanCollectionV210Dto.Info());
        postmanCollection.getInfo().setName(collectionTree.getName());
        postmanCollection.getInfo().setId(UUID.randomUUID().toString());
        postmanCollection.setItem(dumpCollection(collectionTree));

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(destination))) {
            ObjectMapperUtil.getInstance()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(bw, postmanCollection);
        }

        var postmanCollectionSchema = JsonSchemaBuilder.getPostmanJsonSchema(COLLECTION);

        Set<ValidationMessage> validations  = postmanCollectionSchema.validate(
                mapper.readTree(destination)
        );

        if(!validations.isEmpty()) throw new IOException(validations.toString());
    }

    protected List<PostmanCollectionV210Dto.Item> dumpCollection(
            CollectionTreeDto collectionTree
    ){
        List<PostmanCollectionV210Dto.Item> items = new LinkedList<>();

        collectionTree.getChildren()
                .forEachOrdered(tree -> {
                    PostmanCollectionV210Dto.Item item = PostmanCollectionDumpMapper.INSTANCE.toItem(tree);
                    item.setItem(dumpCollection(tree));
                    items.add(item);
                });

        collectionTree.getRequests()
                .map(requestHead -> {
                    try {
                        return requestRepository.load(requestHead).orElseThrow();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEachOrdered(request -> items.add(
                        PostmanCollectionDumpMapper.INSTANCE.toItem(request)
                ));

        return items;
    }

    @Override
    public String getCollectionDumpFileSuffix() {
        return ".postman.collection.json";
    }

    @Override
    public void dumpEnvironment(
            File destination,
            String environmentId
    ) throws IOException {
        EnvironmentDto environment = environmentRepository.load(environmentId).orElseThrow();
        PostmanEnvironmentDto postmanEnvironmentDto = PostmanEnvironmentDumpMapper.INSTANCE.toEnvironmentDto(environment);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(destination))) {
            ObjectMapperUtil.getInstance()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(bw, postmanEnvironmentDto);
        }

        var schema = JsonSchemaBuilder.getPostmanJsonSchema(ENVIRONMENT);

        Set<ValidationMessage> validations  = schema.validate(
                mapper.readTree(destination)
        );

        if(!validations.isEmpty()) throw new IOException(validations.toString());
    }

    @Override
    public String getEnvironmentDumpFileSuffix() {
        return ".postman.environment.json";
    }
}
