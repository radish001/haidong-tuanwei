package com.haidong.tuanwei.analytics.dao;

import com.haidong.tuanwei.analytics.entity.ChartItem;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface YouthAnalyticsDao {

    List<ChartItem> countByColumn(@Param("youthType") String youthType, @Param("column") String column);

    List<ChartItem> countByAgeRange(@Param("youthType") String youthType);
}
