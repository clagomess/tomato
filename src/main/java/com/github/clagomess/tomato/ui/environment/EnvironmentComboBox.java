package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import com.github.clagomess.tomato.ui.environment.edit.EnvironmentEditUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Objects;

import static javax.swing.SwingUtilities.invokeLater;

public class EnvironmentComboBox extends JPanel {
    private final ComboBox comboBox = new ComboBox();
    private final JButton btnEdit = new JButton(new BxEditIcon()){{
        setToolTipText("Edit Environment");
    }};

    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();
    private final WorkspaceSessionRepository workspaceSessionRepository = new WorkspaceSessionRepository();
    private final WorkspaceSessionPublisher workspaceSessionPublisher = WorkspaceSessionPublisher.getInstance();

    public EnvironmentComboBox(){
        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill][]"
        ));
        add(comboBox, "width ::100% - 32px");
        add(btnEdit);

        new Thread(this::addItens, getClass().getSimpleName()).start();

        environmentPublisher.getOnInsert().addListener(event -> addItens());
        workspacePublisher.getOnSwitch().addListener(event -> addItens());

        comboBox.addActionListener(event -> setWorkspaceSessionSelected());

        btnEdit.addActionListener(event -> {
            setBtnEditEnabledOrDisabled();
            if(comboBox.getSelectedItem() == null) return;

            invokeLater(() -> new EnvironmentEditUI(this, comboBox.getSelectedItem()));
        });
    }

    private void addItens() {
        try {
            invokeLater(comboBox::removeAllItems);

            var session = workspaceSessionRepository.load();

            environmentRepository.list().forEach(item -> {
                invokeLater(() -> comboBox.addItem(item));

                if(item.getId().equals(session.getEnvironmentId())){
                    invokeLater(() -> comboBox.setSelectedItem(item));
                }
            });

            setBtnEditEnabledOrDisabled();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }
    }

    private void setBtnEditEnabledOrDisabled(){
        invokeLater(() -> btnEdit.setEnabled(
                comboBox.getItemCount() > 0 &&
                comboBox.getSelectedItem() != null
        ));
    }

    private void setWorkspaceSessionSelected() {
        new WaitExecution(this, () -> {
            var selected = comboBox.getSelectedItem();
            var environmentId = selected == null ? null : selected.getId();

            var session = workspaceSessionRepository.load();

            if(Objects.equals(session.getEnvironmentId(), environmentId)) return;

            session.setEnvironmentId(environmentId);

            workspaceSessionRepository.save(session);
            workspaceSessionPublisher.getOnSave().publish(session);
        }).execute();
    }

    public static class ComboBox extends JComboBox<EnvironmentDto> {

        public ComboBox() {
            setRenderer(new DtoListCellRenderer<>(EnvironmentDto::getName));
        }

        @Override
        public EnvironmentDto getSelectedItem() {
            return (EnvironmentDto) super.getSelectedItem();
        }
    }
}
