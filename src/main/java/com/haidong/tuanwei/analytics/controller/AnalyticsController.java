package com.haidong.tuanwei.analytics.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haidong.tuanwei.analytics.dto.YouthAnalyticsView;
import com.haidong.tuanwei.analytics.service.YouthAnalyticsService;
import com.haidong.tuanwei.youth.support.YouthTypeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class AnalyticsController {

    private static final String COLLEGE_TYPE = "college";

    private final YouthAnalyticsService youthAnalyticsService;
    private final ObjectMapper objectMapper;

    @GetMapping("/analytics/{type}")
    public String redirectAnalyticsPage(@PathVariable String type) {
        return "redirect:/youth/" + type + "/analytics";
    }

    @GetMapping("/youth/{type}/analytics")
    public String analyticsPage(@PathVariable String type, Model model) {
        model.addAttribute("pageTitle", "青年信息库");
        model.addAttribute("youthType", type);
        model.addAttribute("youthTypeLabel", YouthTypeHelper.label(type));
        model.addAttribute("isCollegeAnalytics", COLLEGE_TYPE.equals(type));
        model.addAttribute("analyticsTab", true);
        YouthAnalyticsView analytics = youthAnalyticsService.getAnalytics(YouthTypeHelper.code(type));
        model.addAttribute("ageDistributionJson", toJson(analytics.getAgeDistribution()));
        model.addAttribute("schoolCategoryDistributionJson", toJson(analytics.getSchoolCategoryDistribution()));
        model.addAttribute("majorCategoryDistributionJson", toJson(analytics.getMajorCategoryDistribution()));
        model.addAttribute("jobMajorDistributionJson", toJson(analytics.getJobMajorDistribution()));
        model.addAttribute("genderDistributionJson", toJson(analytics.getGenderDistribution()));
        model.addAttribute("educationDistributionJson", toJson(analytics.getEducationDistribution()));
        model.addAttribute("ethnicityDistributionJson", toJson(analytics.getEthnicityDistribution()));
        model.addAttribute("politicalStatusDistributionJson", toJson(analytics.getPoliticalStatusDistribution()));
        model.addAttribute("entrepreneurshipDemandDistributionJson",
                toJson(analytics.getEntrepreneurshipDemandDistribution()));
        model.addAttribute("haidongSchoolTagDistributionJson", toJson(analytics.getHaidongSchoolTagDistributions()));
        return "analytics/index";
    }

    private String toJson(Object value) {
        if (value == null) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("图表数据序列化失败", e);
        }
    }
}
