package com.haidong.tuanwei.system.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Menu {

    private Long id;

    private Long parentId;

    private String menuName;

    private String menuPath;

    private String icon;

    private Integer sortNo;

    private Boolean visible;

    private List<Menu> children = new ArrayList<>();
}
