package com.haidong.tuanwei.integration.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.haidong.tuanwei.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 政策管理模块集成测试
 */
class PolicyControllerIntegrationTest extends IntegrationTestBase {

    private static final String BASE_URL = "/policies";

    @Test
    void policiesPageShouldLoad() throws Exception {
        mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("policy/list"))
                .andExpect(model().attributeExists("records"))
                .andExpect(model().attribute("pageTitle", "政策管理"));
    }

    @Test
    void createPolicyShouldPersist() throws Exception {
        String uniqueTitle = "测试政策-" + System.currentTimeMillis();

        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("title", uniqueTitle)
                        .param("issuingOrganization", "海东市团委")
                        .param("policySource", "市政府")
                        .param("summary", "这是一条测试政策的摘要")
                        .param("contentHtml", "<p>这是政策的详细内容</p>"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL))
                .andExpect(flash().attribute("successMessage", "政策文章新增成功"));

        // 验证已写入数据库
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle> records =
                (java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle>) listResult
                        .getModelAndView().getModel().get("records");

        assertThat(records)
                .extracting(com.haidong.tuanwei.policy.entity.PolicyArticle::getTitle)
                .contains(uniqueTitle);
    }

    @Test
    void createPolicyWithEmptyTitleShouldReturnFormWithErrors() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("title", "")
                        .param("contentHtml", "<p>内容</p>"))
                .andExpect(status().isOk())
                .andExpect(view().name("policy/form"))
                .andExpect(model().attributeHasFieldErrors("policyForm", "title"))
                .andExpect(model().attribute("formTitle", "新增政策文章"));
    }

    @Test
    void updatePolicyShouldModifyRecord() throws Exception {
        // 先创建一个政策
        String originalTitle = "原政策-" + System.currentTimeMillis();
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("title", originalTitle)
                        .param("contentHtml", "<p>原内容</p>"))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle> records =
                (java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle>) listResult
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.policy.entity.PolicyArticle policy = records.stream()
                .filter(p -> p.getTitle().equals(originalTitle))
                .findFirst()
                .orElseThrow();

        // 更新
        String updatedTitle = "更新后政策-" + System.currentTimeMillis();
        mockMvc.perform(post(BASE_URL + "/" + policy.getId())
                        .session(adminSession)
                        .param("id", String.valueOf(policy.getId()))
                        .param("title", updatedTitle)
                        .param("issuingOrganization", "更新后部门")
                        .param("contentHtml", "<p>更新后内容</p>"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL))
                .andExpect(flash().attribute("successMessage", "政策文章更新成功"));
    }

    @Test
    void updatePolicyStatusShouldTogglePublishState() throws Exception {
        // 创建政策
        String title = "状态测试政策-" + System.currentTimeMillis();
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("title", title)
                        .param("contentHtml", "<p>内容</p>"))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle> records =
                (java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle>) listResult
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.policy.entity.PolicyArticle policy = records.stream()
                .filter(p -> p.getTitle().equals(title))
                .findFirst()
                .orElseThrow();

        // 发布
        mockMvc.perform(post(BASE_URL + "/" + policy.getId() + "/status")
                        .session(adminSession)
                        .param("status", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "政策已发布"));

        // 下线
        mockMvc.perform(post(BASE_URL + "/" + policy.getId() + "/status")
                        .session(adminSession)
                        .param("status", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "政策已下线"));
    }

    @Test
    void deletePolicyShouldRemoveFromList() throws Exception {
        // 创建政策
        String title = "待删除政策-" + System.currentTimeMillis();
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("title", title)
                        .param("contentHtml", "<p>内容</p>"))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle> records =
                (java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle>) listResult
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.policy.entity.PolicyArticle policy = records.stream()
                .filter(p -> p.getTitle().equals(title))
                .findFirst()
                .orElseThrow();

        // 删除
        mockMvc.perform(post(BASE_URL + "/" + policy.getId() + "/delete").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL))
                .andExpect(flash().attribute("successMessage", "政策文章删除成功"));
    }

    @Test
    void batchDeletePoliciesShouldRemoveMultiple() throws Exception {
        // 创建两个政策
        String title1 = "批量删除1-" + System.currentTimeMillis();
        String title2 = "批量删除2-" + System.currentTimeMillis();

        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("title", title1)
                        .param("contentHtml", "<p>内容1</p>"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("title", title2)
                        .param("contentHtml", "<p>内容2</p>"))
                .andExpect(status().is3xxRedirection());

        // 获取IDs
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle> records =
                (java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle>) listResult
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.policy.entity.PolicyArticle policy1 = records.stream()
                .filter(p -> p.getTitle().equals(title1)).findFirst().orElseThrow();
        com.haidong.tuanwei.policy.entity.PolicyArticle policy2 = records.stream()
                .filter(p -> p.getTitle().equals(title2)).findFirst().orElseThrow();

        // 批量删除
        mockMvc.perform(post(BASE_URL + "/batch-delete")
                        .session(adminSession)
                        .param("ids", String.valueOf(policy1.getId()))
                        .param("ids", String.valueOf(policy2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "已批量删除 2 条政策文章"));
    }

    @Test
    void policyDetailPageShouldLoad() throws Exception {
        // 创建政策
        String title = "详情测试-" + System.currentTimeMillis();
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("title", title)
                        .param("contentHtml", "<p>详情内容</p>"))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle> records =
                (java.util.List<com.haidong.tuanwei.policy.entity.PolicyArticle>) listResult
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.policy.entity.PolicyArticle policy = records.stream()
                .filter(p -> p.getTitle().equals(title))
                .findFirst()
                .orElseThrow();

        // 访问详情页
        mockMvc.perform(get(BASE_URL + "/" + policy.getId()).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("policy/detail"))
                .andExpect(model().attributeExists("record"))
                .andExpect(model().attribute("pageTitle", "政策详情"));
    }

    @Test
    void newPolicyPageShouldLoad() throws Exception {
        mockMvc.perform(get(BASE_URL + "/new").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("policy/form"))
                .andExpect(model().attributeExists("policyForm"))
                .andExpect(model().attribute("formTitle", "新增政策文章"))
                .andExpect(model().attribute("formAction", BASE_URL));
    }

    @Test
    void ajaxRequestShouldReturnFragment() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .session(adminSession)
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("policy/list :: listContent"));
    }

}
