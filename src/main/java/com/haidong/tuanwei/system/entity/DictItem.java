package com.haidong.tuanwei.system.entity;

import lombok.Data;

@Data
public class DictItem {

    private Long id;

    private String dictType;

    private String dictLabel;

    private String dictValue;

    private Integer sortNo;
}
