package com.haidong.tuanwei.analytics.service.impl;

import com.haidong.tuanwei.analytics.dao.YouthAnalyticsDao;
import com.haidong.tuanwei.analytics.dto.YouthAnalyticsView;
import com.haidong.tuanwei.analytics.entity.ChartItem;
import com.haidong.tuanwei.analytics.service.YouthAnalyticsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YouthAnalyticsServiceImpl implements YouthAnalyticsService {

    private final YouthAnalyticsDao youthAnalyticsDao;

    @Override
    public YouthAnalyticsView getAnalytics(String youthType) {
        return YouthAnalyticsView.builder()
                .ageDistribution(youthAnalyticsDao.countByAgeRange(youthType))
                .genderDistribution(countByColumn(youthType, "gender"))
                .educationDistribution(countByColumn(youthType, "education_level"))
                .ethnicityDistribution(countByColumn(youthType, "ethnicity"))
                .politicalStatusDistribution(countByColumn(youthType, "political_status"))
                .entrepreneurshipDemandDistribution(countByColumn(youthType, "entrepreneurship_demand"))
                .build();
    }

    private List<ChartItem> countByColumn(String youthType, String column) {
        return youthAnalyticsDao.countByColumn(youthType, column);
    }
}
