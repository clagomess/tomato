package io.github.clagomess.tomato.ui.environment.edit;

import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.ui.component.ComponentUtil;
import io.github.clagomess.tomato.ui.component.IconButton;
import io.github.clagomess.tomato.ui.component.ListenableTextField;
import io.github.clagomess.tomato.ui.component.StagingMonitor;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
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
    private static final Icon HAS_CHANGED_ICON = new BxsCircleIcon(Color.ORANGE);
    private static final Icon HAS_NOT_CHANGED_ICON = new BxsCircleIcon(Color.GRAY);
    private static final Icon TRASH_ICON = new BxTrashIcon();

    private final Container parent;
    private final List<KeyValueItemDto> list;
    private final KeyValueItemDto item;
    private final StagingMonitor<KeyValueItemDto> stagingMonitor;

    private final JLabel changeIcon = new JLabel(HAS_NOT_CHANGED_ICON);
    private final ListenableTextField txtKey = new ListenableTextField();
    private final ListenableTextField txtValue = new ListenableTextField();
    private final JButton btnRemove = new IconButton(
            TRASH_ICON,
            "Remove"
    );

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
            invokeLater(() -> changeIcon.setIcon(HAS_CHANGED_ICON));
        }else{
            invokeLater(() -> changeIcon.setIcon(HAS_NOT_CHANGED_ICON));
        }
    }

    public void resetStagingMonitor(){
        stagingMonitor.reset();
        invokeLater(() -> changeIcon.setIcon(HAS_NOT_CHANGED_ICON));
    }
}
