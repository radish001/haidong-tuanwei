package com.haidong.tuanwei.enterprise.entity;

import lombok.Data;

@Data
public class EnterpriseInfo {

    private Long id;
    private String enterpriseName;
    private String industry;
    private String enterpriseNature;
    private String enterpriseScale;
    private String regionProvinceCode;
    private String regionCityCode;
    private String regionCountyCode;
    private String regionName;
    private String unifiedSocialCreditCode;
    private String businessLicensePath;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String description;
    private Integer sortOrder;
    private Integer status;
}
