package com.haidong.tuanwei.system.dao;

import com.haidong.tuanwei.system.entity.DisplaySettings;
import org.apache.ibatis.annotations.Param;

public interface DisplaySettingsDao {

    DisplaySettings findFirst();

    int insert(DisplaySettings settings);

    int updateSortFieldVisible(@Param("id") Long id, @Param("sortFieldVisible") boolean sortFieldVisible);
}
