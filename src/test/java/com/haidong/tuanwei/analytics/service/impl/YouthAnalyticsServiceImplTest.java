package com.haidong.tuanwei.analytics.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.haidong.tuanwei.analytics.dao.YouthAnalyticsDao;
import com.haidong.tuanwei.analytics.dto.TagChartView;
import com.haidong.tuanwei.analytics.dto.TagSchoolStat;
import com.haidong.tuanwei.analytics.dto.YouthAnalyticsView;
import com.haidong.tuanwei.analytics.entity.ChartItem;
import com.haidong.tuanwei.system.service.MasterDataService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class YouthAnalyticsServiceImplTest {

    private YouthAnalyticsDao youthAnalyticsDao;
    private MasterDataService masterDataService;
    private YouthAnalyticsServiceImpl youthAnalyticsService;

    @BeforeEach
    void setUp() {
        youthAnalyticsDao = mock(YouthAnalyticsDao.class);
        masterDataService = mock(MasterDataService.class);
        youthAnalyticsService = new YouthAnalyticsServiceImpl(youthAnalyticsDao, masterDataService);
    }

    @Test
    void getAnalyticsUsesCollegeDashboardDatasetsForCollegeType() {
        List<Long> configuredTagIds = List.of(1L, 3L);
        when(masterDataService.getAnalyticsSchoolTagIds()).thenReturn(configuredTagIds);
        when(youthAnalyticsDao.countBySchoolCategory("COLLEGE"))
                .thenReturn(List.of(chartItem("双一流", 3), chartItem("普通本科", 2)));
        when(youthAnalyticsDao.countByMajorCategory("COLLEGE"))
                .thenReturn(List.of(chartItem("工学", 4), chartItem("管理学", 1)));
        // 新增：本科专业类别分布
        when(youthAnalyticsDao.countByMajorCategoryForUndergraduate("COLLEGE"))
                .thenReturn(List.of(chartItem("工学", 3), chartItem("理学", 2)));
        // 新增：专科专业类别分布
        when(youthAnalyticsDao.countByMajorCategoryForJuniorCollege("COLLEGE"))
                .thenReturn(List.of(chartItem("电子信息", 2), chartItem("医药卫生", 1)));
        when(youthAnalyticsDao.countByGender("COLLEGE"))
                .thenReturn(List.of(chartItem("男", 3), chartItem("女", 2)));
        when(youthAnalyticsDao.countByEducationLevel("COLLEGE"))
                .thenReturn(List.of(chartItem("本科", 4), chartItem("专科", 1)));
        when(youthAnalyticsDao.countByEthnicity("COLLEGE"))
                .thenReturn(List.of(chartItem("汉族", 5)));
        when(youthAnalyticsDao.countHaidongNativeSchoolsByTag("COLLEGE", "630200", configuredTagIds))
                .thenReturn(List.of(
                        tagSchoolStat("985", "清华大学", 1),
                        tagSchoolStat("985", "北京大学", 1),
                        tagSchoolStat("双一流", "兰州大学", 2),
                        tagSchoolStat("双一流", "复旦大学", 1)));

        YouthAnalyticsView analytics = youthAnalyticsService.getAnalytics("COLLEGE");

        assertThat(analytics.getSchoolCategoryDistribution()).extracting(ChartItem::getName, ChartItem::getValue)
                .containsExactly(tuple("双一流", 3), tuple("普通本科", 2));
        assertThat(analytics.getMajorCategoryDistribution()).extracting(ChartItem::getName, ChartItem::getValue)
                .containsExactly(tuple("工学", 4), tuple("管理学", 1));
        // 验证新增本科专业类别分布
        assertThat(analytics.getUndergraduateMajorCategoryDistribution()).extracting(ChartItem::getName, ChartItem::getValue)
                .containsExactly(tuple("工学", 3), tuple("理学", 2));
        // 验证新增专科专业类别分布
        assertThat(analytics.getJuniorCollegeMajorCategoryDistribution()).extracting(ChartItem::getName, ChartItem::getValue)
                .containsExactly(tuple("电子信息", 2), tuple("医药卫生", 1));
        assertThat(analytics.getGenderDistribution()).extracting(ChartItem::getName).containsExactly("男", "女");
        assertThat(analytics.getEducationDistribution()).extracting(ChartItem::getName).containsExactly("本科", "专科");
        assertThat(analytics.getEthnicityDistribution()).extracting(ChartItem::getName).containsExactly("汉族");
        assertThat(analytics.getAgeDistribution()).isNull();
        assertThat(analytics.getPoliticalStatusDistribution()).isNull();
        assertThat(analytics.getEntrepreneurshipDemandDistribution()).isNull();

        assertThat(analytics.getHaidongSchoolTagDistributions()).hasSize(2);
        TagChartView firstTagChart = analytics.getHaidongSchoolTagDistributions().get(0);
        assertThat(firstTagChart.getTitle()).isEqualTo("985");
        assertThat(firstTagChart.getData()).extracting(ChartItem::getName, ChartItem::getValue)
                .containsExactly(tuple("清华大学", 1), tuple("北京大学", 1));
        TagChartView secondTagChart = analytics.getHaidongSchoolTagDistributions().get(1);
        assertThat(secondTagChart.getTitle()).isEqualTo("双一流");
        assertThat(secondTagChart.getData()).extracting(ChartItem::getName, ChartItem::getValue)
                .containsExactly(tuple("兰州大学", 2), tuple("复旦大学", 1));
    }

    @Test
    void getAnalyticsReturnsEmptyTagChartsWhenNoTagsConfigured() {
        when(masterDataService.getAnalyticsSchoolTagIds()).thenReturn(List.of());
        when(youthAnalyticsDao.countBySchoolCategory("COLLEGE")).thenReturn(List.of());
        when(youthAnalyticsDao.countByMajorCategory("COLLEGE")).thenReturn(List.of());
        when(youthAnalyticsDao.countByGender("COLLEGE")).thenReturn(List.of());
        when(youthAnalyticsDao.countByEducationLevel("COLLEGE")).thenReturn(List.of());
        when(youthAnalyticsDao.countByEthnicity("COLLEGE")).thenReturn(List.of());

        YouthAnalyticsView analytics = youthAnalyticsService.getAnalytics("COLLEGE");

        assertThat(analytics.getHaidongSchoolTagDistributions()).isEmpty();
    }

    @Test
    void getAnalyticsKeepsLegacyDatasetsForNonCollegeTypes() {
        when(youthAnalyticsDao.countByAgeRange("GRADUATED_UNEMPLOYED"))
                .thenReturn(List.of(chartItem("21-25岁", 2)));
        when(youthAnalyticsDao.countByColumn("GRADUATED_UNEMPLOYED", "gender"))
                .thenReturn(List.of(chartItem("男", 1), chartItem("女", 1)));
        when(youthAnalyticsDao.countByEducationLevel("GRADUATED_UNEMPLOYED"))
                .thenReturn(List.of(chartItem("本科", 2)));
        when(youthAnalyticsDao.countByColumn("GRADUATED_UNEMPLOYED", "ethnicity"))
                .thenReturn(List.of(chartItem("汉族", 2)));
        when(youthAnalyticsDao.countByColumn("GRADUATED_UNEMPLOYED", "political_status"))
                .thenReturn(List.of(chartItem("共青团员", 2)));
        when(youthAnalyticsDao.countByColumn("GRADUATED_UNEMPLOYED", "entrepreneurship_demand"))
                .thenReturn(List.of(chartItem("资金", 1)));

        YouthAnalyticsView analytics = youthAnalyticsService.getAnalytics("GRADUATED_UNEMPLOYED");

        assertThat(analytics.getAgeDistribution()).extracting(ChartItem::getName).containsExactly("21-25岁");
        assertThat(analytics.getPoliticalStatusDistribution()).extracting(ChartItem::getName).containsExactly("共青团员");
        assertThat(analytics.getEntrepreneurshipDemandDistribution()).extracting(ChartItem::getName).containsExactly("资金");
        assertThat(analytics.getSchoolCategoryDistribution()).isNull();
        assertThat(analytics.getMajorCategoryDistribution()).isNull();
        assertThat(analytics.getHaidongSchoolTagDistributions()).isNull();
    }

    private ChartItem chartItem(String name, int value) {
        ChartItem item = new ChartItem();
        item.setName(name);
        item.setValue(value);
        return item;
    }

    private TagSchoolStat tagSchoolStat(String tagName, String schoolName, int value) {
        TagSchoolStat item = new TagSchoolStat();
        item.setTagName(tagName);
        item.setSchoolName(schoolName);
        item.setValue(value);
        return item;
    }
}
