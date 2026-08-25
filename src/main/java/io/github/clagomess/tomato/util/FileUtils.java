package io.github.clagomess.tomato.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FileUtils {
    public static String humanReadableByteCountBinary(long size){
        if(size <= 1024) return size + "B";

        if(size <= Math.pow(1024, 2)){
            return new BigDecimal(size / 1024d)
                    .setScale(2, RoundingMode.HALF_UP) + "KB";
        }

        return new BigDecimal(size / Math.pow(1024, 2))
                .setScale(2, RoundingMode.HALF_UP) + "MB";
    }
}
