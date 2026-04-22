package com.haidong.tuanwei.enterprise.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnterpriseFormRequest {

    private Long id;

    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    private String industry;
    private String enterpriseNature;
    private String enterpriseScale;
    private String regionProvinceCode;
    private String regionCityCode;
    private String regionCountyCode;
    private String unifiedSocialCreditCode;
    private String businessLicensePath;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String description;
    private Integer sortOrder;
}
