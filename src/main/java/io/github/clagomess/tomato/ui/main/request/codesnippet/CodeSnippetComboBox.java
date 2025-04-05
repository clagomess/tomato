package io.github.clagomess.tomato.ui.main.request.codesnippet;

import io.github.clagomess.tomato.io.snippet.CodeSnippet;
import io.github.clagomess.tomato.io.snippet.CurlSnippet;
import io.github.clagomess.tomato.ui.component.DtoListCellRenderer;

import javax.swing.*;

import static io.github.clagomess.tomato.io.snippet.CurlSnippet.Type.*;

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
