package com.haidong.tuanwei.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictionaryItemForm {

    private Long id;

    @NotBlank(message = "显示名称不能为空")
    private String dictLabel;

    @NotBlank(message = "字典值不能为空")
    private String dictValue;

    /**
     * 所属学历层次（专科专业/本科专业/研究生专业，可多选）
     * 仅对 major_category 类型有效，逗号分隔的常量值
     */
    private String educationScopes;
}
