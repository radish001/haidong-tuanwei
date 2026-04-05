package com.haidong.tuanwei.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SchoolTagForm {

    private Long id;

    @NotBlank(message = "标签名称不能为空")
    private String tagName;
}
