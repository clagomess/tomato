package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import com.github.clagomess.tomato.service.EnvironmentDataService;
import com.github.clagomess.tomato.service.WorkspaceSessionDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import com.github.clagomess.tomato.ui.environment.edit.EnvironmentEditUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class EnvironmentComboBox extends JPanel {
    private final ComboBox comboBox = new ComboBox();
    private final JButton btnEdit = new JButton(new BxEditIcon());

    private final EnvironmentDataService environmentDataService = new EnvironmentDataService();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();
    private final WorkspaceSessionDataService workspaceSessionDataService = new WorkspaceSessionDataService();
    private final WorkspaceSessionPublisher workspaceSessionPublisher = WorkspaceSessionPublisher.getInstance();

    public EnvironmentComboBox(){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill][]", ""));
        add(comboBox);
        add(btnEdit);

        SwingUtilities.invokeLater(this::addItens);
        environmentPublisher.getOnInsert().addListener(event -> addItens());
        workspacePublisher.getOnSwitch().addListener(event -> addItens());

        comboBox.addActionListener(event -> setWorkspaceSessionSelected());

        btnEdit.addActionListener(event -> SwingUtilities.invokeLater(() -> {
            if(comboBox.getSelectedItem() == null) return;
            new EnvironmentEditUI(this, comboBox.getSelectedItem());
        }));
    }

    private void addItens() {
        try {
            comboBox.removeAllItems();
            // @TODO: add empty option

            var session = workspaceSessionDataService.load();

            environmentDataService.list().forEach(item -> {
                comboBox.addItem(item);

                if(item.getId().equals(session.getEnvironmentId())){
                    comboBox.setSelectedItem(item);
                }
            });

            btnEdit.setEnabled(comboBox.getItemCount() > 0); //@TODO: and not empty selected
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }
    }

    private void setWorkspaceSessionSelected() {
        new WaitExecution(this, null).setExecute(() -> {
            var selected = comboBox.getSelectedItem();
            var environmentId = selected == null ? null : selected.getId();

            var session = workspaceSessionDataService.load();
            session.setEnvironmentId(environmentId);
            workspaceSessionDataService.save(session);
            workspaceSessionPublisher.getOnSave().publish(session);
        }).execute();
    }

    public static class ComboBox extends JComboBox<EnvironmentDto> {

        public ComboBox() {
            setRenderer(new HierarchyRenderer());
        }

        @Override
        public EnvironmentDto getSelectedItem() {
            return (EnvironmentDto) super.getSelectedItem();
        }
    }

    protected static class HierarchyRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
            );

            if(value != null) {
                label.setText(((EnvironmentDto) value).getName());
            }

            return label;
        }
    }
}
