package com.haidong.tuanwei.youth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class YouthFormRequest {

    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    private String birthDate;
    private String ethnicity;
    private String politicalStatus;
    private String nativeProvinceCode;
    private String nativeCityCode;
    private String nativeCountyCode;
    private String educationLevel;
    private String degreeCode;
    private String schoolCode;
    private String schoolProvinceCode;
    private String schoolCityCode;
    private String schoolCountyCode;
    private String majorCode;
    private String majorCategory;
    private String recruitmentYear;
    private String graduationDate;
    private String employmentDirection;
    private String phone;
    private String residenceProvinceCode;
    private String residenceCityCode;
    private String residenceCountyCode;
    private String employmentStatus;
    private String currentJob;
    private String employmentCompany;
    private String entrepreneurshipStatus;
    private String entrepreneurshipProject;
    private String entrepreneurshipDemand;
    private String remarks;
}
