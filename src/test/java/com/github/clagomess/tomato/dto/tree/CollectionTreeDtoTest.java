package com.github.clagomess.tomato.dto.tree;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionTreeDtoTest {
    @Test
    public void flattened() {
        var root = new CollectionTreeDto(){{
            setName("ROOT");
            setChildren(parent -> Stream.of(
                    new CollectionTreeDto(){{
                        setName("LEVEL 1 - A");
                    }},
                    new CollectionTreeDto(){{
                        setName("LEVEL 1 - B");
                        setChildren(parent -> Stream.of(
                                new CollectionTreeDto(){{
                                    setName("LEVEL 2 - A");
                                }},
                                new CollectionTreeDto(){{
                                    setName("LEVEL 2 - B");
                                    setChildren(parent -> Stream.of(
                                            new CollectionTreeDto(){{
                                                setName("LEVEL 3 - A");
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
    public void getFlattenedParentString(){
        var root = new CollectionTreeDto(){{setName("ROOT");}};
        var level1 = new CollectionTreeDto(){{
            setName("LEVEL 1");
            setParent(root);
        }};
        var level2 = new CollectionTreeDto(){{
            setName("LEVEL 2");
            setParent(level1);
        }};

        assertEquals("ROOT", root.getFlattenedParentString());
        assertEquals("ROOT / LEVEL 1", level1.getFlattenedParentString());
        assertEquals("ROOT / LEVEL 1 / LEVEL 2", level2.getFlattenedParentString());
    }
}
