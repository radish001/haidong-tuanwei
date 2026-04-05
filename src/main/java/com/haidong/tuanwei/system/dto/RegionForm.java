package com.haidong.tuanwei.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegionForm {

    private Long id;

    @NotNull(message = "上级区域不能为空")
    private Long parentId;

    @NotBlank(message = "区域编码不能为空")
    private String regionCode;

    @NotBlank(message = "区域名称不能为空")
    private String regionName;

    @NotNull(message = "区域层级不能为空")
    private Integer regionLevel;
}
