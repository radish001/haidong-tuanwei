package com.haidong.tuanwei.system.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DataImportResult {

    private int successCount;

    private int failCount;

    private final List<DataImportError> errors = new ArrayList<>();

    public void addSuccess() {
        successCount++;
    }

    public void addError(int rowNumber, String message) {
        failCount++;
        errors.add(new DataImportError(rowNumber, message));
    }
}
