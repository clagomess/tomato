package io.github.clagomess.tomato.controller.environment.list;

import io.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import io.github.clagomess.tomato.ui.environment.list.EnvironmentListInterface;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class EnvironmentListController {
    private final EnvironmentRepository environmentRepository;
    private final EnvironmentPublisher environmentPublisher;
    private final EnvironmentListInterface ui;

    public EnvironmentListController(
            EnvironmentListInterface ui
    ) {
        environmentRepository = new EnvironmentRepository();
        environmentPublisher = EnvironmentPublisher.getInstance();
        this.ui = ui;
    }

    private UUID onChangeListnerUuid;
    public void addOnChangeListner() {
        onChangeListnerUuid = environmentPublisher.getOnChange()
                .addListener(e -> ui.refresh());
    }

    public List<EnvironmentHeadDto> list() throws IOException {
        return environmentRepository.listHead();
    }

    public void dispose(){
        environmentPublisher.getOnChange()
                .removeListener(onChangeListnerUuid);
    }
}
