package com.tanggo.odata4.model;

public enum OrderStatus {
    DRAFT("草稿"),
    PENDING("待处理"),
    PROCESSING("处理中"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 