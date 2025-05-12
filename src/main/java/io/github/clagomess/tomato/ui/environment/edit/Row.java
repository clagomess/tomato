package io.github.clagomess.tomato.ui.environment.edit;

import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum;
import io.github.clagomess.tomato.io.keystore.EnvironmentKeystore;
import io.github.clagomess.tomato.ui.component.ComponentUtil;
import io.github.clagomess.tomato.ui.component.IconButton;
import io.github.clagomess.tomato.ui.component.ListenableTextField;
import io.github.clagomess.tomato.ui.component.StagingMonitor;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

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
    private final List<EnvironmentItemDto> list;
    private final EnvironmentItemDto item;
    private final EnvironmentKeystore environmentKeystore;
    private final StagingMonitor<EnvironmentItemDto> stagingMonitor;

    private final JLabel changeIcon = new JLabel(HAS_NOT_CHANGED_ICON);
    private final JComboBox<EnvironmentItemTypeEnum> cbType = new JComboBox<>(
            EnvironmentItemTypeEnum.values()
    );
    private final ListenableTextField txtKey = new ListenableTextField();
    private ValueTextField txtValue;
    private final JButton btnRemove = new IconButton(
            TRASH_ICON,
            "Remove"
    );

    public Row(
            Container parent,
            @NotNull EnvironmentKeystore environmentKeystore,
            List<EnvironmentItemDto> list,
            EnvironmentItemDto item
    ){
        this.parent = parent;
        this.list = list;
        this.item = item;
        this.environmentKeystore = environmentKeystore;
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
            onChangeType();
            updateStagingMonitor();
        });
        add(cbType, "width 80!");

        txtKey.setText(item.getKey());
        txtKey.addOnChange(value -> {
            item.setKey(value);
            updateStagingMonitor();
        });
        add(txtKey, "width 150!");

        txtValue = createTxtValue();
        add(txtValue, "width 150:150:100%");

        btnRemove.addActionListener(l -> btnRemoveAction());
        add(btnRemove);
    }

    protected ValueTextField createTxtValue(){
        return new ValueTextField(environmentKeystore, item, value -> {
            item.setValue(value);
            updateStagingMonitor();
        });
    }

    private void updateParentStagingMonitor(){
        var parent = (EnvironmentEditFrame) getAncestorOfClass(EnvironmentEditFrame.class, this);
        parent.updateStagingMonitor();
    }

    public void onChangeType() {
        txtValue.unlockSecret();
        item.setValue(txtValue.getText());

        int index = ComponentUtil.getComponentIndex(this, txtValue);
        remove(index);

        txtValue = createTxtValue();
        add(txtValue, "width 150:150:100%", index);
        revalidate();
        repaint();
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
