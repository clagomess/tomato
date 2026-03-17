package io.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.regex.Pattern;

@EqualsAndHashCode
@JsonSerialize(converter = TomatoID.Serializer.class)
public class TomatoID implements Serializable, Comparable<TomatoID> {
    private final String id;

    public TomatoID(@NotNull String id) {
        var pattern = Pattern.compile("^[a-zA-Z0-9]{8}$");
        if(!pattern.matcher(id).find()) throw new IllegalArgumentException("Invalid id:" + id);
        this.id = id;
    }

    public TomatoID(){
        this.id = RandomStringUtils.secure().nextAlphanumeric(8);
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int compareTo(@NotNull TomatoID o) {
        return StringUtils.compareIgnoreCase(this.id, o.id, true);
    }

    public static class Serializer extends StdConverter<TomatoID, String> {
        @Override
        public String convert(TomatoID tid) {
            return tid != null ? tid.id : null;
        }
    }
}
