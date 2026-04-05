package com.haidong.tuanwei.policy.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PolicyArticle {

    private Long id;
    private String title;
    private String issuingOrganization;
    private String policySource;
    private String summary;
    private String contentHtml;
    private LocalDateTime publishTime;
    private Integer status;
}
