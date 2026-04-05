package com.haidong.tuanwei.youth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YouthImportError {

    private int rowNumber;

    private String message;
}
