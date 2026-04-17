package com.haidong.tuanwei.analytics.dao;

import com.haidong.tuanwei.analytics.dto.TagSchoolStat;
import com.haidong.tuanwei.analytics.entity.ChartItem;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface YouthAnalyticsDao {

    List<ChartItem> countByColumn(@Param("youthType") String youthType, @Param("column") String column);

    List<ChartItem> countByAgeRange(@Param("youthType") String youthType);

    List<ChartItem> countByEducationLevel(@Param("youthType") String youthType);

    List<ChartItem> countByGender(@Param("youthType") String youthType);

    List<ChartItem> countByEthnicity(@Param("youthType") String youthType);

    List<ChartItem> countBySchoolCategory(@Param("youthType") String youthType);

    List<ChartItem> countByMajorCategory(@Param("youthType") String youthType);

    /**
     * 本科专业类别分布统计
     * 统计所属学历层次包含本科专业的专业类别分布
     */
    List<ChartItem> countByMajorCategoryForUndergraduate(@Param("youthType") String youthType);

    /**
     * 专科专业类别分布统计
     * 统计所属学历层次包含专科专业的专业类别分布
     */
    List<ChartItem> countByMajorCategoryForJuniorCollege(@Param("youthType") String youthType);

    List<ChartItem> countJobDemandByMajor();

    List<TagSchoolStat> countHaidongNativeSchoolsByTag(@Param("youthType") String youthType,
            @Param("haidongCityCode") String haidongCityCode,
            @Param("tagIds") List<Long> tagIds);
}
