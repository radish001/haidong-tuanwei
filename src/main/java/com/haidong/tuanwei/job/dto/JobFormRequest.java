package com.haidong.tuanwei.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class JobFormRequest {

    private Long id;

    @NotNull(message = "所属企业不能为空")
    private Long enterpriseId;

    @NotBlank(message = "岗位名称不能为空")
    private String jobName;

    private String jobCategory;
    private List<String> educationRequirements = new ArrayList<>();
    private List<String> majorCodes = new ArrayList<>();
    private List<Long> schoolCategoryIds = new ArrayList<>();
    private List<Long> schoolTagIds = new ArrayList<>();
    private String experienceRequirement;
    private String salaryRange;
    private Integer recruitCount;
    private String workProvinceCode;
    private String workCityCode;
    private String workCountyCode;
    private String contactPerson;
    private String contactPhone;
    private String jobDescription;
}
