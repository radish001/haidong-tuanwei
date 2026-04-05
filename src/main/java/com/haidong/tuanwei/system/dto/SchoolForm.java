package com.haidong.tuanwei.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SchoolForm {

    private Long id;

    @NotBlank(message = "学校名称不能为空")
    private String schoolName;

    @NotNull(message = "学校类别不能为空")
    private Long categoryDictItemId;

    private List<Long> tagIds = new ArrayList<>();
}
