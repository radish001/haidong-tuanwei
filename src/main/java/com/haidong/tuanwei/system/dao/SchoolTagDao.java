package com.haidong.tuanwei.system.dao;

import com.haidong.tuanwei.system.entity.SchoolTag;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SchoolTagDao {

    List<SchoolTag> search(@Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long count(@Param("keyword") String keyword);

    List<SchoolTag> findAll();

    SchoolTag findById(@Param("id") Long id);

    SchoolTag findByName(@Param("tagName") String tagName);

    int insert(SchoolTag schoolTag);

    int update(SchoolTag schoolTag);

    int softDelete(@Param("id") Long id);

    long countSchoolUsage(@Param("tagId") Long tagId);
}
