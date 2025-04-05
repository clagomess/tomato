package io.github.clagomess.tomato.ui.environment;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.ui.component.WaitExecution;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;

public class EnvironmentDeleteDialog {
    private final Component parent;
    private final EnvironmentDto environment;

    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    public EnvironmentDeleteDialog(
            Component parent,
            String environmentId
    ) throws IOException {
        this.parent = parent;
        this.environment = environmentRepository.load(environmentId).orElseThrow();
    }

    public void showConfirmDialog(){
        int ret = JOptionPane.showConfirmDialog(
                parent,
                String.format(
                        "Are you sure you want to delete \"%s\"?",
                        environment.getName()
                ),
                "Environment Delete",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if(ret == JOptionPane.OK_OPTION){
            delete();
        }
    }

    private void delete(){
        new WaitExecution(parent, () -> {
            environmentRepository.delete(environment);
            environmentPublisher.getOnChange()
                    .publish(new PublisherEvent<>(DELETED, environment.getId()));
        }).execute();
    }
}
