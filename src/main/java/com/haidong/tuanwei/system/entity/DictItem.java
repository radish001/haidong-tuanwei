package com.haidong.tuanwei.system.entity;

import lombok.Data;

@Data
public class DictItem {

    private Long id;

    private String dictType;

    private String dictLabel;

    private String dictValue;

    private Integer sortNo;

    /**
     * 所属学历层次（专科专业/本科专业/研究生专业，可多选，逗号分隔）
     * 仅对 major_category 类型有效
     */
    private String educationScopes;
}
