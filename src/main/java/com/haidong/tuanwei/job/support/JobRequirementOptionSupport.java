package com.haidong.tuanwei.job.support;

import java.util.List;

public final class JobRequirementOptionSupport {

    public static final String UNLIMITED_OPTION_VALUE = "UNLIMITED_OPTION";

    private JobRequirementOptionSupport() {
    }

    public static boolean isUnlimitedSelection(String value) {
        return UNLIMITED_OPTION_VALUE.equals(value);
    }

    public static boolean containsUnlimitedSelection(List<String> values) {
        return values != null && values.stream().anyMatch(JobRequirementOptionSupport::isUnlimitedSelection);
    }

    public static List<String> toFormSelections(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of(UNLIMITED_OPTION_VALUE);
        }
        return values;
    }
}
