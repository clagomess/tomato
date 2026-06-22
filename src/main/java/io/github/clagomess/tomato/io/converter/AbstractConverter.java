package io.github.clagomess.tomato.io.converter;

import io.github.clagomess.tomato.io.repository.CollectionRepository;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.Error;
import com.networknt.schema.Schema;
import lombok.RequiredArgsConstructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
            Schema schema,
            File target
    ) throws IOException {
        List<Error> validations  = schema.validate(
                mapper.readTree(target)
        );

        if(!validations.isEmpty()) throw new IOException(validations.toString());
    }
}
