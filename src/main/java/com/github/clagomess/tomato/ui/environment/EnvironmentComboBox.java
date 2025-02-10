package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import com.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import com.github.clagomess.tomato.ui.environment.edit.EnvironmentEditUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;

import static javax.swing.SwingUtilities.invokeLater;

public class EnvironmentComboBox extends JPanel {
    private final ComboBox comboBox = new ComboBox();
    private final JButton btnEdit = new IconButton(new BxEditIcon(), "Edit Environment");

    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();
    private final WorkspaceSessionRepository workspaceSessionRepository = new WorkspaceSessionRepository();
    private final WorkspaceSessionPublisher workspaceSessionPublisher = WorkspaceSessionPublisher.getInstance();

    public EnvironmentComboBox(){
        setLayout(new MigLayout(
                "insets 2",
                "[grow, fill][]"
        ));
        add(comboBox, "width ::100% - 32px");
        add(btnEdit);

        invokeLater(this::addItens);

        EnvironmentPublisher.getInstance()
                .getOnChange()
                .addListener(event -> addItens());
        WorkspacePublisher.getInstance()
                .getOnSwitch()
                .addListener(event -> addItens());

        btnEdit.addActionListener(event -> {
            setBtnEditEnabledOrDisabled();
            if(comboBox.getSelectedItem() == null) return;

            invokeLater(() -> new EnvironmentEditUI(this, comboBox.getSelectedItem()));
        });
    }

    private void addItens() {
        Arrays.stream(comboBox.getActionListeners()).forEach(comboBox::removeActionListener);
        comboBox.removeAllItems();

        invokeLater(() -> {
            try {
                var session = workspaceSessionRepository.load();
                var list = environmentRepository.list();

                invokeLater(() -> {
                    comboBox.addItem(null);
                    comboBox.setSelectedItem(null);

                    list.forEach(item -> {
                        comboBox.addItem(item);

                        if(item.getId().equals(session.getEnvironmentId())){
                            comboBox.setSelectedItem(item);
                        }
                    });

                    invokeLater(() -> {
                        setBtnEditEnabledOrDisabled();
                        comboBox.addActionListener(event -> setWorkspaceSessionSelected());
                    });
                });
            } catch (Throwable e){
                new ExceptionDialog(this, e);
            }
        });
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

            setBtnEditEnabledOrDisabled();
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
