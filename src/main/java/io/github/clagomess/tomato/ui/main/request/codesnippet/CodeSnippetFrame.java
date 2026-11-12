package io.github.clagomess.tomato.ui.main.request.codesnippet;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.BaseFrame;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.syntaxtextarea.ScrollableTomatoRSyntaxTextArea;
import io.github.clagomess.tomato.ui.component.syntaxtextarea.TomatoRSyntaxTextArea;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class CodeSnippetFrame extends BaseFrame {
    private final CodeSnippetComboBox codeSnippetComboBox;
    private final TomatoRSyntaxTextArea textArea = new TomatoRSyntaxTextArea();

    public CodeSnippetFrame(
            Component parent,
            @Nullable RequestHeadDto requestHead,
            RequestDto request
    ) {
        codeSnippetComboBox = new CodeSnippetComboBox(requestHead);

        setTitle("Code Snippet");
        setMinimumSize(new Dimension(600, 400));

        setLayout(new MigLayout(
                "insets 10",
                "[grow,fill]"
        ));

        add(codeSnippetComboBox, "wrap");
        add(new ScrollableTomatoRSyntaxTextArea(textArea), "height 100%");

        codeSnippetComboBox.addActionListener(l -> refresh(request));

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        refresh(request);
    }

    private void refresh(RequestDto request){
        var codeSnippet = codeSnippetComboBox.getSelectedItem();
        if(codeSnippet == null){
            textArea.setText("");
            textArea.setSyntaxEditingStyle(null);
            return;
        }

        AtomicReference<String> text = new AtomicReference<>();
        new WaitExecution(this)
                .thenBackground(() -> text.set(codeSnippet.build(request)))
                .thenEdt(() -> {
                    textArea.setSyntaxEditingStyle(codeSnippet.getSyntaxStyle());
                    textArea.setText(text.get());
                    textArea.setCaretPosition(0);
                })
                .execute();
    }
}
