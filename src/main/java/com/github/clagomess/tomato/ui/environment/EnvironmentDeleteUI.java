package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.ui.component.WaitExecution;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;

public class EnvironmentDeleteUI {
    private final Component parent;
    private final EnvironmentDto environment;

    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    public EnvironmentDeleteUI(
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
