package com.haidong.tuanwei.system.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AnalyticsSchoolTagDao {

    List<Long> findAllTagIds();

    int deleteAll();

    int deleteByTagId(@Param("tagId") Long tagId);

    int insert(@Param("tagId") Long tagId);
}
