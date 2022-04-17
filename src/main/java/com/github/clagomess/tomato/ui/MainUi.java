package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.EnvironmentDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.ui.collection.CollectionUi;
import com.github.clagomess.tomato.ui.request.RequestUi;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MainUi extends JFrame {
    private final CollectionUi collectionUi;
    private final RequestUi requestUi;

    public MainUi(){
        this.collectionUi = new CollectionUi(this);
        this.requestUi = new RequestUi(this);

        mock();
        setTitle("Tomato");
        setVisible(true);
        setMinimumSize(new Dimension(1000, 500));
        setJMenuBar(getMenu());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, collectionUi, requestUi);
        splitPane.setDividerLocation(200);

        add(splitPane);
        pack();
    }

    public JMenuBar getMenu(){
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu("File"));
        menuBar.add(new JMenu("Workspace"));
        menuBar.add(new JMenu("Collection"));
        menuBar.add(new JMenu("Environment"));

        return menuBar;
    }

    private void mock(){
        List<EnvironmentDto> environmentDtoList = new ArrayList<>();
        environmentDtoList.add(new EnvironmentDto("Desenvolvimento"));
        environmentDtoList.add(new EnvironmentDto("Homologação"));
        environmentDtoList.add(new EnvironmentDto("Produção"));

        this.collectionUi.setEnvironments(environmentDtoList);

        List<RequestDto> requestDtos = new ArrayList<>();
        requestDtos.add(new RequestDto("/api/fooo"));
        requestDtos.add(new RequestDto("/api/bar"));
        requestDtos.add(new RequestDto("/api/aboa"));

        List<CollectionDto> collectionDtos = new ArrayList<>();
        collectionDtos.add(new CollectionDto("FOO", requestDtos));
        collectionDtos.add(new CollectionDto("BAR", requestDtos));
        this.collectionUi.setCollections(collectionDtos);
    }
}
