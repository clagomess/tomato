package com.github.clagomess.tomato.ui.main.request.right.statusbadge;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.io.http.MediaType;
import com.github.clagomess.tomato.ui.ColorConstant;

import javax.swing.*;

public class CharsetBadge extends JPanel {
    public CharsetBadge(ResponseDto.Response response) {
        if(response == null) return;

        var charset = getCharset(response.getContentType());
        if("UTF-8".equalsIgnoreCase(charset)) return;

        var color = getColor(charset);
        setBackground(color.background());

        var label = new JLabel(charset);
        label.setForeground(color.foreground());
        add(label);
    }

    private ColorConstant.Match getColor(String charset){
        if(charset == null){
            return ColorConstant.RED_MATCH;
        }else{
            return ColorConstant.ORANGE_MATCH;
        }
    }

    protected String getCharset(MediaType contentType){
        if(contentType == null) return "No Content Type";

        if(!contentType.isString() &&
                !contentType.isCompatible(MediaType.APPLICATION_JSON)){
            return contentType.toString();
        }

        return contentType.getParameters()
                .getOrDefault("charset", "UTF-8");
    }
}
