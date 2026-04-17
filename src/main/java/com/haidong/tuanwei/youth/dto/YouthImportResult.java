package com.haidong.tuanwei.youth.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class YouthImportResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int successCount;

    private int failCount;

    private final List<YouthImportError> errors = new ArrayList<>();

    private final List<YouthImportFailedRow> failedRows = new ArrayList<>();

    public void addSuccess() {
        successCount++;
    }

    public void addError(int rowNumber, String message) {
        addError(rowNumber, message, List.of());
    }

    public void addError(int rowNumber, String message, List<String> values) {
        failCount++;
        errors.add(new YouthImportError(rowNumber, message));
        if (values != null && !values.isEmpty()) {
            failedRows.add(new YouthImportFailedRow(rowNumber, new ArrayList<>(values), message));
        }
    }

    public boolean hasFailedRows() {
        return !failedRows.isEmpty();
    }
}
