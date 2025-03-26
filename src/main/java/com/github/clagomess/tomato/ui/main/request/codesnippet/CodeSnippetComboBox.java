package com.github.clagomess.tomato.ui.main.request.codesnippet;

import com.github.clagomess.tomato.io.snippet.CodeSnippet;
import com.github.clagomess.tomato.io.snippet.CurlSnippet;
import com.github.clagomess.tomato.ui.component.DtoListCellRenderer;

import javax.swing.*;

import static com.github.clagomess.tomato.io.snippet.CurlSnippet.Type.*;

class CodeSnippetComboBox extends JComboBox<CodeSnippet> {
    public CodeSnippetComboBox() {
        setRenderer(new DtoListCellRenderer<>(CodeSnippet::getName));
        addItem(new CurlSnippet(BASH));
        addItem(new CurlSnippet(POWERSHELL));
        addItem(new CurlSnippet(CMD));
    }

    @Override
    public CodeSnippet getSelectedItem() {
        return (CodeSnippet) super.getSelectedItem();
    }
}
