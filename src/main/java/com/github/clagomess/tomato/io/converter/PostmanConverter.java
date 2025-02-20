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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@RequiredArgsConstructor
public class PostmanConverter {
    private final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();
    private final PostmanCollectionPumpMapper colectionPumpMapper = PostmanCollectionPumpMapper.INSTANCE;
    private final PostmanCollectionDumpMapper colectionDumpMapper = PostmanCollectionDumpMapper.INSTANCE;
    private final PostmanEnvironmentPumpMapper environmentPumpMapper = PostmanEnvironmentPumpMapper.INSTANCE;
    private final PostmanEnvironmentDumpMapper environmentDumpMapper = PostmanEnvironmentDumpMapper.INSTANCE;
    private final CollectionRepository collectionRepository;
    private final RequestRepository requestRepository;
    private final EnvironmentRepository environmentRepository;

    public PostmanConverter() {
        collectionRepository = new CollectionRepository();
        requestRepository = new RequestRepository();
        environmentRepository = new EnvironmentRepository();
    }

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
                    PostmanCollectionV210Dto.Item item = colectionDumpMapper.toItem(tree);
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
                .forEachOrdered(request -> items.add(colectionDumpMapper.toItem(request)));

        return items;
    }

    public void dumpEnvironment(
            File destination,
            EnvironmentDto environment
    ) throws IOException {
        PostmanEnvironmentDto postmanEnvironmentDto = environmentDumpMapper.toEnvironmentDto(environment);

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
}
