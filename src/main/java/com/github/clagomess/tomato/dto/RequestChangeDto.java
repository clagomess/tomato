package com.github.clagomess.tomato.dto;

import com.github.clagomess.tomato.publisher.RequestPublisher;

public class RequestChangeDto {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private int currentHashCode;
    private int actualHashCode;

    public RequestChangeDto(CollectionTreeDto.Request requestHeadDto, RequestDto dto) {
        this.currentHashCode = requestHeadDto == null ? 0 : dto.hashCode();
        this.actualHashCode = dto.hashCode();
    }

    public void reset(RequestDto dto){
        this.currentHashCode = dto.hashCode();
        this.actualHashCode = dto.hashCode();
        requestPublisher.getOnChange().publish(dto.getId(), false);
    }

    public void setActualHashCode(RequestDto dto){
        this.actualHashCode = dto.hashCode();
        requestPublisher.getOnChange().publish(
                dto.getId(),
                currentHashCode != actualHashCode
        );
    }
}
