package com.haidong.tuanwei.job.service.impl;

import com.haidong.tuanwei.enterprise.service.EnterpriseService;
import com.haidong.tuanwei.job.dao.JobPostDao;
import com.haidong.tuanwei.job.dto.JobFormRequest;
import com.haidong.tuanwei.job.dto.JobSearchRequest;
import com.haidong.tuanwei.job.entity.JobPost;
import com.haidong.tuanwei.job.service.JobPostService;
import com.haidong.tuanwei.system.dao.DictionaryDao;
import com.haidong.tuanwei.system.dao.MajorCatalogDao;
import com.haidong.tuanwei.system.dao.SchoolTagDao;
import com.haidong.tuanwei.system.entity.DictItem;
import com.haidong.tuanwei.system.entity.MajorCatalog;
import com.haidong.tuanwei.system.entity.SchoolTag;
import com.haidong.tuanwei.system.support.RegionSelectionSupport;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {

    private final JobPostDao jobPostDao;
    private final EnterpriseService enterpriseService;
    private final DictionaryDao dictionaryDao;
    private final MajorCatalogDao majorCatalogDao;
    private final SchoolTagDao schoolTagDao;
    private final RegionSelectionSupport regionSelectionSupport;

    @Override
    public List<JobPost> search(JobSearchRequest query) {
        List<JobPost> records = jobPostDao.search(query);
        records.forEach(this::populateSelections);
        return records;
    }

    @Override
    public long count(JobSearchRequest query) {
        return jobPostDao.count(query);
    }

    @Override
    public JobPost getById(Long id) {
        JobPost jobPost = jobPostDao.findById(id);
        if (jobPost != null) {
            populateSelections(jobPost);
        }
        return jobPost;
    }

    @Override
    public void create(JobFormRequest request, Long operatorId) {
        JobPost jobPost = toEntity(request);
        jobPost.setPublishTime(LocalDateTime.now());
        jobPost.setStatus(1);
        jobPostDao.insert(jobPost);
        replaceSelections(jobPost.getId(), request, operatorId);
        log.info("Job post created: id={}, operatorId={}, enterpriseId={}",
                jobPost.getId(), operatorId, jobPost.getEnterpriseId());
    }

    @Override
    public void update(Long id, JobFormRequest request, Long operatorId) {
        JobPost jobPost = toEntity(request);
        jobPost.setId(id);
        jobPost.setStatus(1);
        jobPostDao.update(jobPost);
        replaceSelections(id, request, operatorId);
        log.info("Job post updated: id={}, operatorId={}, enterpriseId={}",
                id, operatorId, jobPost.getEnterpriseId());
    }

    @Override
    public void delete(Long id, Long operatorId) {
        jobPostDao.softDelete(id, operatorId);
        log.info("Job post deleted: id={}, operatorId={}", id, operatorId);
    }

    @Override
    public int deleteBatch(List<Long> ids, Long operatorId) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        int deletedCount = jobPostDao.softDeleteBatch(ids, operatorId);
        log.info("Job posts batch deleted: requestedCount={}, deletedCount={}, operatorId={}",
                ids.size(), deletedCount, operatorId);
        return deletedCount;
    }

    private JobPost toEntity(JobFormRequest request) {
        if (enterpriseService.getById(request.getEnterpriseId()) == null) {
            throw new IllegalStateException("所属企业不存在");
        }
        JobPost jobPost = new JobPost();
        jobPost.setEnterpriseId(request.getEnterpriseId());
        jobPost.setJobName(request.getJobName());
        jobPost.setJobCategory(request.getJobCategory());
        List<String> educationCodes = normalizeDictValues("education_level", request.getEducationRequirements(), "学历要求");
        jobPost.setEducationRequirements(educationCodes);
        jobPost.setEducationRequirement(educationCodes.isEmpty() ? null : educationCodes.get(0));
        jobPost.setMajorCodes(normalizeMajorCodes(request.getMajorCodes()));
        jobPost.setSchoolCategoryIds(normalizeDictItemIds("school_category", request.getSchoolCategoryIds(), "学校类别"));
        jobPost.setSchoolTagIds(normalizeSchoolTagIds(request.getSchoolTagIds()));
        jobPost.setExperienceRequirement(validateDictValue("experience_requirement", request.getExperienceRequirement(), "经验要求"));
        jobPost.setSalaryRange(validateDictValue("salary_range", request.getSalaryRange(), "薪资待遇"));
        jobPost.setRecruitCount(request.getRecruitCount());
        RegionSelectionSupport.RegionSelection workRegionSelection = regionSelectionSupport.normalize(
                request.getWorkProvinceCode(), request.getWorkCityCode(), request.getWorkCountyCode(), "工作地区");
        jobPost.setWorkProvinceCode(workRegionSelection.getProvinceCode());
        jobPost.setWorkCityCode(workRegionSelection.getCityCode());
        jobPost.setWorkCountyCode(workRegionSelection.getCountyCode());
        jobPost.setContactPerson(request.getContactPerson());
        jobPost.setContactPhone(request.getContactPhone());
        jobPost.setJobDescription(request.getJobDescription());
        return jobPost;
    }

    private void replaceSelections(Long jobPostId, JobFormRequest request, Long operatorId) {
        jobPostDao.deleteEducationRelations(jobPostId);
        for (String educationCode : normalizeDictValues("education_level", request.getEducationRequirements(), "学历要求")) {
            jobPostDao.insertEducationRelation(jobPostId, educationCode, operatorId);
        }

        jobPostDao.deleteMajorRelations(jobPostId);
        for (String majorCode : normalizeMajorCodes(request.getMajorCodes())) {
            jobPostDao.insertMajorRelation(jobPostId, majorCode, operatorId);
        }

        jobPostDao.deleteSchoolCategoryRelations(jobPostId);
        for (Long categoryId : normalizeDictItemIds("school_category", request.getSchoolCategoryIds(), "学校类别")) {
            jobPostDao.insertSchoolCategoryRelation(jobPostId, categoryId, operatorId);
        }

        jobPostDao.deleteSchoolTagRelations(jobPostId);
        for (Long tagId : normalizeSchoolTagIds(request.getSchoolTagIds())) {
            jobPostDao.insertSchoolTagRelation(jobPostId, tagId, operatorId);
        }
    }

    private void populateSelections(JobPost jobPost) {
        List<String> educationCodes = new ArrayList<>(jobPostDao.findEducationCodesByJobId(jobPost.getId()));
        List<String> majorCodes = new ArrayList<>(jobPostDao.findMajorCodesByJobId(jobPost.getId()));
        List<Long> schoolCategoryIds = new ArrayList<>(jobPostDao.findSchoolCategoryIdsByJobId(jobPost.getId()));
        List<Long> schoolTagIds = new ArrayList<>(jobPostDao.findSchoolTagIdsByJobId(jobPost.getId()));
        jobPost.setEducationRequirements(educationCodes);
        jobPost.setEducationRequirement(educationCodes.isEmpty() ? null : educationCodes.get(0));
        jobPost.setMajorCodes(majorCodes);
        jobPost.setSchoolCategoryIds(schoolCategoryIds);
        jobPost.setSchoolTagIds(schoolTagIds);
        populateSummaries(jobPost);
    }

    private void populateSummaries(JobPost jobPost) {
        jobPost.setEducationRequirement(joinDictLabels("education_level", jobPost.getEducationRequirements()));
        jobPost.setMajorRequirementSummary(joinMajorNames(jobPost.getMajorCodes()));
        jobPost.setSchoolCategorySummary(joinDictLabelsByIds("school_category", jobPost.getSchoolCategoryIds()));
        jobPost.setSchoolTagSummary(joinSchoolTagNames(jobPost.getSchoolTagIds()));
    }

    private List<String> normalizeDictValues(String dictType, List<String> values, String label) {
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        if (values == null) {
            return List.of();
        }
        for (String value : values) {
            String validated = validateDictValue(dictType, value, label);
            if (validated != null) {
                normalized.add(validated);
            }
        }
        return new ArrayList<>(normalized);
    }

    private List<Long> normalizeDictItemIds(String dictType, List<Long> ids, String label) {
        LinkedHashSet<Long> normalized = new LinkedHashSet<>();
        if (ids == null) {
            return List.of();
        }
        List<DictItem> items = dictionaryDao.findByType(dictType);
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            DictItem matched = items.stream()
                    .filter(item -> Objects.equals(item.getId(), id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(label + "不在基础数据范围内"));
            normalized.add(matched.getId());
        }
        return new ArrayList<>(normalized);
    }

    private List<String> normalizeMajorCodes(List<String> majorCodes) {
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        if (majorCodes == null) {
            return List.of();
        }
        for (String majorCode : majorCodes) {
            if (majorCode == null || majorCode.isBlank()) {
                continue;
            }
            MajorCatalog major = majorCatalogDao.findByCode(majorCode.trim());
            if (major == null) {
                throw new IllegalStateException("专业要求不在基础数据范围内");
            }
            normalized.add(major.getMajorCode());
        }
        return new ArrayList<>(normalized);
    }

    private List<Long> normalizeSchoolTagIds(List<Long> tagIds) {
        LinkedHashSet<Long> normalized = new LinkedHashSet<>();
        if (tagIds == null) {
            return List.of();
        }
        for (Long tagId : tagIds) {
            if (tagId == null) {
                continue;
            }
            SchoolTag tag = schoolTagDao.findById(tagId);
            if (tag == null) {
                throw new IllegalStateException("学校标签不在基础数据范围内");
            }
            normalized.add(tag.getId());
        }
        return new ArrayList<>(normalized);
    }

    private String joinDictLabels(String dictType, List<String> dictValues) {
        if (dictValues == null || dictValues.isEmpty()) {
            return "";
        }
        List<DictItem> items = dictionaryDao.findByType(dictType);
        List<String> labels = new ArrayList<>();
        for (String dictValue : dictValues) {
            items.stream()
                    .filter(item -> item.getDictValue().equals(dictValue))
                    .findFirst()
                    .map(DictItem::getDictLabel)
                    .ifPresent(labels::add);
        }
        return String.join("、", labels);
    }

    private String joinDictLabelsByIds(String dictType, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        List<DictItem> items = dictionaryDao.findByType(dictType);
        List<String> labels = new ArrayList<>();
        for (Long id : ids) {
            items.stream()
                    .filter(item -> Objects.equals(item.getId(), id))
                    .findFirst()
                    .map(DictItem::getDictLabel)
                    .ifPresent(labels::add);
        }
        return String.join("、", labels);
    }

    private String joinMajorNames(List<String> majorCodes) {
        if (majorCodes == null || majorCodes.isEmpty()) {
            return "";
        }
        List<String> labels = new ArrayList<>();
        for (String majorCode : majorCodes) {
            MajorCatalog major = majorCatalogDao.findByCode(majorCode);
            if (major != null) {
                labels.add(major.getMajorName());
            }
        }
        return String.join("、", labels);
    }

    private String joinSchoolTagNames(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return "";
        }
        List<String> labels = new ArrayList<>();
        for (Long tagId : tagIds) {
            SchoolTag tag = schoolTagDao.findById(tagId);
            if (tag != null) {
                labels.add(tag.getTagName());
            }
        }
        return String.join("、", labels);
    }

    private String validateDictValue(String dictType, String value, String label) {
        if (value == null || value.isBlank()) {
            return null;
        }
        List<DictItem> items = dictionaryDao.findByType(dictType);
        return items.stream()
                .map(DictItem::getDictValue)
                .filter(itemValue -> itemValue.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(label + "不在基础数据范围内"));
    }
}
