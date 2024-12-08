package com.github.clagomess.tomato.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionTreeDtoTest {
    @Test
    public void flattened() {
        var root = new CollectionTreeDto(){{
            setName("ROOT");
            setChildren(Stream.of(
                    new CollectionTreeDto(){{
                        setName("LEVEL 1 - A");
                        setChildren(Stream.empty());
                    }},
                    new CollectionTreeDto(){{
                        setName("LEVEL 1 - B");
                        setChildren(Stream.of(
                                new CollectionTreeDto(){{
                                    setName("LEVEL 2 - A");
                                    setChildren(Stream.empty());
                                }},
                                new CollectionTreeDto(){{
                                    setName("LEVEL 2 - B");
                                    setChildren(Stream.of(
                                            new CollectionTreeDto(){{
                                                setName("LEVEL 3 - A");
                                                setChildren(Stream.empty());
                                            }}
                                    ));
                                }}
                        ));
                    }}
            ));
        }};

        var result = root.flattened().toList();

        Assertions.assertThat(result).hasSize(6);
    }

    @Test
    public void flattenedParentString(){
        var root = new CollectionTreeDto(){{setName("ROOT");}};
        var level1 = new CollectionTreeDto(){{
            setName("LEVEL 1");
            setParent(root);
        }};
        var level2 = new CollectionTreeDto(){{
            setName("LEVEL 2");
            setParent(level1);
        }};

        assertEquals("ROOT", root.flattenedParentString());
        assertEquals("ROOT / LEVEL 1", level1.flattenedParentString());
        assertEquals("ROOT / LEVEL 1 / LEVEL 2", level2.flattenedParentString());
    }
}
