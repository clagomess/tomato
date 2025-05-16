package io.github.clagomess.tomato.ui.main.request.keyvalue;

import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.publisher.DisposableListener;
import io.github.clagomess.tomato.ui.component.ColorConstant;
import io.github.clagomess.tomato.ui.component.ComponentUtil;
import io.github.clagomess.tomato.ui.component.IconButton;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSortAZIcon;
import io.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.util.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class KeyValue<T extends KeyValueItemDto> extends JPanel implements DisposableListener {
    private static final Icon SORT_AZ_ICON = new BxSortAZIcon();
    private static final Icon PLUS_ICON = new BxPlusIcon();

    private final Class<T> itemClass;
    private final List<T> listItens;
    private final RequestStagingMonitor requestStagingMonitor;
    private final IconButton btnSortByKey = new IconButton(SORT_AZ_ICON, "Sort by Key");
    private final IconButton btnAddNew = new IconButton(PLUS_ICON, "Add new");
    private final JPanel rowsPanel;
    private final KeyValueOptions options;

    public KeyValue(
            List<T> listItens,
            Class<T> itemClass,
            RequestStagingMonitor requestStagingMonitor
    ) {
        this(
                listItens,
                itemClass,
                requestStagingMonitor,
                KeyValueOptions.builder().build()
        );
    }

    public KeyValue(
            List<T> listItens,
            Class<T> itemClass,
            RequestStagingMonitor requestStagingMonitor,
            KeyValueOptions options
    ){
        ComponentUtil.checkIsEventDispatchThread();

        this.itemClass = itemClass;
        this.listItens = listItens;
        this.requestStagingMonitor = requestStagingMonitor;
        this.options = options;

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]"
        ));

        // ### HEADER
        JPanel header = new JPanel(new MigLayout("insets 5 0 5 0"));
        header.setBorder(new MatteBorder(0, 0, 1, 0, ColorConstant.GRAY));
        header.add(new JLabel(), "width 25!");

        if(itemClass == FileKeyValueItemDto.class) {
            header.add(new JLabel("Type"), "width 70!");
        }

        header.add(new JLabel("Key"), "width 100!");
        header.add(new JLabel("Value"), "grow, width 100%");

        if(options.getCharsetComboBox() != null){
            header.add(options.getCharsetComboBox());
        }

        header.add(btnSortByKey);
        header.add(btnAddNew);

        add(header, "width 100%, wrap");

        // ### ROWS
        rowsPanel = new JPanel(new MigLayout(
                "insets 0 0 0 0",
                "[grow,fill]"
        ));
        JScrollPane scrollPane = new JScrollPane(
                rowsPanel,
                VERTICAL_SCROLLBAR_AS_NEEDED,
                HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, "width ::100%, height 100%");

        btnSortByKey.addActionListener(e -> sortByKey());
        btnAddNew.addActionListener(l -> addNewRow(null, null));

        this.listItens.forEach(this::addRow);
    }

    private void addNewRow(String key, String value){
        try {
            var item = itemClass.getDeclaredConstructor().newInstance();
            item.setKey(key);
            item.setValue(value);

            addRow(item);
            options.getOnChange().run(item);
            requestStagingMonitor.update();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void addRow(T item){
        var row = new RowComponent<>(
                rowsPanel,
                this.requestStagingMonitor,
                this.listItens,
                item,
                options
        );

        rowsPanel.add(row, "wrap 4");
        rowsPanel.revalidate();
        rowsPanel.repaint();
    }

    private void sortByKey(){
        dispose();
        Collections.sort(listItens);
        rowsPanel.removeAll();
        listItens.forEach(this::addRow);
        requestStagingMonitor.update();
    }

    public void update(String key, String value){
        Optional<? extends RowComponent<?>> result = Arrays.stream(rowsPanel.getComponents())
                .filter(row -> row instanceof RowComponent)
                .map(row -> (RowComponent<?>) row)
                .filter(row -> Objects.equals(key, row.getItem().getKey()))
                .findFirst();

        if(result.isPresent()){
            result.get().getCValue().setValue(value);
        }else{
            addNewRow(key, value);
        }
    }

    public void dispose(){
        Arrays.stream(rowsPanel.getComponents())
                .filter(row -> row instanceof RowComponent)
                .map(row -> (RowComponent<?>) row)
                .forEach(RowComponent::dispose);
    }
}
