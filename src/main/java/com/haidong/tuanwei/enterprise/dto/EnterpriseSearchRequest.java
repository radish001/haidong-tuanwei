package com.haidong.tuanwei.enterprise.dto;

import lombok.Data;

@Data
public class EnterpriseSearchRequest {

    private String enterpriseName;
    private String industry;
    private String enterpriseNature;
    private String enterpriseScale;
    private String regionProvinceCode;
    private String regionCityCode;
    private String regionCountyCode;
    private Integer page = 1;
    private Integer pageSize = 10;
    private Boolean paged = true;

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
