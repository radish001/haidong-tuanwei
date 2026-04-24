package com.haidong.tuanwei.system.enums;

import lombok.Getter;

/**
 * 专业类别所属学历层次枚举
 */
@Getter
public enum MajorCategoryEducationScope {

    JUNIOR_COLLEGE("专科专业", "JUNIOR_COLLEGE"),
    UNDERGRADUATE("本科专业", "UNDERGRADUATE"),
    GRADUATE("研究生专业", "GRADUATE");

    private final String label;
    private final String value;

    MajorCategoryEducationScope(String label, String value) {
        this.label = label;
        this.value = value;
    }

    /**
     * 根据值获取枚举
     */
    public static MajorCategoryEducationScope fromValue(String value) {
        for (MajorCategoryEducationScope scope : values()) {
            if (scope.value.equals(value)) {
                return scope;
            }
        }
        return null;
    }

    /**
     * 将逗号分隔的常量值转换为标签显示
     */
    public static String toLabelDisplay(String scopesValue) {
        if (scopesValue == null || scopesValue.isEmpty()) {
            return "";
        }
        String[] values = scopesValue.split(",");
        StringBuilder labels = new StringBuilder();
        for (String value : values) {
            MajorCategoryEducationScope scope = fromValue(value.trim());
            if (scope != null) {
                if (!labels.isEmpty()) {
                    labels.append("、");
                }
                labels.append(scope.getLabel());
            }
        }
        return labels.toString();
    }
}
