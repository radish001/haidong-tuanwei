package com.haidong.tuanwei.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MajorForm {

    private Long id;

    @NotBlank(message = "专业编码不能为空")
    private String majorCode;

    @NotBlank(message = "专业名称不能为空")
    private String majorName;

    @NotNull(message = "专业类别不能为空")
    private Long categoryDictItemId;
}
