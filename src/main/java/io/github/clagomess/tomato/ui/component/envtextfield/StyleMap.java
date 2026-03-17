package io.github.clagomess.tomato.ui.component.envtextfield;

import javax.swing.text.StyledDocument;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface StyleMap {
    Map<String, String> injected = new HashMap<>();

    default void clearInjected(){
        injected.clear();
    }

    default Map<String, String> getInjected(){
        return injected;
    }

    void update(
            StyledDocument document,
            String text
    ) throws IOException;
}
