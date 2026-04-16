package com.haidong.tuanwei.dashboard.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haidong.tuanwei.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "首页");
        model.addAttribute("stats", dashboardService.getStats());
        model.addAttribute("collegeSchoolProvinceDistributionJson",
                toJson(dashboardService.getCollegeDistributionBySchoolProvince()));
        model.addAttribute("collegeHaidongNativeCountyDistributionJson",
                toJson(dashboardService.getCollegeDistributionByNativeCountyInHaidong()));
        return "dashboard/index";
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("首页地图数据序列化失败", e);
        }
    }
}
