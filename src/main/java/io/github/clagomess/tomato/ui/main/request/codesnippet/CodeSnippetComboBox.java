package io.github.clagomess.tomato.ui.main.request.codesnippet;

import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.snippet.CodeSnippet;
import io.github.clagomess.tomato.io.snippet.CurlSnippet;
import io.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import org.jspecify.annotations.Nullable;

import javax.swing.*;

import static io.github.clagomess.tomato.io.snippet.CurlSnippet.Type.*;

class CodeSnippetComboBox extends JComboBox<CodeSnippet> {
    public CodeSnippetComboBox(@Nullable RequestHeadDto requestHead) {
        setRenderer(new DtoListCellRenderer<>(CodeSnippet::getName));
        addItem(new CurlSnippet(requestHead, BASH));
        addItem(new CurlSnippet(requestHead, POWERSHELL));
        addItem(new CurlSnippet(requestHead, CMD));
    }

    @Override
    public CodeSnippet getSelectedItem() {
        return (CodeSnippet) super.getSelectedItem();
    }
}
