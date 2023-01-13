package com.github.clagomess.tomato.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.clagomess.tomato.mapper.RequestDtoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CollectionDto extends TomatoMetadataDto {
    private String name;

    @JsonIgnore
    private List<RequestDto> requests = new ArrayList<>();

    public void addOrReplaceRequest(RequestDto requestDto){
        if(requests.stream().anyMatch(item -> item.getId().equals(requestDto.getId()))){
            requests.stream()
                    .filter(item -> item.getId().equals(requestDto.getId()))
                    .findFirst()
                    .ifPresent(item -> RequestDtoMapper.INSTANCE.copy(item, requestDto));
        }else{
            requests.add(requestDto);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
