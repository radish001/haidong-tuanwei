package com.haidong.tuanwei.job.dto;

import com.haidong.tuanwei.job.support.JobRequirementOptionSupport;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class JobSearchRequest {

    private String jobName;
    private Long enterpriseId;
    private String educationRequirement;
    private List<String> educationRequirements = new ArrayList<>();
    private List<String> majorCodes = new ArrayList<>();
    private List<Long> schoolCategoryIds = new ArrayList<>();
    private List<Long> schoolTagIds = new ArrayList<>();
    private String experienceRequirement;
    private String salaryRange;
    private String workProvinceCode;
    private String workCityCode;
    private String workCountyCode;
    private Integer page = 1;
    private Integer pageSize = 10;
    private Boolean paged = true;
    private Boolean includeUnspecifiedRequirements = false;

    public int getSafePage() {
        return page == null || page < 1 ? 1 : page;
    }

    public int getSafePageSize() {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }

    public int getOffset() {
        return (getSafePage() - 1) * getSafePageSize();
    }

    public boolean hasUnlimitedEducationFilter() {
        return JobRequirementOptionSupport.isUnlimitedSelection(educationRequirement)
                || JobRequirementOptionSupport.containsUnlimitedSelection(educationRequirements);
    }

    public boolean hasSpecificEducationFilter() {
        return !hasUnlimitedEducationFilter()
                && ((educationRequirement != null && !educationRequirement.isBlank())
                || (educationRequirements != null && !educationRequirements.isEmpty()));
    }

    public boolean hasUnlimitedMajorFilter() {
        return JobRequirementOptionSupport.containsUnlimitedSelection(majorCodes);
    }

    public boolean hasSpecificMajorFilter() {
        return !hasUnlimitedMajorFilter() && majorCodes != null && !majorCodes.isEmpty();
    }

    public boolean shouldIncludeUnspecifiedRequirements() {
        return Boolean.TRUE.equals(includeUnspecifiedRequirements);
    }
}
