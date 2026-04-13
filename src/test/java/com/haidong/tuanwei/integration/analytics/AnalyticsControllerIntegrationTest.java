package com.haidong.tuanwei.integration.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.haidong.tuanwei.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 数据分析模块集成测试
 */
class AnalyticsControllerIntegrationTest extends IntegrationTestBase {

    @Test
    void collegeAnalyticsShouldLoad() throws Exception {
        mockMvc.perform(get("/youth/college/analytics").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("analytics/index"))
                .andExpect(model().attributeExists("genderDistributionJson", "educationDistributionJson",
                        "ethnicityDistributionJson", "schoolCategoryDistributionJson", "majorCategoryDistributionJson",
                        "jobMajorDistributionJson"))
                .andExpect(model().attribute("pageTitle", "青年信息库"))
                .andExpect(model().attribute("youthType", "college"))
                .andExpect(model().attribute("youthTypeLabel", "在校大学生"))
                .andExpect(model().attribute("isCollegeAnalytics", true))
                .andExpect(model().attribute("analyticsTab", true));
    }

    @Test
    void graduateAnalyticsShouldLoad() throws Exception {
        mockMvc.perform(get("/youth/graduate/analytics").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("analytics/index"))
                .andExpect(model().attributeExists("ageDistributionJson", "genderDistributionJson",
                        "educationDistributionJson", "ethnicityDistributionJson"))
                .andExpect(model().attribute("pageTitle", "青年信息库"))
                .andExpect(model().attribute("youthType", "graduate"))
                .andExpect(model().attribute("youthTypeLabel", "毕业未就业"))
                .andExpect(model().attribute("isCollegeAnalytics", false));
    }

    @Test
    void ruralAnalyticsShouldLoad() throws Exception {
        mockMvc.perform(get("/youth/rural/analytics").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("analytics/index"))
                .andExpect(model().attributeExists("ageDistributionJson"))
                .andExpect(model().attribute("pageTitle", "青年信息库"))
                .andExpect(model().attribute("youthType", "rural"))
                .andExpect(model().attribute("youthTypeLabel", "农村社区待业"));
    }

    @Test
    void entrepreneurAnalyticsShouldLoad() throws Exception {
        mockMvc.perform(get("/youth/entrepreneur/analytics").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("analytics/index"))
                .andExpect(model().attributeExists("ageDistributionJson", "entrepreneurshipDemandDistributionJson"))
                .andExpect(model().attribute("pageTitle", "青年信息库"))
                .andExpect(model().attribute("youthType", "entrepreneur"))
                .andExpect(model().attribute("youthTypeLabel", "创业青年"));
    }

    @Test
    void collegeAnalyticsWithNoConfiguredTagsShouldReturnEmptyTagCharts() throws Exception {
        MvcResult result = mockMvc.perform(get("/youth/college/analytics").session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        String tagJson = (String) result.getModelAndView().getModel().get("haidongSchoolTagDistributionJson");
        assertThat(tagJson).isEqualTo("[]");
    }

    @Test
    void collegeAnalyticsWithConfiguredTagsShouldIncludeTagCharts() throws Exception {
        mockMvc.perform(post("/system/analytics/haidong-school-tags")
                        .session(adminSession)
                        .param("tagIds", "1", "3"))
                .andExpect(status().is3xxRedirection());

        MvcResult result = mockMvc.perform(get("/youth/college/analytics").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("haidongSchoolTagDistributionJson"))
                .andReturn();

        String tagJson = (String) result.getModelAndView().getModel().get("haidongSchoolTagDistributionJson");
        assertThat(tagJson).isNotNull();
    }

    @Test
    void collegeAnalyticsShouldExposeJobMajorAnalysisTopicAndJumpConfig() throws Exception {
        String analyticsJobName = "分析专业岗位-" + System.currentTimeMillis();
        mockMvc.perform(post("/jobs")
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", analyticsJobName)
                        .param("majorCodes", "080901"))
                .andExpect(status().is3xxRedirection());

        MvcResult result = mockMvc.perform(get("/youth/college/analytics").session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        String jobMajorJson = (String) result.getModelAndView().getModel().get("jobMajorDistributionJson");
        String html = result.getResponse().getContentAsString();

        assertThat(jobMajorJson).contains("080901");
        assertThat(html).contains("招聘信息专业分析");
        assertThat(html).contains("topN: 15");
        assertThat(html).contains("/jobs?majorCodes=");
    }

    @Test
    void legacyAnalyticsRouteShouldRedirectToYouthAnalyticsTab() throws Exception {
        mockMvc.perform(get("/analytics/college").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/youth/college/analytics"));
    }

}
