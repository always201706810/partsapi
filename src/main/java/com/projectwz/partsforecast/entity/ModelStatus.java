package com.projectwz.partsforecast.entity; // 或 enums 包

public enum ModelStatus {
    PENDING(0, "待训练"),
    TRAINING(1, "训练中"),
    COMPLETED(2, "已完成"),
    FAILED(3, "失败");

    private final int code;
    private final String description;

    ModelStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ModelStatus fromCode(int code) {
        for (ModelStatus status : ModelStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
}