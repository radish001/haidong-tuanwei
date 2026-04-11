package com.haidong.tuanwei.system.service;

import com.haidong.tuanwei.system.dto.DataImportResult;
import com.haidong.tuanwei.system.dto.MajorForm;
import com.haidong.tuanwei.system.dto.SchoolForm;
import com.haidong.tuanwei.system.dto.SchoolTagForm;
import com.haidong.tuanwei.system.entity.MajorCatalog;
import com.haidong.tuanwei.system.entity.School;
import com.haidong.tuanwei.system.entity.SchoolTag;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MasterDataService {

    List<MajorCatalog> searchMajors(String keyword, int page, int pageSize);

    long countMajors(String keyword);

    List<MajorCatalog> getAllMajors();

    MajorCatalog getMajorById(Long id);

    void createMajor(MajorForm request);

    void updateMajor(Long id, MajorForm request);

    void deleteMajor(Long id);

    byte[] generateMajorImportTemplate();

    DataImportResult importMajorsFromExcel(MultipartFile file);

    List<SchoolTag> searchSchoolTags(String keyword, int page, int pageSize);

    long countSchoolTags(String keyword);

    List<SchoolTag> getAllSchoolTags();

    SchoolTag getSchoolTagById(Long id);

    void createSchoolTag(SchoolTagForm request);

    void updateSchoolTag(Long id, SchoolTagForm request);

    void deleteSchoolTag(Long id);

    List<School> searchSchools(String keyword, int page, int pageSize);

    long countSchools(String keyword);

    List<School> getAllSchools();

    List<School> getAllSchoolsForSelect();

    School getSchoolById(Long id);

    void createSchool(SchoolForm request);

    void updateSchool(Long id, SchoolForm request);

    void deleteSchool(Long id);

    byte[] generateSchoolImportTemplate();

    DataImportResult importSchoolsFromExcel(MultipartFile file);

    List<Long> getAnalyticsSchoolTagIds();

    void saveAnalyticsSchoolTagIds(List<Long> tagIds);
}
