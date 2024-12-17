package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.service.EnvironmentDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class EnvironmentComboBox extends JPanel {
    private final ComboBox comboBox = new ComboBox();
    private final JButton btnEdit = new JButton(new BxEditIcon());

    private final EnvironmentDataService environmentDataService = EnvironmentDataService.getInstance();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    public EnvironmentComboBox(){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill][]", ""));
        add(comboBox);
        add(btnEdit);

        SwingUtilities.invokeLater(this::addItens);
        environmentPublisher.getOnInsert().addListener(event -> addItens());
        workspacePublisher.getOnSwitch().addListener(event -> addItens());

        // @TODO: combobox add event on change to set session selected
    }

    private void addItens() {
        try {
            comboBox.removeAllItems();
            environmentDataService.list().forEach(comboBox::addItem);
            btnEdit.setEnabled(comboBox.getItemCount() > 0);

            // @TODO: set session selected item
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }
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
