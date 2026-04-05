package com.haidong.tuanwei.system.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class School {

    private Long id;

    private String schoolName;

    private Long categoryDictItemId;

    private String categoryLabel;

    private String tagSummary;

    private List<Long> tagIds = new ArrayList<>();
}
