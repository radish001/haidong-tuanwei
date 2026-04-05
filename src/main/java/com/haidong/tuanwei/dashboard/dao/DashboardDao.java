package com.haidong.tuanwei.dashboard.dao;

import com.haidong.tuanwei.dashboard.entity.DashboardStats;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface DashboardDao {

    DashboardStats getStats();

    List<Map<String, Object>> getYouthDistributionByType();

    List<Map<String, Object>> getCollegeDistributionBySchoolProvince();

    List<Map<String, Object>> getCollegeDistributionByNativeCountyInHaidong();

    List<Map<String, Object>> getRecentPolicies(@Param("limit") int limit);

    List<Map<String, Object>> getRecentJobs(@Param("limit") int limit);
}
