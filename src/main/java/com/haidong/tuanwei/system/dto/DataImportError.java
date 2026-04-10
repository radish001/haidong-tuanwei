package com.haidong.tuanwei.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataImportError {

    private int rowNumber;

    private String message;
}
