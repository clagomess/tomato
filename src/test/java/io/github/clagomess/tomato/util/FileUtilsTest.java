package io.github.clagomess.tomato.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileUtilsTest {
    @ParameterizedTest
    @CsvSource({
            "100,100B",
            "10240,10.00KB",
            "2048576,1.95MB",
    })
    void humanReadableByteCountBinary(
            Long size,
            String expected
    ){
        var result = FileUtils.humanReadableByteCountBinary(size);
        assertEquals(expected, result);
    }

    private void createFiles(File dir) throws IOException {
        Files.write(new File(dir, "collection-AAAAAAAA.json").toPath(), new byte[100]);
        Files.write(new File(dir, "request-BBBBBBBB.json").toPath(), new byte[200]);

        var subDir = new File(dir, "collection-CCCCCCCC");
        assertTrue(subDir.mkdirs());
        Files.write(new File(subDir, "collection-CCCCCCCC.json").toPath(), new byte[212]);
    }

    @Nested
    class dirSize {
        @Test
        void whenHasSubDir_sumAllFiles(@TempDir File dir) throws IOException {
            createFiles(dir);
            assertEquals(512L, FileUtils.dirSize(dir));
        }

        @Test
        void whenDirIsEmpty_returnZero(@TempDir File dir) {
            assertEquals(0L, FileUtils.dirSize(dir));
        }

        @Test
        void whenDirNotExists_returnZero() {
            assertEquals(0L, FileUtils.dirSize(new File("not-exists")));
        }
    }

    @Nested
    class dirFileCount {
        @Test
        void whenHasSubDir_countAllFiles(@TempDir File dir) throws IOException {
            createFiles(dir);
            assertEquals(3L, FileUtils.dirFileCount(dir));
        }

        @Test
        void whenDirIsEmpty_returnZero(@TempDir File dir) {
            assertEquals(0L, FileUtils.dirFileCount(dir));
        }

        @Test
        void whenDirNotExists_returnZero() {
            assertEquals(0L, FileUtils.dirFileCount(new File("not-exists")));
        }
    }
}
