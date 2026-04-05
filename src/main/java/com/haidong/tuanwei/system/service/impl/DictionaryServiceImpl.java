package com.haidong.tuanwei.system.service.impl;

import com.haidong.tuanwei.system.dao.DictionaryDao;
import com.haidong.tuanwei.system.dao.MajorCatalogDao;
import com.haidong.tuanwei.system.dao.SchoolDao;
import com.haidong.tuanwei.system.dto.DictionaryItemForm;
import com.haidong.tuanwei.system.entity.DictItem;
import com.haidong.tuanwei.system.service.DictionaryService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private static final Set<String> MANAGED_DICT_TYPES = Set.of(
            "gender",
            "ethnicity",
            "political_status",
            "education_level",
            "major_category",
            "school_category",
            "enterprise_scale",
            "enterprise_nature",
            "enterprise_industry",
            "experience_requirement",
            "salary_range");

    private final DictionaryDao dictionaryDao;
    private final MajorCatalogDao majorCatalogDao;
    private final SchoolDao schoolDao;

    @Override
    public List<DictItem> getByType(String dictType) {
        return dictionaryDao.findByType(dictType);
    }

    @Override
    public List<DictItem> searchByType(String dictType, String keyword, int page, int pageSize) {
        requireManagedType(dictType);
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        return dictionaryDao.searchByType(dictType, keyword, (safePage - 1) * safePageSize, safePageSize);
    }

    @Override
    public long countByType(String dictType, String keyword) {
        requireManagedType(dictType);
        return dictionaryDao.countByType(dictType, keyword);
    }

    @Override
    public List<DictItem> getAll() {
        return dictionaryDao.findAll();
    }

    @Override
    public DictItem getById(Long id) {
        return dictionaryDao.findById(id);
    }

    @Override
    public void create(String dictType, DictionaryItemForm request) {
        requireManagedType(dictType);
        DictItem item = new DictItem();
        item.setDictType(dictType);
        item.setDictLabel(request.getDictLabel());
        item.setDictValue(request.getDictValue());
        item.setSortNo(getByType(dictType).size() + 1);
        dictionaryDao.insert(item);
    }

    @Override
    public void update(Long id, DictionaryItemForm request) {
        DictItem existing = requireExisting(id);
        requireManagedType(existing.getDictType());
        existing.setDictLabel(request.getDictLabel());
        existing.setDictValue(request.getDictValue());
        dictionaryDao.update(existing);
    }

    @Override
    public void delete(Long id) {
        DictItem existing = requireExisting(id);
        requireManagedType(existing.getDictType());
        String blockMessage = switch (existing.getDictType()) {
            case "gender" -> buildBlockMessage(dictionaryDao.countGenderUsage(existing.getDictValue()), "该性别已被青年信息使用，无法删除");
            case "ethnicity" -> buildBlockMessage(dictionaryDao.countEthnicityUsage(existing.getDictValue()), "该民族已被青年信息使用，无法删除");
            case "political_status" ->
                    buildBlockMessage(dictionaryDao.countPoliticalStatusUsage(existing.getDictValue()), "该政治面貌已被青年信息使用，无法删除");
            case "education_level" ->
                    buildBlockMessage(dictionaryDao.countEducationLevelUsage(existing.getDictValue()), "该学历层次已被业务数据使用，无法删除");
            case "major_category" ->
                    buildBlockMessage(majorCatalogDao.countByCategoryId(existing.getId()), "该专业类别存在关联专业名称，无法删除");
            case "school_category" ->
                    buildBlockMessage(schoolDao.countByCategoryId(existing.getId()), "该学校类别存在关联学校，无法删除");
            case "enterprise_scale" ->
                    buildBlockMessage(dictionaryDao.countEnterpriseScaleUsage(existing.getDictValue()), "该企业规模已被企业信息使用，无法删除");
            case "enterprise_nature" ->
                    buildBlockMessage(dictionaryDao.countEnterpriseNatureUsage(existing.getDictValue()), "该企业性质已被企业信息使用，无法删除");
            case "enterprise_industry" ->
                    buildBlockMessage(dictionaryDao.countEnterpriseIndustryUsage(existing.getDictValue()), "该企业行业已被企业信息使用，无法删除");
            case "experience_requirement" ->
                    buildBlockMessage(dictionaryDao.countExperienceRequirementUsage(existing.getDictValue()), "该经验要求已被招聘岗位使用，无法删除");
            case "salary_range" ->
                    buildBlockMessage(dictionaryDao.countSalaryRangeUsage(existing.getDictValue()), "该薪资待遇已被招聘岗位使用，无法删除");
            default -> null;
        };
        if (blockMessage != null) {
            throw new IllegalStateException(blockMessage);
        }
        dictionaryDao.softDelete(id);
    }

    private DictItem requireExisting(Long id) {
        DictItem item = dictionaryDao.findById(id);
        if (item == null) {
            throw new IllegalStateException("字典项不存在或已删除");
        }
        return item;
    }

    private void requireManagedType(String dictType) {
        if (!MANAGED_DICT_TYPES.contains(dictType)) {
            throw new IllegalStateException("当前类型不支持在固定工作台中维护");
        }
    }

    private String buildBlockMessage(long usage, String message) {
        return usage > 0 ? message : null;
    }
}
