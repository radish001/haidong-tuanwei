package com.haidong.tuanwei.system.entity;

import lombok.Data;

@Data
public class MajorCatalog {

    private Long id;

    private String majorName;

    private Long categoryDictItemId;

    private String categoryLabel;
}
