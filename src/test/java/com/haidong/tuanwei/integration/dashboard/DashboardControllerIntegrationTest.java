package com.haidong.tuanwei.integration.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.haidong.tuanwei.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

/**
 * 仪表板模块集成测试
 */
class DashboardControllerIntegrationTest extends IntegrationTestBase {

    @Test
    void dashboardShouldLoadWithStats() throws Exception {
        mockMvc.perform(get("/dashboard").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attributeExists("stats", "collegeSchoolProvinceDistributionJson",
                        "collegeHaidongNativeCountyDistributionJson", "recentPolicies", "recentJobs"))
                .andExpect(model().attribute("pageTitle", "首页"));
    }

    @Test
    void homeShouldRedirectToDashboardWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void homeShouldRedirectToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

}
