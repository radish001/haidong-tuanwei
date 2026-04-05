package com.haidong.tuanwei.job.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class JobPost {

    private Long id;
    private Long enterpriseId;
    private String enterpriseName;
    private String jobName;
    private String jobCategory;
    private String educationRequirement;
    private String experienceRequirement;
    private String salaryRange;
    private Integer recruitCount;
    private String workProvinceCode;
    private String workCityCode;
    private String workCountyCode;
    private String workRegionName;
    private String contactPerson;
    private String contactPhone;
    private String jobDescription;
    private LocalDateTime publishTime;
    private Integer status;
}
