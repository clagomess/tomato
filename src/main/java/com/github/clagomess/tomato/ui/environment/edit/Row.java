package com.github.clagomess.tomato.ui.environment.edit;

import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum;
import com.github.clagomess.tomato.ui.component.ComponentUtil;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
import com.github.clagomess.tomato.ui.component.StagingMonitor;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static javax.swing.SwingUtilities.getAncestorOfClass;
import static javax.swing.SwingUtilities.invokeLater;

@Getter
@Setter
class Row extends JPanel {
    private final Container parent;
    private final List<EnvironmentItemDto> list;
    private final EnvironmentItemDto item;
    private final StagingMonitor<EnvironmentItemDto> stagingMonitor;

    private final BxsCircleIcon iconHasChanged = new BxsCircleIcon(Color.ORANGE);
    private final BxsCircleIcon iconHasNotChanged = new BxsCircleIcon(Color.GRAY);

    private final JLabel changeIcon = new JLabel(iconHasNotChanged);
    private final JComboBox<EnvironmentItemTypeEnum> cbType = new JComboBox<>(
            EnvironmentItemTypeEnum.values()
    );
    private final ListenableTextField txtKey = new ListenableTextField();
    private final ValueTextField txtValue;
    private final JButton btnRemove = new IconButton(new BxTrashIcon(), "Remove");

    public Row(
            Container parent,
            String environmentId,
            List<EnvironmentItemDto> list,
            EnvironmentItemDto item
    ){
        this.parent = parent;
        this.list = list;
        this.item = item;
        this.stagingMonitor = new StagingMonitor<>(item);

        if(!this.list.contains(this.item)){
            this.list.add(this.item);
        }

        setLayout(new MigLayout(
                "insets 2",
                "[][][][grow, fill][]"
        ));

        // set values & add layout
        add(changeIcon, "width 8!");

        cbType.setSelectedItem(item.getType());
        cbType.addActionListener(e -> {
            item.setType((EnvironmentItemTypeEnum) cbType.getSelectedItem());
            updateStagingMonitor();
        });
        add(cbType, "width 70!");

        txtKey.setText(item.getKey());
        txtKey.addOnChange(value -> {
            item.setKey(value);
            updateStagingMonitor();
        });
        add(txtKey, "width 150!");

        txtValue = new ValueTextField(environmentId, item, value -> {
            item.setValue(value);
            updateStagingMonitor();
        });
        add(txtValue, "width 150:150:100%");

        btnRemove.addActionListener(l -> btnRemoveAction());
        add(btnRemove);
    }

    private void updateParentStagingMonitor(){
        var parent = (EnvironmentEditFrame) getAncestorOfClass(EnvironmentEditFrame.class, this);
        parent.updateStagingMonitor();
    }

    private void btnRemoveAction(){
        list.remove(item);
        parent.remove(ComponentUtil.getComponentIndex(parent, this));
        parent.revalidate();
        parent.repaint();
        updateParentStagingMonitor();
    }

    private void updateStagingMonitor(){
        stagingMonitor.update();
        updateParentStagingMonitor();

        if(stagingMonitor.isDiferent()){
            invokeLater(() -> changeIcon.setIcon(iconHasChanged));
        }else{
            invokeLater(() -> changeIcon.setIcon(iconHasNotChanged));
        }
    }

    public void resetStagingMonitor(){
        stagingMonitor.reset();
        invokeLater(() -> changeIcon.setIcon(iconHasNotChanged));
    }
}
