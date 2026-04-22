package com.haidong.tuanwei.youth.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class YouthInfo {

    private Long id;
    private String youthType;
    private String name;
    private String gender;
    private String genderName;
    private LocalDate birthDate;
    private String ethnicity;
    private String ethnicityName;
    private String politicalStatus;
    private String politicalStatusName;
    private String nativeProvinceCode;
    private String nativeCityCode;
    private String nativeCountyCode;
    private String nativePlaceName;
    private String educationCode;
    private String educationLevelName;
    private String degreeCode;
    private String degreeName;
    private String schoolCode;
    private String schoolName;
    private String schoolProvinceCode;
    private String schoolCityCode;
    private String schoolCountyCode;
    private String schoolRegionName;
    private String majorCode;
    private String major;
    private String majorCategory;
    private Integer recruitmentYear;
    private LocalDate graduationDate;
    private String employmentDirection;
    private String phone;
    private String residenceProvinceCode;
    private String residenceCityCode;
    private String residenceCountyCode;
    private String currentResidenceName;
    private String employmentStatus;
    private String currentJob;
    private String employmentCompany;
    private String entrepreneurshipStatus;
    private String entrepreneurshipProject;
    private String entrepreneurshipDemand;
    private Integer sortOrder;
    private String remarks;
    private Long createBy;
    private Long updateBy;
    private LocalDateTime createTime;
}
