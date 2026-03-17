package io.github.clagomess.tomato.io.converter;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;

import java.io.File;
import java.io.IOException;

public interface InterfaceConverter {
    String getConverterName();

    CollectionDto pumpCollection(
            File target,
            File source
    ) throws IOException;

    TomatoID pumpEnvironment(
            File source
    ) throws IOException;

    void dumpCollection(
            File target,
            CollectionTreeDto source
    ) throws IOException;

    String getCollectionDumpFileSuffix();

    void dumpEnvironment(
            File target,
            TomatoID environmentId
    ) throws IOException;

    String getEnvironmentDumpFileSuffix();
}
