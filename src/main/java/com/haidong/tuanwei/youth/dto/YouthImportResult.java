package com.haidong.tuanwei.youth.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class YouthImportResult {

    private int successCount;

    private int failCount;

    private final List<YouthImportError> errors = new ArrayList<>();

    public void addSuccess() {
        successCount++;
    }

    public void addError(int rowNumber, String message) {
        failCount++;
        errors.add(new YouthImportError(rowNumber, message));
    }
}
