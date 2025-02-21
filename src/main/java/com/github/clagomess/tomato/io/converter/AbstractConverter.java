package com.github.clagomess.tomato.io.converter;

import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractConverter implements InterfaceConverter {
    protected final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();
    protected final CollectionRepository collectionRepository;
    protected final RequestRepository requestRepository;
    protected final EnvironmentRepository environmentRepository;

    public AbstractConverter() {
        collectionRepository = new CollectionRepository();
        requestRepository = new RequestRepository();
        environmentRepository = new EnvironmentRepository();
    }

    protected <T> void write(
            T object,
            File target
    ) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(target))) {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(bw, object);
        }
    }

    protected void validate(
            JsonSchema schema,
            File target
    ) throws IOException {
        Set<ValidationMessage> validations  = schema.validate(
                mapper.readTree(target)
        );

        if(!validations.isEmpty()) throw new IOException(validations.toString());
    }
}
