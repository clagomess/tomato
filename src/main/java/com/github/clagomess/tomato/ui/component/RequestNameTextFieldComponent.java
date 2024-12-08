package com.github.clagomess.tomato.ui.component;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.dto.RequestDto;

import javax.swing.*;
import java.awt.*;

public class RequestNameTextFieldComponent extends JPanel {
    private final JLabel parent = new JLabel();
    private final ListenableTextFieldComponent txtRequestName = new ListenableTextFieldComponent();

    public RequestNameTextFieldComponent(){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(parent);
        add(txtRequestName);
    }

    public void setText(
            CollectionTreeDto.Request requestHeadDto,
            RequestDto requestDto
    ){
        if(requestHeadDto != null && requestHeadDto.getParent() != null){
            parent.setText(requestHeadDto.getParent().flattenedParentString());
        }

        txtRequestName.setText(requestDto.getName());
    }

    public void addOnChange(ListenableTextFieldComponent.OnChangeFI value){
        txtRequestName.addOnChange(value);
    }
}
