package io.github.clagomess.tomato.dto.tree;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static io.github.clagomess.tomato.util.FileUtils.COLLECTION_FILES_DIR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CollectionTreeDtoTest {
    @Test
    void flattened() {
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
    void getFlattenedParentString(){
        var root = new CollectionTreeDto(){{setName("MYROOT");}};
        var level1 = new CollectionTreeDto(){{
            setName("LEVEL 1");
            setParent(root);
        }};
        var level2 = new CollectionTreeDto(){{
            setName("LEVEL 2");
            setParent(level1);
        }};

        assertEquals("ROOT - MYROOT /", root.getFlattenedParentString());
        assertEquals("LEVEL 1", level1.getFlattenedParentString());
        assertEquals("LEVEL 1 / LEVEL 2", level2.getFlattenedParentString());
    }

    @Test
    void sort(){
        var a = new CollectionTreeDto();
        a.setName("aaa");

        var b = new CollectionTreeDto();
        b.setName("bbb");

        List<CollectionTreeDto> list = new ArrayList<>(2);
        list.add(b);
        list.add(a);


        Collections.sort(list);

        assertEquals("aaa", list.get(0).getName());
    }

    @Nested
    class getCollectionFileDir {
        @Test
        void whenPathIsNull_returnNull(){
            assertNull(new CollectionTreeDto().getCollectionFileDir());
        }

        @Test
        void whenPathIsSet_returnSiblingCollectionFilesDir(@TempDir File tempDir){
            var dto = new CollectionTreeDto();
            dto.setPath(new File(tempDir, "collection-8a1c"));

            assertEquals(
                    new File(tempDir, COLLECTION_FILES_DIR).getAbsolutePath(),
                    dto.getCollectionFileDir()
            );
        }

        @Test
        void whenPathHasNoParent_returnCollectionFilesDirOnWorkingDir(){
            var dto = new CollectionTreeDto();
            dto.setPath(new File("collection-8a1c"));

            assertEquals(
                    new File(COLLECTION_FILES_DIR).getAbsolutePath(),
                    dto.getCollectionFileDir()
            );
        }
    }
}
