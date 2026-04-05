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

    MajorCatalog findByName(@Param("majorName") String majorName);

    int insert(MajorCatalog majorCatalog);

    int update(MajorCatalog majorCatalog);

    int softDelete(@Param("id") Long id);

    long countByCategoryId(@Param("categoryDictItemId") Long categoryDictItemId);

    long countYouthUsageByMajorName(@Param("majorName") String majorName);

    int updateYouthReferences(@Param("oldMajorName") String oldMajorName,
            @Param("newMajorName") String newMajorName,
            @Param("newMajorCategory") String newMajorCategory);
}
