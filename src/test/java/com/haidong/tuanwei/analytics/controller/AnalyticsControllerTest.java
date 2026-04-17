package com.haidong.tuanwei.analytics.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haidong.tuanwei.analytics.dto.YouthAnalyticsView;
import com.haidong.tuanwei.analytics.entity.ChartItem;
import com.haidong.tuanwei.analytics.service.YouthAnalyticsService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

class AnalyticsControllerTest {

    private YouthAnalyticsService youthAnalyticsService;
    private ObjectMapper objectMapper;
    private AnalyticsController analyticsController;

    @BeforeEach
    void setUp() {
        youthAnalyticsService = mock(YouthAnalyticsService.class);
        objectMapper = new ObjectMapper();
        analyticsController = new AnalyticsController(youthAnalyticsService, objectMapper);
    }

    @Test
    void analyticsPageAddsUndergraduateMajorCategoryDistributionJson() {
        Model model = new ExtendedModelMap();
        ChartItem chartItem = new ChartItem();
        chartItem.setName("工学");
        chartItem.setValue(4);

        YouthAnalyticsView analytics = YouthAnalyticsView.builder()
                .undergraduateMajorCategoryDistribution(List.of(chartItem))
                .juniorCollegeMajorCategoryDistribution(List.of())
                .schoolCategoryDistribution(List.of())
                .majorCategoryDistribution(List.of())
                .genderDistribution(List.of())
                .educationDistribution(List.of())
                .ethnicityDistribution(List.of())
                .build();

        when(youthAnalyticsService.getAnalytics("COLLEGE")).thenReturn(analytics);

        String result = analyticsController.analyticsPage("college", model);

        assertThat(result).isEqualTo("analytics/index");
        assertThat(model.getAttribute("undergraduateMajorCategoryDistributionJson")).isNotNull();
        assertThat(model.getAttribute("undergraduateMajorCategoryDistributionJson").toString())
                .contains("工学");
    }

    @Test
    void analyticsPageAddsJuniorCollegeMajorCategoryDistributionJson() {
        Model model = new ExtendedModelMap();
        ChartItem chartItem = new ChartItem();
        chartItem.setName("电子信息");
        chartItem.setValue(3);

        YouthAnalyticsView analytics = YouthAnalyticsView.builder()
                .juniorCollegeMajorCategoryDistribution(List.of(chartItem))
                .undergraduateMajorCategoryDistribution(List.of())
                .schoolCategoryDistribution(List.of())
                .majorCategoryDistribution(List.of())
                .genderDistribution(List.of())
                .educationDistribution(List.of())
                .ethnicityDistribution(List.of())
                .build();

        when(youthAnalyticsService.getAnalytics("COLLEGE")).thenReturn(analytics);

        String result = analyticsController.analyticsPage("college", model);

        assertThat(result).isEqualTo("analytics/index");
        assertThat(model.getAttribute("juniorCollegeMajorCategoryDistributionJson")).isNotNull();
        assertThat(model.getAttribute("juniorCollegeMajorCategoryDistributionJson").toString())
                .contains("电子信息");
    }

    @Test
    void analyticsPageSetsCollegeAnalyticsFlag() {
        Model model = new ExtendedModelMap();
        YouthAnalyticsView analytics = YouthAnalyticsView.builder()
                .undergraduateMajorCategoryDistribution(List.of())
                .juniorCollegeMajorCategoryDistribution(List.of())
                .schoolCategoryDistribution(List.of())
                .majorCategoryDistribution(List.of())
                .genderDistribution(List.of())
                .educationDistribution(List.of())
                .ethnicityDistribution(List.of())
                .build();

        when(youthAnalyticsService.getAnalytics("COLLEGE")).thenReturn(analytics);

        analyticsController.analyticsPage("college", model);

        assertThat(model.getAttribute("isCollegeAnalytics")).isEqualTo(true);
    }

    @Test
    void analyticsPageDoesNotSetCollegeAnalyticsFlagForOtherTypes() {
        Model model = new ExtendedModelMap();
        YouthAnalyticsView analytics = YouthAnalyticsView.builder()
                .build();

        when(youthAnalyticsService.getAnalytics("GRADUATED_UNEMPLOYED")).thenReturn(analytics);

        analyticsController.analyticsPage("graduated_unemployed", model);

        assertThat(model.getAttribute("isCollegeAnalytics")).isEqualTo(false);
    }
}
