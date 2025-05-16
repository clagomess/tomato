package io.github.clagomess.tomato.io.converter;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.external.PostmanCollectionV210Dto;
import io.github.clagomess.tomato.dto.external.PostmanEnvironmentDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.CollectionRepository;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.mapper.PostmanCollectionDumpMapper;
import io.github.clagomess.tomato.mapper.PostmanCollectionPumpMapper;
import io.github.clagomess.tomato.mapper.PostmanEnvironmentDumpMapper;
import io.github.clagomess.tomato.mapper.PostmanEnvironmentPumpMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static io.github.clagomess.tomato.enums.PostmanJsonSchemaEnum.COLLECTION;
import static io.github.clagomess.tomato.enums.PostmanJsonSchemaEnum.ENVIRONMENT;
import static io.github.clagomess.tomato.io.converter.JsonSchemaBuilder.getPostmanJsonSchema;

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
        validate(
                getPostmanJsonSchema(COLLECTION),
                postmanCollection
        );

        PostmanCollectionV210Dto postmanCollectionV210 = mapper.readValue(
                postmanCollection,
                PostmanCollectionV210Dto.class
        );

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
        validate(
                getPostmanJsonSchema(ENVIRONMENT),
                postmanEnvironment
        );

        PostmanEnvironmentDto postmanEnvironmentDto = mapper.readValue(
                postmanEnvironment,
                PostmanEnvironmentDto.class
        );

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

        write(postmanCollection, destination);

        validate(
                getPostmanJsonSchema(COLLECTION),
                destination
        );
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
                        throw new RuntimeException(e.getMessage(), e);
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

        write(postmanEnvironmentDto, destination);

        validate(
                getPostmanJsonSchema(ENVIRONMENT),
                destination
        );
    }

    @Override
    public String getEnvironmentDumpFileSuffix() {
        return ".postman.environment.json";
    }
}
