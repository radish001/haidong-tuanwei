package com.haidong.tuanwei.system.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Region {

    private Long id;

    private Long parentId;
    private String parentName;

    private String regionCode;

    private String regionName;

    private Integer regionLevel;

    private Integer sortNo;

    private String fullName;

    private List<Region> children = new ArrayList<>();
}
