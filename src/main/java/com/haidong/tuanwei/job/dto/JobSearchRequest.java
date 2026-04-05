package com.haidong.tuanwei.job.dto;

import lombok.Data;

@Data
public class JobSearchRequest {

    private String jobName;
    private Long enterpriseId;
    private String educationRequirement;
    private String experienceRequirement;
    private String salaryRange;
    private String workProvinceCode;
    private String workCityCode;
    private String workCountyCode;
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
