package com.haidong.tuanwei.system.dto;

import lombok.Data;

@Data
public class DictionaryWorkbenchQuery {

    private String tab = "common";

    private String section;

    private String keyword;

    private Long parentId;

    private Integer regionLevel;

    private Integer page = 1;

    private Integer pageSize = 10;

    public int getSafePage() {
        return page == null || page < 1 ? 1 : page;
    }

    public int getSafePageSize() {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }

    public int getOffset() {
        return (getSafePage() - 1) * getSafePageSize();
    }
}
