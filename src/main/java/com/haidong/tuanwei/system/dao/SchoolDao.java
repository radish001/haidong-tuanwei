package com.haidong.tuanwei.system.dao;

import com.haidong.tuanwei.system.entity.School;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SchoolDao {

    List<School> search(@Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long count(@Param("keyword") String keyword);

    List<School> findAll();

    School findById(@Param("id") Long id);

    School findByName(@Param("schoolName") String schoolName);

    List<Long> findTagIdsBySchoolId(@Param("schoolId") Long schoolId);

    int insert(School school);

    int update(School school);

    int softDelete(@Param("id") Long id);

    int deleteTagRelations(@Param("schoolId") Long schoolId);

    int insertTagRelation(@Param("schoolId") Long schoolId, @Param("tagId") Long tagId);

    long countByCategoryId(@Param("categoryDictItemId") Long categoryDictItemId);

    long countYouthUsageBySchoolName(@Param("schoolName") String schoolName);

    int updateYouthReferences(@Param("oldSchoolName") String oldSchoolName,
            @Param("newSchoolName") String newSchoolName);
}
