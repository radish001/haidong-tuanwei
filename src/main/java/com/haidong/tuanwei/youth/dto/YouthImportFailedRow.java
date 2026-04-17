package com.haidong.tuanwei.youth.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YouthImportFailedRow implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int rowNumber;

    private List<String> values;

    private String message;
}
