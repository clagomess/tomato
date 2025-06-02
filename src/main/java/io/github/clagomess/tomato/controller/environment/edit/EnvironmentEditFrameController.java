package io.github.clagomess.tomato.controller.environment.edit;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.keystore.EnvironmentKeystore;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.ui.environment.edit.EnvironmentEditFrameInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.SECRET;
import static io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.TEXT;
import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;

@RequiredArgsConstructor
public class EnvironmentEditFrameController {
    private final EnvironmentRepository environmentRepository;
    private final WorkspaceRepository workspaceRepository;

    @Getter
    private final EnvironmentKeystore environmentKeystore;
    private final EnvironmentPublisher environmentPublisher;
    private final EnvironmentEditFrameInterface ui;

    @Getter
    private final EnvironmentDto environment;

    public EnvironmentEditFrameController(
            String environmentId,
            EnvironmentEditFrameInterface ui
    ) throws IOException {
        this.ui = ui;
        this.environmentRepository = new EnvironmentRepository();
        this.workspaceRepository = new WorkspaceRepository();
        this.environmentPublisher = EnvironmentPublisher.getInstance();
        this.environment = environmentRepository.load(environmentId).orElseThrow();
        this.environmentKeystore = getEnvironmentKeystore(environmentId);
    }

    private EnvironmentKeystore getEnvironmentKeystore(String environmentId) {
        try {
            File workspacePath = workspaceRepository.getDataSessionWorkspace()
                    .getPath();

            var environmentKeystore = new EnvironmentKeystore(workspacePath, environmentId);
            environmentKeystore.setGetPassword(ui::getPassword);
            environmentKeystore.setGetNewPassword(ui::getNewPassword);

            return environmentKeystore;
        }catch (IOException e){
            throw new TomatoException(e);
        }
    }

    public void save() throws IOException {
        environment.getEnvs()
                .removeIf(item -> StringUtils.isBlank(item.getKey()));

        List<EnvironmentKeystore.Entry> secretsToSave = environment.getEnvs().stream()
                .filter(item -> item.getType().equals(SECRET))
                .filter(item -> item.getValue() != null)
                .map(EnvironmentKeystore.Entry::new)
                .toList();

        environmentKeystore.saveEntries(secretsToSave).forEach(entry ->
                environment.getEnvs().stream()
                        .filter(env -> env.getKey().equals(entry.getKey()))
                        .findFirst()
                        .ifPresent(env -> {
                            env.setSecretId(entry.getEntryId());
                            env.setValue(null);
                        })
        );

        environment.getEnvs().stream()
                .filter(item -> item.getType().equals(SECRET))
                .filter(item -> item.getSecretId() == null)
                .forEach(item -> item.setType(TEXT));

        environmentRepository.save(environment);

        ui.resetStagingMonitor();

        environmentPublisher.getOnChange()
                .publish(new PublisherEvent<>(UPDATED, environment.getId()));
    }
}
