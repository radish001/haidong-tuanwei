package com.haidong.tuanwei.job.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class JobPost {

    private Long id;
    private Long enterpriseId;
    private String enterpriseName;
    private String jobName;
    private String jobCategory;
    private String educationRequirement;
    private String majorRequirementSummary;
    private String schoolCategorySummary;
    private String schoolTagSummary;
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
    private Integer sortOrder;
    private LocalDateTime publishTime;
    private Integer status;
    private List<String> educationRequirements = new ArrayList<>();
    private List<String> majorCodes = new ArrayList<>();
    private List<Long> schoolCategoryIds = new ArrayList<>();
    private List<Long> schoolTagIds = new ArrayList<>();
}
