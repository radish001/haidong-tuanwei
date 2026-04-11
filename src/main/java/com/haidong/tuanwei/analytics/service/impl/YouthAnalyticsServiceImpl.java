package com.haidong.tuanwei.analytics.service.impl;

import com.haidong.tuanwei.analytics.dao.YouthAnalyticsDao;
import com.haidong.tuanwei.analytics.dto.TagSchoolStat;
import com.haidong.tuanwei.analytics.dto.TagChartView;
import com.haidong.tuanwei.analytics.dto.YouthAnalyticsView;
import com.haidong.tuanwei.analytics.entity.ChartItem;
import com.haidong.tuanwei.analytics.service.YouthAnalyticsService;
import com.haidong.tuanwei.system.service.MasterDataService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YouthAnalyticsServiceImpl implements YouthAnalyticsService {

    private static final String COLLEGE_YOUTH_TYPE = "COLLEGE";
    private static final String HAIDONG_CITY_CODE = "630200";

    private final YouthAnalyticsDao youthAnalyticsDao;
    private final MasterDataService masterDataService;

    @Override
    public YouthAnalyticsView getAnalytics(String youthType) {
        if (COLLEGE_YOUTH_TYPE.equalsIgnoreCase(youthType)) {
            return buildCollegeAnalytics(youthType);
        }
        return YouthAnalyticsView.builder()
                .ageDistribution(youthAnalyticsDao.countByAgeRange(youthType))
                .genderDistribution(youthAnalyticsDao.countByGender(youthType))
                .educationDistribution(youthAnalyticsDao.countByEducationLevel(youthType))
                .ethnicityDistribution(youthAnalyticsDao.countByEthnicity(youthType))
                .politicalStatusDistribution(countByColumn(youthType, "political_status"))
                .entrepreneurshipDemandDistribution(countByColumn(youthType, "entrepreneurship_demand"))
                .build();
    }

    private YouthAnalyticsView buildCollegeAnalytics(String youthType) {
        List<Long> configuredTagIds = masterDataService.getAnalyticsSchoolTagIds();
        List<TagChartView> tagCharts;
        if (configuredTagIds.isEmpty()) {
            tagCharts = List.of();
        } else {
            List<TagSchoolStat> tagSchoolStats = youthAnalyticsDao.countHaidongNativeSchoolsByTag(
                    youthType, HAIDONG_CITY_CODE, configuredTagIds);
            tagCharts = buildTagCharts(tagSchoolStats);
        }
        return YouthAnalyticsView.builder()
                .schoolCategoryDistribution(youthAnalyticsDao.countBySchoolCategory(youthType))
                .majorCategoryDistribution(youthAnalyticsDao.countByMajorCategory(youthType))
                .genderDistribution(youthAnalyticsDao.countByGender(youthType))
                .educationDistribution(youthAnalyticsDao.countByEducationLevel(youthType))
                .ethnicityDistribution(youthAnalyticsDao.countByEthnicity(youthType))
                .haidongSchoolTagDistributions(tagCharts)
                .build();
    }

    private List<ChartItem> countByColumn(String youthType, String column) {
        return youthAnalyticsDao.countByColumn(youthType, column);
    }

    private List<TagChartView> buildTagCharts(List<TagSchoolStat> tagSchoolStats) {
        Map<String, List<ChartItem>> chartsByTag = new LinkedHashMap<>();
        if (tagSchoolStats == null) {
            return List.of();
        }
        for (TagSchoolStat item : tagSchoolStats) {
            chartsByTag.computeIfAbsent(item.getTagName(), key -> new ArrayList<>())
                    .add(chartItem(item.getSchoolName(), item.getValue() == null ? 0 : item.getValue()));
        }
        List<TagChartView> charts = new ArrayList<>();
        for (Map.Entry<String, List<ChartItem>> entry : chartsByTag.entrySet()) {
            charts.add(TagChartView.builder()
                    .title(entry.getKey())
                    .data(entry.getValue())
                    .build());
        }
        return charts;
    }

    private ChartItem chartItem(String name, int value) {
        ChartItem item = new ChartItem();
        item.setName(name);
        item.setValue(value);
        return item;
    }
}
