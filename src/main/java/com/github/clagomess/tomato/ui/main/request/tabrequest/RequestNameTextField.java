package com.github.clagomess.tomato.ui.main.request.tabrequest;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.ui.component.ListenableTextField;

import javax.swing.*;
import java.awt.*;

public class RequestNameTextField extends JPanel {
    private final JLabel parent = new JLabel();
    private final ListenableTextField txtRequestName = new ListenableTextField();

    public RequestNameTextField(){
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

    public void addOnChange(ListenableTextField.OnChangeFI value){
        txtRequestName.addOnChange(value);
    }
}
