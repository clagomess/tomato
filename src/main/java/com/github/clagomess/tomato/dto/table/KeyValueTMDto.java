package com.github.clagomess.tomato.dto.table;

import com.github.clagomess.tomato.ui.component.tablemanager.ModelColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KeyValueTMDto {
    @ModelColumn(name = "Key")
    private String key;

    @ModelColumn(name = "Value")
    private String value;
}
