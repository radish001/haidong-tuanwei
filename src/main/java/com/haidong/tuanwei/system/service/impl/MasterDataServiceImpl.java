package com.haidong.tuanwei.system.service.impl;

import com.haidong.tuanwei.system.dao.DictionaryDao;
import com.haidong.tuanwei.system.dao.MajorCatalogDao;
import com.haidong.tuanwei.system.dao.SchoolDao;
import com.haidong.tuanwei.system.dao.SchoolTagDao;
import com.haidong.tuanwei.system.dto.MajorForm;
import com.haidong.tuanwei.system.dto.SchoolForm;
import com.haidong.tuanwei.system.dto.SchoolTagForm;
import com.haidong.tuanwei.system.entity.DictItem;
import com.haidong.tuanwei.system.entity.MajorCatalog;
import com.haidong.tuanwei.system.entity.School;
import com.haidong.tuanwei.system.entity.SchoolTag;
import com.haidong.tuanwei.system.service.MasterDataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private final MajorCatalogDao majorCatalogDao;
    private final SchoolTagDao schoolTagDao;
    private final SchoolDao schoolDao;
    private final DictionaryDao dictionaryDao;

    @Override
    public List<MajorCatalog> searchMajors(String keyword, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        return majorCatalogDao.search(keyword, (safePage - 1) * safePageSize, safePageSize);
    }

    @Override
    public long countMajors(String keyword) {
        return majorCatalogDao.count(keyword);
    }

    @Override
    public List<MajorCatalog> getAllMajors() {
        return majorCatalogDao.findAll();
    }

    @Override
    public MajorCatalog getMajorById(Long id) {
        return majorCatalogDao.findById(id);
    }

    @Override
    public void createMajor(MajorForm request) {
        if (majorCatalogDao.findByName(request.getMajorName()) != null) {
            throw new IllegalStateException("专业名称已存在");
        }
        DictItem category = requireDictItem(request.getCategoryDictItemId(), "major_category");
        MajorCatalog majorCatalog = new MajorCatalog();
        majorCatalog.setMajorName(request.getMajorName().trim());
        majorCatalog.setCategoryDictItemId(category.getId());
        majorCatalogDao.insert(majorCatalog);
    }

    @Override
    public void updateMajor(Long id, MajorForm request) {
        MajorCatalog existing = requireMajor(id);
        MajorCatalog duplicate = majorCatalogDao.findByName(request.getMajorName().trim());
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new IllegalStateException("专业名称已存在");
        }
        DictItem category = requireDictItem(request.getCategoryDictItemId(), "major_category");
        String oldMajorName = existing.getMajorName();
        existing.setMajorName(request.getMajorName().trim());
        existing.setCategoryDictItemId(category.getId());
        majorCatalogDao.update(existing);
        majorCatalogDao.updateYouthReferences(oldMajorName, existing.getMajorName(), category.getDictLabel());
    }

    @Override
    public void deleteMajor(Long id) {
        MajorCatalog existing = requireMajor(id);
        if (majorCatalogDao.countYouthUsageByMajorName(existing.getMajorName()) > 0) {
            throw new IllegalStateException("该专业名称已被青年信息使用，无法删除");
        }
        majorCatalogDao.softDelete(id);
    }

    @Override
    public List<SchoolTag> searchSchoolTags(String keyword, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        return schoolTagDao.search(keyword, (safePage - 1) * safePageSize, safePageSize);
    }

    @Override
    public long countSchoolTags(String keyword) {
        return schoolTagDao.count(keyword);
    }

    @Override
    public List<SchoolTag> getAllSchoolTags() {
        return schoolTagDao.findAll();
    }

    @Override
    public SchoolTag getSchoolTagById(Long id) {
        return schoolTagDao.findById(id);
    }

    @Override
    public void createSchoolTag(SchoolTagForm request) {
        if (schoolTagDao.findByName(request.getTagName().trim()) != null) {
            throw new IllegalStateException("学校标签已存在");
        }
        SchoolTag schoolTag = new SchoolTag();
        schoolTag.setTagName(request.getTagName().trim());
        schoolTagDao.insert(schoolTag);
    }

    @Override
    public void updateSchoolTag(Long id, SchoolTagForm request) {
        SchoolTag existing = requireSchoolTag(id);
        SchoolTag duplicate = schoolTagDao.findByName(request.getTagName().trim());
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new IllegalStateException("学校标签已存在");
        }
        existing.setTagName(request.getTagName().trim());
        schoolTagDao.update(existing);
    }

    @Override
    public void deleteSchoolTag(Long id) {
        requireSchoolTag(id);
        if (schoolTagDao.countSchoolUsage(id) > 0) {
            throw new IllegalStateException("该学校标签存在关联学校，无法删除");
        }
        schoolTagDao.softDelete(id);
    }

    @Override
    public List<School> searchSchools(String keyword, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        List<School> schools = schoolDao.search(keyword, (safePage - 1) * safePageSize, safePageSize);
        populateTagIds(schools);
        return schools;
    }

    @Override
    public long countSchools(String keyword) {
        return schoolDao.count(keyword);
    }

    @Override
    public List<School> getAllSchools() {
        List<School> schools = schoolDao.findAll();
        populateTagIds(schools);
        return schools;
    }

    @Override
    public School getSchoolById(Long id) {
        School school = schoolDao.findById(id);
        if (school != null) {
            school.setTagIds(schoolDao.findTagIdsBySchoolId(id));
        }
        return school;
    }

    @Override
    public void createSchool(SchoolForm request) {
        if (schoolDao.findByName(request.getSchoolName().trim()) != null) {
            throw new IllegalStateException("学校名称已存在");
        }
        DictItem category = requireDictItem(request.getCategoryDictItemId(), "school_category");
        School school = new School();
        school.setSchoolName(request.getSchoolName().trim());
        school.setCategoryDictItemId(category.getId());
        schoolDao.insert(school);
        replaceSchoolTags(school.getId(), request.getTagIds());
    }

    @Override
    public void updateSchool(Long id, SchoolForm request) {
        School existing = requireSchool(id);
        School duplicate = schoolDao.findByName(request.getSchoolName().trim());
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new IllegalStateException("学校名称已存在");
        }
        DictItem category = requireDictItem(request.getCategoryDictItemId(), "school_category");
        String oldSchoolName = existing.getSchoolName();
        existing.setSchoolName(request.getSchoolName().trim());
        existing.setCategoryDictItemId(category.getId());
        schoolDao.update(existing);
        replaceSchoolTags(id, request.getTagIds());
        schoolDao.updateYouthReferences(oldSchoolName, existing.getSchoolName());
    }

    @Override
    public void deleteSchool(Long id) {
        School existing = requireSchool(id);
        if (schoolDao.countYouthUsageBySchoolName(existing.getSchoolName()) > 0) {
            throw new IllegalStateException("该学校存在关联青年信息，无法删除");
        }
        schoolDao.softDelete(id);
        schoolDao.deleteTagRelations(id);
    }

    private void replaceSchoolTags(Long schoolId, List<Long> tagIds) {
        schoolDao.deleteTagRelations(schoolId);
        if (tagIds == null) {
            return;
        }
        for (Long tagId : tagIds) {
            if (tagId == null) {
                continue;
            }
            requireSchoolTag(tagId);
            schoolDao.insertTagRelation(schoolId, tagId);
        }
    }

    private void populateTagIds(List<School> schools) {
        for (School school : schools) {
            school.setTagIds(schoolDao.findTagIdsBySchoolId(school.getId()));
        }
    }

    private DictItem requireDictItem(Long id, String dictType) {
        DictItem item = dictionaryDao.findById(id);
        if (item == null || !dictType.equals(item.getDictType())) {
            throw new IllegalStateException("关联字典不存在或类型不匹配");
        }
        return item;
    }

    private MajorCatalog requireMajor(Long id) {
        MajorCatalog majorCatalog = majorCatalogDao.findById(id);
        if (majorCatalog == null) {
            throw new IllegalStateException("专业名称不存在或已删除");
        }
        return majorCatalog;
    }

    private SchoolTag requireSchoolTag(Long id) {
        SchoolTag schoolTag = schoolTagDao.findById(id);
        if (schoolTag == null) {
            throw new IllegalStateException("学校标签不存在或已删除");
        }
        return schoolTag;
    }

    private School requireSchool(Long id) {
        School school = schoolDao.findById(id);
        if (school == null) {
            throw new IllegalStateException("学校不存在或已删除");
        }
        return school;
    }
}
