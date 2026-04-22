package com.haidong.tuanwei.policy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PolicyFormRequest {

    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String issuingOrganization;
    private String policySource;
    private String summary;
    private Integer sortOrder;

    @NotBlank(message = "正文不能为空")
    private String contentHtml;
}
