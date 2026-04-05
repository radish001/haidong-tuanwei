package com.haidong.tuanwei.dashboard.service;

import com.haidong.tuanwei.dashboard.entity.DashboardStats;
import java.util.List;
import java.util.Map;

public interface DashboardService {

    DashboardStats getStats();

    List<Map<String, Object>> getYouthDistributionByType();

    List<Map<String, Object>> getCollegeDistributionBySchoolProvince();

    List<Map<String, Object>> getCollegeDistributionByNativeCountyInHaidong();

    List<Map<String, Object>> getRecentPolicies(int limit);

    List<Map<String, Object>> getRecentJobs(int limit);
}
