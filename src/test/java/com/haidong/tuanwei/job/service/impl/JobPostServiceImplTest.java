package com.haidong.tuanwei.job.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.haidong.tuanwei.enterprise.entity.EnterpriseInfo;
import com.haidong.tuanwei.enterprise.service.EnterpriseService;
import com.haidong.tuanwei.job.dao.JobPostDao;
import com.haidong.tuanwei.job.dto.JobFormRequest;
import com.haidong.tuanwei.job.entity.JobPost;
import com.haidong.tuanwei.system.dao.DictionaryDao;
import com.haidong.tuanwei.system.dao.MajorCatalogDao;
import com.haidong.tuanwei.system.dao.SchoolTagDao;
import com.haidong.tuanwei.system.entity.DictItem;
import com.haidong.tuanwei.system.entity.MajorCatalog;
import com.haidong.tuanwei.system.entity.SchoolTag;
import com.haidong.tuanwei.system.support.RegionSelectionSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class JobPostServiceImplTest {

    private JobPostDao jobPostDao;
    private EnterpriseService enterpriseService;
    private DictionaryDao dictionaryDao;
    private MajorCatalogDao majorCatalogDao;
    private SchoolTagDao schoolTagDao;
    private RegionSelectionSupport regionSelectionSupport;
    private JobPostServiceImpl jobPostService;

    @BeforeEach
    void setUp() {
        jobPostDao = mock(JobPostDao.class);
        enterpriseService = mock(EnterpriseService.class);
        dictionaryDao = mock(DictionaryDao.class);
        majorCatalogDao = mock(MajorCatalogDao.class);
        schoolTagDao = mock(SchoolTagDao.class);
        regionSelectionSupport = mock(RegionSelectionSupport.class);

        EnterpriseInfo enterprise = new EnterpriseInfo();
        enterprise.setId(1L);
        enterprise.setEnterpriseName("示例企业");
        when(enterpriseService.getById(1L)).thenReturn(enterprise);

        when(dictionaryDao.findByType("education_level")).thenReturn(List.of(
                dictItem(11L, "education_level", "BK", "本科"),
                dictItem(12L, "education_level", "SS", "硕士")));
        when(dictionaryDao.findByType("experience_requirement")).thenReturn(List.of(
                dictItem(21L, "experience_requirement", "EXP_1_3", "1-3年")));
        when(dictionaryDao.findByType("salary_range")).thenReturn(List.of(
                dictItem(31L, "salary_range", "SALARY_A", "8000-12000元")));
        when(dictionaryDao.findByType("school_category")).thenReturn(List.of(
                dictItem(41L, "school_category", "SC001", "双一流")));

        MajorCatalog major = new MajorCatalog();
        major.setMajorCode("080901");
        major.setMajorName("计算机科学与技术");
        when(majorCatalogDao.findByCode("080901")).thenReturn(major);

        SchoolTag tag = new SchoolTag();
        tag.setId(51L);
        tag.setTagName("985");
        when(schoolTagDao.findById(51L)).thenReturn(tag);

        RegionSelectionSupport.RegionSelection regionSelection = new RegionSelectionSupport.RegionSelection("630000", "630200",
                "630202");
        when(regionSelectionSupport.normalize("630000", "630200", "630202", "工作地区")).thenReturn(regionSelection);

        jobPostService = new JobPostServiceImpl(
                jobPostDao, enterpriseService, dictionaryDao, majorCatalogDao, schoolTagDao, regionSelectionSupport);
    }

    @Test
    void createPersistsMultiSelectionsAndLegacyEducationValue() {
        JobFormRequest request = new JobFormRequest();
        request.setEnterpriseId(1L);
        request.setJobName("Java开发工程师");
        request.setEducationRequirements(List.of("BK", "SS"));
        request.setMajorCodes(List.of("080901"));
        request.setSchoolCategoryIds(List.of(41L));
        request.setSchoolTagIds(List.of(51L));
        request.setExperienceRequirement("EXP_1_3");
        request.setSalaryRange("SALARY_A");
        request.setWorkProvinceCode("630000");
        request.setWorkCityCode("630200");
        request.setWorkCountyCode("630202");

        ArgumentCaptor<JobPost> captor = ArgumentCaptor.forClass(JobPost.class);
        when(jobPostDao.insert(captor.capture())).thenAnswer(invocation -> {
            JobPost jobPost = captor.getValue();
            jobPost.setId(100L);
            return 1;
        });

        jobPostService.create(request, 9L);

        JobPost saved = captor.getValue();
        assertThat(saved.getEducationRequirement()).isEqualTo("BK");
        assertThat(saved.getEducationRequirements()).containsExactly("BK", "SS");
        assertThat(saved.getMajorCodes()).containsExactly("080901");
        assertThat(saved.getSchoolCategoryIds()).containsExactly(41L);
        assertThat(saved.getSchoolTagIds()).containsExactly(51L);

        verify(jobPostDao).insertEducationRelation(100L, "BK", 9L);
        verify(jobPostDao).insertEducationRelation(100L, "SS", 9L);
        verify(jobPostDao).insertMajorRelation(100L, "080901", 9L);
        verify(jobPostDao).insertSchoolCategoryRelation(100L, 41L, 9L);
        verify(jobPostDao).insertSchoolTagRelation(100L, 51L, 9L);
    }

    @Test
    void getByIdLoadsSelectionsAndReadableSummaries() {
        JobPost stored = new JobPost();
        stored.setId(100L);
        stored.setEnterpriseId(1L);
        stored.setJobName("Java开发工程师");
        when(jobPostDao.findById(100L)).thenReturn(stored);
        when(jobPostDao.findEducationCodesByJobId(100L)).thenReturn(List.of("BK", "SS"));
        when(jobPostDao.findMajorCodesByJobId(100L)).thenReturn(List.of("080901"));
        when(jobPostDao.findSchoolCategoryIdsByJobId(100L)).thenReturn(List.of(41L));
        when(jobPostDao.findSchoolTagIdsByJobId(100L)).thenReturn(List.of(51L));

        JobPost jobPost = jobPostService.getById(100L);

        assertThat(jobPost.getEducationRequirements()).containsExactly("BK", "SS");
        assertThat(jobPost.getEducationRequirement()).isEqualTo("本科、硕士");
        assertThat(jobPost.getMajorRequirementSummary()).isEqualTo("计算机科学与技术");
        assertThat(jobPost.getSchoolCategorySummary()).isEqualTo("双一流");
        assertThat(jobPost.getSchoolTagSummary()).isEqualTo("985");

        verify(jobPostDao, times(1)).findEducationCodesByJobId(100L);
        verify(jobPostDao, times(1)).findMajorCodesByJobId(100L);
        verify(jobPostDao, times(1)).findSchoolCategoryIdsByJobId(100L);
        verify(jobPostDao, times(1)).findSchoolTagIdsByJobId(100L);
    }

    private DictItem dictItem(Long id, String type, String value, String label) {
        DictItem item = new DictItem();
        item.setId(id);
        item.setDictType(type);
        item.setDictValue(value);
        item.setDictLabel(label);
        return item;
    }
}
