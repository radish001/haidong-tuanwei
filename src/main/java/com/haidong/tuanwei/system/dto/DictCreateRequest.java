package com.haidong.tuanwei.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictCreateRequest {

    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @NotBlank(message = "显示名称不能为空")
    private String dictLabel;

    @NotBlank(message = "字典值不能为空")
    private String dictValue;
}
