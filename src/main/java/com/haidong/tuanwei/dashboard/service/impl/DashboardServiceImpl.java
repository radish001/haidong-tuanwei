package com.haidong.tuanwei.dashboard.service.impl;

import com.haidong.tuanwei.dashboard.dao.DashboardDao;
import com.haidong.tuanwei.dashboard.entity.DashboardStats;
import com.haidong.tuanwei.dashboard.service.DashboardService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardDao dashboardDao;

    @Override
    public DashboardStats getStats() {
        return dashboardDao.getStats();
    }

    @Override
    public List<Map<String, Object>> getYouthDistributionByType() {
        return dashboardDao.getYouthDistributionByType();
    }

    @Override
    public List<Map<String, Object>> getCollegeDistributionBySchoolProvince() {
        return dashboardDao.getCollegeDistributionBySchoolProvince();
    }

    @Override
    public List<Map<String, Object>> getCollegeDistributionByNativeCountyInHaidong() {
        return dashboardDao.getCollegeDistributionByNativeCountyInHaidong();
    }

    @Override
    public List<Map<String, Object>> getRecentPolicies(int limit) {
        return dashboardDao.getRecentPolicies(limit);
    }

    @Override
    public List<Map<String, Object>> getRecentJobs(int limit) {
        return dashboardDao.getRecentJobs(limit);
    }
}
