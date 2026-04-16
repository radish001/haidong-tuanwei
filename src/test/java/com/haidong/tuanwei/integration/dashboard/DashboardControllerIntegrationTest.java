package com.haidong.tuanwei.integration.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
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
                        "collegeHaidongNativeCountyDistributionJson"))
                .andExpect(model().attribute("pageTitle", "首页"));
    }

    @Test
    void dashboardShouldNotRenderPolicyOrJobSummarySections() throws Exception {
        String html = mockMvc.perform(get("/dashboard").session(adminSession))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html).doesNotContain("<h2>政策信息</h2>");
        assertThat(html).doesNotContain("暂无政策摘要");
        assertThat(html).doesNotContain("<h2>招聘信息</h2>");
        assertThat(html).doesNotContain("暂无招聘摘要");
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
