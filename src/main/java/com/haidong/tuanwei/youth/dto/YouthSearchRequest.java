package com.haidong.tuanwei.youth.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class YouthSearchRequest {

    private String name;
    private String gender;
    private String ethnicity;
    private String birthDate;
    private Integer recruitmentYear;
    private String politicalStatus;
    private String educationLevel;
    private String degreeCode;
    private String nativeProvinceCode;
    private String nativeCityCode;
    private String nativeCountyCode;
    private String residenceProvinceCode;
    private String residenceCityCode;
    private String residenceCountyCode;
    private String schoolProvinceCode;
    private String schoolCityCode;
    private String schoolCountyCode;
    private String ageRange;
    private String schoolCode;
    private String majorCode;
    private String majorCategory;
    private String phone;
    private List<String> educationCodes = new ArrayList<>();
    private List<String> majorCodes = new ArrayList<>();
    private List<Long> schoolCategoryIds = new ArrayList<>();
    private List<Long> schoolTagIds = new ArrayList<>();
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
