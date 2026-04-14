package com.haidong.tuanwei.system.dao;

import com.haidong.tuanwei.system.entity.DictItem;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DictionaryDao {

    List<DictItem> findByType(@Param("dictType") String dictType);

    List<DictItem> searchByType(@Param("dictType") String dictType,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countByType(@Param("dictType") String dictType, @Param("keyword") String keyword);

    List<DictItem> findAll();

    DictItem findById(@Param("id") Long id);

    int insert(DictItem dictItem);

    int update(DictItem dictItem);

    int delete(@Param("id") Long id);

    long countEthnicityUsage(@Param("dictValue") String dictValue);

    long countPoliticalStatusUsage(@Param("dictValue") String dictValue);

    long countEducationLevelUsage(@Param("dictValue") String dictValue);

    long countDegreeUsage(@Param("dictValue") String dictValue);

    long countGenderUsage(@Param("dictValue") String dictValue);

    long countEnterpriseScaleUsage(@Param("dictValue") String dictValue);

    long countEnterpriseNatureUsage(@Param("dictValue") String dictValue);

    long countEnterpriseIndustryUsage(@Param("dictValue") String dictValue);

    long countExperienceRequirementUsage(@Param("dictValue") String dictValue);

    long countSalaryRangeUsage(@Param("dictValue") String dictValue);
}
