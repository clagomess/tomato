package com.github.clagomess.tomato.ui.environment.edit;

import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.ui.component.ComponentUtil;
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
    private final List<KeyValueItemDto> list;
    private final KeyValueItemDto item;
    private final StagingMonitor<KeyValueItemDto> stagingMonitor;

    private final BxsCircleIcon iconHasChanged = new BxsCircleIcon(Color.ORANGE);
    private final BxsCircleIcon iconHasNotChanged = new BxsCircleIcon(Color.GRAY);

    private final JLabel changeIcon = new JLabel(iconHasNotChanged);
    private final ListenableTextField txtKey = new ListenableTextField();
    private final ListenableTextField txtValue = new ListenableTextField();
    private final JButton btnRemove = new JButton(new BxTrashIcon()){{
        setToolTipText("Remove");
    }};

    public Row(
            Container parent,
            List<KeyValueItemDto> list,
            KeyValueItemDto item
    ){
        this.parent = parent;
        this.list = list;
        this.item = item;
        this.stagingMonitor = new StagingMonitor<>(item);

        if(!this.list.contains(this.item)){
            this.list.add(this.item);
        }

        // set values
        txtKey.setText(item.getKey());
        txtValue.setText(item.getValue());

        // listeners
        txtKey.addOnChange(value -> {
            item.setKey(value);
            updateStagingMonitor();
        });
        txtValue.addOnChange(value -> {
            item.setValue(value);
            updateStagingMonitor();
        });
        btnRemove.addActionListener(l -> btnRemoveAction());

        // layout
        setLayout(new MigLayout(
                "insets 2",
                "[][][grow, fill][]"
        ));
        add(changeIcon, "width 8!");
        add(txtKey, "width 150!");
        add(txtValue, "width 150:150:100%");
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
