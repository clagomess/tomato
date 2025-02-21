package com.github.clagomess.tomato.io.converter;

import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;

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
}
