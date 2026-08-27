package io.github.clagomess.tomato.util;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class FileUtils {
    public static final String COLLECTION_FILES_DIR = "__collection_files";

    public static String humanReadableByteCountBinary(long size){
        if(size <= 1024) return size + "B";

        if(size <= Math.pow(1024, 2)){
            return new BigDecimal(size / 1024d)
                    .setScale(2, RoundingMode.HALF_UP) + "KB";
        }

        return new BigDecimal(size / Math.pow(1024, 2))
                .setScale(2, RoundingMode.HALF_UP) + "MB";
    }

    /**
     * Sums the length of the files of a directory,
     * including the ones of its subdirectories.
     */
    public static long dirSize(File dir){
        long size = 0L;

        for(File item : listFiles(dir)){
            if(item.isDirectory()){
                size += dirSize(item);
            }else{
                size += item.length();
            }
        }

        return size;
    }

    /**
     * Counts the files of a directory,
     * including the ones of its subdirectories.
     */
    public static long dirFileCount(File dir){
        long fileCount = 0L;

        for(File item : listFiles(dir)){
            if(item.isDirectory()){
                fileCount += dirFileCount(item);
            }else{
                fileCount++;
            }
        }

        return fileCount;
    }

    private static File[] listFiles(File dir){
        return Objects.requireNonNullElseGet(
                dir.listFiles(),
                () -> new File[0]
        );
    }
}
