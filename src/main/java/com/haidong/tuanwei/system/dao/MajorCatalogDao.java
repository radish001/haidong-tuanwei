package com.haidong.tuanwei.system.dao;

import com.haidong.tuanwei.system.entity.MajorCatalog;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MajorCatalogDao {

    List<MajorCatalog> search(@Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long count(@Param("keyword") String keyword);

    List<MajorCatalog> findAll();

    MajorCatalog findById(@Param("id") Long id);

    MajorCatalog findByCode(@Param("majorCode") String majorCode);

    MajorCatalog findByName(@Param("majorName") String majorName);

    int insert(MajorCatalog majorCatalog);

    int update(MajorCatalog majorCatalog);

    int delete(@Param("id") Long id);

    long countByCategoryId(@Param("categoryDictItemId") Long categoryDictItemId);

    List<MajorCatalog> findByCategoryId(@Param("categoryDictItemId") Long categoryDictItemId);

    long countYouthUsageByMajorCode(@Param("majorCode") String majorCode);

    List<MajorCatalog> findExistingCodes(@Param("codes") List<String> codes);
}
