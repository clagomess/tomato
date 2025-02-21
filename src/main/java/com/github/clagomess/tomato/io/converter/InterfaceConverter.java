package com.github.clagomess.tomato.io.converter;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;

import java.io.File;
import java.io.IOException;

public interface InterfaceConverter {
    String getConverterName();

    CollectionDto pumpCollection(
            File target,
            File source
    ) throws IOException;

    String pumpEnvironment(
            File source
    ) throws IOException;

    void dumpCollection(
            File target,
            CollectionTreeDto source
    ) throws IOException;

    String getCollectionDumpFileSuffix();

    void dumpEnvironment(
            File target,
            String environmentId
    ) throws IOException;

    String getEnvironmentDumpFileSuffix();
}
