package com.github.clagomess.tomato.ui.component;

public class StagingMonitor<T> {
    protected final T dto;
    protected int currentHashCode;
    protected int actualHashCode;

    public StagingMonitor(
            T dto
    ) {
        this.dto = dto;
        this.currentHashCode = dto.hashCode();
        this.actualHashCode = dto.hashCode();
    }

    public void reset(){
        this.currentHashCode = dto.hashCode();
        this.actualHashCode = dto.hashCode();
    }

    public void update(){
        int newHashCode = dto.hashCode();
        if(this.actualHashCode == newHashCode) return;

        this.actualHashCode = newHashCode;
    }

    public boolean isDiferent(){
        return this.actualHashCode != currentHashCode;
    }
}
