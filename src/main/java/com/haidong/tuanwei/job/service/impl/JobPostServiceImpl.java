package com.haidong.tuanwei.job.service.impl;

import com.haidong.tuanwei.enterprise.service.EnterpriseService;
import com.haidong.tuanwei.job.dao.JobPostDao;
import com.haidong.tuanwei.job.dto.JobFormRequest;
import com.haidong.tuanwei.job.dto.JobSearchRequest;
import com.haidong.tuanwei.job.entity.JobPost;
import com.haidong.tuanwei.job.service.JobPostService;
import com.haidong.tuanwei.system.dao.DictionaryDao;
import com.haidong.tuanwei.system.entity.DictItem;
import com.haidong.tuanwei.system.support.RegionSelectionSupport;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {

    private final JobPostDao jobPostDao;
    private final EnterpriseService enterpriseService;
    private final DictionaryDao dictionaryDao;
    private final RegionSelectionSupport regionSelectionSupport;

    @Override
    public List<JobPost> search(JobSearchRequest query) {
        return jobPostDao.search(query);
    }

    @Override
    public long count(JobSearchRequest query) {
        return jobPostDao.count(query);
    }

    @Override
    public JobPost getById(Long id) {
        return jobPostDao.findById(id);
    }

    @Override
    public void create(JobFormRequest request, Long operatorId) {
        JobPost jobPost = toEntity(request);
        jobPost.setPublishTime(LocalDateTime.now());
        jobPost.setStatus(1);
        jobPostDao.insert(jobPost);
    }

    @Override
    public void update(Long id, JobFormRequest request, Long operatorId) {
        JobPost jobPost = toEntity(request);
        jobPost.setId(id);
        jobPost.setStatus(1);
        jobPostDao.update(jobPost);
    }

    @Override
    public void delete(Long id, Long operatorId) {
        jobPostDao.softDelete(id, operatorId);
    }

    @Override
    public int deleteBatch(List<Long> ids, Long operatorId) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return jobPostDao.softDeleteBatch(ids, operatorId);
    }

    private JobPost toEntity(JobFormRequest request) {
        if (enterpriseService.getById(request.getEnterpriseId()) == null) {
            throw new IllegalStateException("所属企业不存在");
        }
        JobPost jobPost = new JobPost();
        jobPost.setEnterpriseId(request.getEnterpriseId());
        jobPost.setJobName(request.getJobName());
        jobPost.setJobCategory(request.getJobCategory());
        jobPost.setEducationRequirement(validateDictValue("education_level", request.getEducationRequirement(), "学历要求"));
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
