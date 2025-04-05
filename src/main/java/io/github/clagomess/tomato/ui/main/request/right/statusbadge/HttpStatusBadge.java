package io.github.clagomess.tomato.ui.main.request.right.statusbadge;

import io.github.clagomess.tomato.dto.ResponseDto;
import io.github.clagomess.tomato.ui.component.ColorConstant;

import javax.swing.*;

public class HttpStatusBadge extends JPanel {
    public HttpStatusBadge(ResponseDto.Response response) {
        var color = getColor(response.getStatus());
        setBackground(color.background());

        var label = new JLabel(String.format(
                "%s %s",
                response.getStatus(),
                response.getStatusReason()
        ));
        label.setForeground(color.foreground());
        add(label);
    }

    private ColorConstant.Match getColor(int status){
        if(status >= 100 && status <= 199) return ColorConstant.PURPLE_MATCH;
        if(status >= 200 && status <= 299) return ColorConstant.GREEN_MATCH;
        if(status >= 300 && status <= 399) return ColorConstant.YELLOW_MATCH;
        if(status >= 400 && status <= 499) return ColorConstant.ORANGE_MATCH;
        return ColorConstant.RED_MATCH;
    }
}
