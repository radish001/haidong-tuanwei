package com.haidong.tuanwei.integration.enterprise;

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
 * 企业模块集成测试
 * 测试链路：Controller -> Service -> DAO -> H2 Database
 * 每个测试方法在独立事务中执行，测试完成后自动回滚
 */
class EnterpriseControllerIntegrationTest extends IntegrationTestBase {

    private static final String BASE_URL = "/enterprises";

    /**
     * 测试：访问企业列表页面
     * 验证：页面返回正常，包含预置的企业数据
     */
    @Test
    void enterprisesPageShouldLoadWithExistingData() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("enterprise/list"))
                .andExpect(model().attributeExists("records", "industryOptions", "natureOptions", "scaleOptions"))
                .andExpect(model().attribute("pageTitle", "企业信息"))
                .andReturn();

        String html = result.getResponse().getContentAsString();
        int natureHeaderIndex = html.indexOf("<th>企业性质</th>");
        int industryHeaderIndex = html.indexOf("<th>行业</th>");

        assertThat(html).doesNotContain("<th>企业规模</th>");
        assertThat(natureHeaderIndex).isGreaterThanOrEqualTo(0);
        assertThat(industryHeaderIndex).isGreaterThanOrEqualTo(0);
        assertThat(natureHeaderIndex).isLessThan(industryHeaderIndex);
    }

    /**
     * 测试：新增企业完整流程
     * 1. POST /enterprises 提交表单
     * 2. 重定向到列表页
     * 3. GET /enterprises 验证新数据已写入
     * 4. POST /enterprises/{id}/delete 删除
     * 5. GET /enterprises 验证已删除
     */
    @Test
    void createEnterpriseShouldPersistAndBeQueryable() throws Exception {
        String uniqueName = "集成测试企业-" + System.currentTimeMillis();

        // Step 1: 提交新增表单
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseName", uniqueName)
                        .param("industry", "IT")
                        .param("enterpriseNature", "PRIVATE")
                        .param("enterpriseScale", "MEDIUM")
                        .param("regionProvinceCode", "630000")
                        .param("regionCityCode", "630200")
                        .param("regionCountyCode", "630202")
                        .param("address", "海东市乐都区测试地址")
                        .param("contactPerson", "测试联系人")
                        .param("contactPhone", "13912345678")
                        .param("sortOrder", "3")
                        .param("description", "这是集成测试创建的企业"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL))
                .andExpect(flash().attribute("successMessage", "企业信息新增成功"));

        // Step 2: 查询列表验证数据已写入
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("enterprise/list"))
                .andExpect(model().attributeExists("records"))
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo> records =
                (java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo>) listResult
                        .getModelAndView().getModel().get("records");

        assertThat(records)
                .extracting(com.haidong.tuanwei.enterprise.entity.EnterpriseInfo::getEnterpriseName)
                .contains(uniqueName);
        assertThat(records)
                .filteredOn(item -> uniqueName.equals(item.getEnterpriseName()))
                .extracting(com.haidong.tuanwei.enterprise.entity.EnterpriseInfo::getSortOrder)
                .containsExactly(3);

        // Step 3: 找到新创建的企业ID
        com.haidong.tuanwei.enterprise.entity.EnterpriseInfo createdEnterprise = records.stream()
                .filter(e -> e.getEnterpriseName().equals(uniqueName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未找到新创建的企业"));

        // Step 4: 访问编辑页面验证详情
        mockMvc.perform(get(BASE_URL + "/" + createdEnterprise.getId() + "/edit").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("enterprise/form"))
                .andExpect(model().attributeExists("enterpriseForm"))
                .andExpect(model().attribute("enterpriseForm",
                        org.hamcrest.Matchers.hasProperty("sortOrder", org.hamcrest.Matchers.is(3))))
                .andExpect(model().attribute("formTitle", "编辑企业信息"));
    }

    @Test
    void enterprisesPageShouldPrioritizeExplicitSortOrder() throws Exception {
        String sortedSecond = "排序企业2-" + System.currentTimeMillis();
        String sortedFirst = "排序企业1-" + System.currentTimeMillis();
        String unsorted = "排序企业空-" + System.currentTimeMillis();

        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseName", sortedSecond)
                        .param("sortOrder", "2"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseName", sortedFirst)
                        .param("sortOrder", "1"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseName", unsorted))
                .andExpect(status().is3xxRedirection());

        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo> records =
                (java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo>) listResult
                        .getModelAndView().getModel().get("records");
        java.util.List<String> names = records.stream()
                .map(com.haidong.tuanwei.enterprise.entity.EnterpriseInfo::getEnterpriseName)
                .toList();

        assertThat(names.indexOf(sortedFirst)).isGreaterThanOrEqualTo(0);
        assertThat(names.indexOf(sortedSecond)).isGreaterThanOrEqualTo(0);
        assertThat(names.indexOf(unsorted)).isGreaterThanOrEqualTo(0);
        assertThat(names.indexOf(sortedFirst)).isLessThan(names.indexOf(sortedSecond));
        assertThat(names.indexOf(sortedSecond)).isLessThan(names.indexOf(unsorted));
    }

    /**
     * 测试：表单验证失败时保持在表单页面
     * 验证：必填字段为空时返回表单页面并显示错误
     */
    @Test
    void createEnterpriseWithEmptyNameShouldReturnFormWithErrors() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseName", "")  // 空名称
                        .param("industry", "IT")
                        .param("enterpriseNature", "PRIVATE"))
                .andExpect(status().isOk())
                .andExpect(view().name("enterprise/form"))
                .andExpect(model().attributeHasFieldErrors("enterpriseForm", "enterpriseName"))
                .andExpect(model().attribute("formTitle", "新增企业信息"));
    }

    /**
     * 测试：更新企业信息
     * 验证：更新后数据正确保存
     */
    @Test
    void updateEnterpriseShouldModifyExistingRecord() throws Exception {
        // 使用预置的企业ID=1
        Long enterpriseId = 1L;
        String updatedAddress = "更新后的地址-" + System.currentTimeMillis();

        mockMvc.perform(post(BASE_URL + "/" + enterpriseId)
                        .session(adminSession)
                        .param("id", String.valueOf(enterpriseId))
                        .param("enterpriseName", "测试科技有限公司")  // 保持原名
                        .param("industry", "MANUFACTURING")  // 修改行业
                        .param("enterpriseNature", "SOE")  // 修改性质
                        .param("enterpriseScale", "LARGE")  // 修改规模
                        .param("regionProvinceCode", "630000")
                        .param("regionCityCode", "630100")  // 修改到西宁
                        .param("regionCountyCode", "630102")
                        .param("address", updatedAddress)
                        .param("contactPerson", "新联系人")
                        .param("contactPhone", "13888888888")
                        .param("description", "更新后的描述"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL))
                .andExpect(flash().attribute("successMessage", "企业信息更新成功"));

        // 验证更新后的数据
        mockMvc.perform(get(BASE_URL + "/" + enterpriseId + "/edit").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(model().attribute("enterpriseForm",
                        org.hamcrest.Matchers.hasProperty("address", org.hamcrest.Matchers.is(updatedAddress))));
    }

    @Test
    void sortFieldShouldHideAndExistingValueShouldBePreservedWhenSettingDisabled() throws Exception {
        String uniqueName = "隐藏排序企业-" + System.currentTimeMillis();

        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseName", uniqueName)
                        .param("industry", "IT")
                        .param("enterpriseNature", "PRIVATE")
                        .param("enterpriseScale", "MEDIUM")
                        .param("regionProvinceCode", "630000")
                        .param("regionCityCode", "630200")
                        .param("regionCountyCode", "630202")
                        .param("address", "原地址")
                        .param("contactPerson", "原联系人")
                        .param("contactPhone", "13912340000")
                        .param("sortOrder", "7"))
                .andExpect(status().is3xxRedirection());

        MvcResult createdList = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo> createdRecords =
                (java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo>) createdList
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.enterprise.entity.EnterpriseInfo created = createdRecords.stream()
                .filter(item -> uniqueName.equals(item.getEnterpriseName()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/system/display/sort-field-visibility")
                        .session(adminSession)
                        .param("sortFieldVisible", "false"))
                .andExpect(status().is3xxRedirection());

        String editHtml = mockMvc.perform(get(BASE_URL + "/" + created.getId() + "/edit").session(adminSession))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(editHtml).doesNotContain("name=\"sortOrder\"");

        String detailHtml = mockMvc.perform(get(BASE_URL + "/" + created.getId()).session(adminSession))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(detailHtml).doesNotContain("<span>排序</span>");

        mockMvc.perform(post(BASE_URL + "/" + created.getId())
                        .session(adminSession)
                        .param("id", String.valueOf(created.getId()))
                        .param("enterpriseName", uniqueName)
                        .param("industry", "IT")
                        .param("enterpriseNature", "PRIVATE")
                        .param("enterpriseScale", "MEDIUM")
                        .param("regionProvinceCode", "630000")
                        .param("regionCityCode", "630200")
                        .param("regionCountyCode", "630202")
                        .param("address", "更新后地址")
                        .param("contactPerson", "更新联系人")
                        .param("contactPhone", "13912340001"))
                .andExpect(status().is3xxRedirection());

        MvcResult updatedList = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo> updatedRecords =
                (java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo>) updatedList
                        .getModelAndView().getModel().get("records");
        assertThat(updatedRecords)
                .filteredOn(item -> uniqueName.equals(item.getEnterpriseName()))
                .extracting(com.haidong.tuanwei.enterprise.entity.EnterpriseInfo::getSortOrder)
                .containsExactly(7);
    }

    /**
     * 测试：删除企业
     * 验证：删除后不再出现在列表中
     */
    @Test
    void deleteEnterpriseShouldRemoveFromList() throws Exception {
        // 先创建一个新企业，然后删除它
        String uniqueName = "待删除企业-" + System.currentTimeMillis();

        // 创建企业
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseName", uniqueName)
                        .param("industry", "IT")
                        .param("enterpriseNature", "PRIVATE")
                        .param("enterpriseScale", "SMALL"))
                .andExpect(status().is3xxRedirection());

        // 获取新创建的企业ID
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo> records =
                (java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo>) listResult
                        .getModelAndView().getModel().get("records");

        com.haidong.tuanwei.enterprise.entity.EnterpriseInfo createdEnterprise = records.stream()
                .filter(e -> e.getEnterpriseName().equals(uniqueName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未找到新创建的企业"));

        // 删除企业
        mockMvc.perform(post(BASE_URL + "/" + createdEnterprise.getId() + "/delete").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL))
                .andExpect(flash().attribute("successMessage", "企业信息删除成功"));

        // 验证已删除（再次查询列表，不包含该名称）
        MvcResult finalListResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo> finalRecords =
                (java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo>) finalListResult
                        .getModelAndView().getModel().get("records");

        assertThat(finalRecords)
                .extracting(com.haidong.tuanwei.enterprise.entity.EnterpriseInfo::getEnterpriseName)
                .doesNotContain(uniqueName);
    }

    /**
     * 测试：批量删除企业
     * 验证：多条记录同时删除
     */
    @Test
    void batchDeleteEnterprisesShouldRemoveMultipleRecords() throws Exception {
        // 创建两个企业用于批量删除测试
        String name1 = "批量删除企业1-" + System.currentTimeMillis();
        String name2 = "批量删除企业2-" + System.currentTimeMillis();

        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseName", name1)
                        .param("industry", "IT")
                        .param("enterpriseNature", "PRIVATE")
                        .param("enterpriseScale", "SMALL"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseName", name2)
                        .param("industry", "FINANCE")
                        .param("enterpriseNature", "FOREIGN")
                        .param("enterpriseScale", "MICRO"))
                .andExpect(status().is3xxRedirection());

        // 获取新创建的ID
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo> records =
                (java.util.List<com.haidong.tuanwei.enterprise.entity.EnterpriseInfo>) listResult
                        .getModelAndView().getModel().get("records");

        com.haidong.tuanwei.enterprise.entity.EnterpriseInfo enterprise1 = records.stream()
                .filter(e -> e.getEnterpriseName().equals(name1))
                .findFirst()
                .orElseThrow();
        com.haidong.tuanwei.enterprise.entity.EnterpriseInfo enterprise2 = records.stream()
                .filter(e -> e.getEnterpriseName().equals(name2))
                .findFirst()
                .orElseThrow();

        // 批量删除
        mockMvc.perform(post(BASE_URL + "/batch-delete")
                        .session(adminSession)
                        .param("ids", String.valueOf(enterprise1.getId()))
                        .param("ids", String.valueOf(enterprise2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL))
                .andExpect(flash().attribute("successMessage", "已批量删除 2 条企业信息"));
    }

    /**
     * 测试：无效字典值抛出异常
     * 验证：使用不存在的行业代码时返回表单页面并显示错误
     */
    @Test
    void createEnterpriseWithInvalidIndustryShouldReturnError() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseName", "无效行业测试企业")
                        .param("industry", "INVALID_INDUSTRY")  // 无效值
                        .param("enterpriseNature", "PRIVATE")
                        .param("enterpriseScale", "SMALL"))
                .andExpect(status().isOk())
                .andExpect(view().name("enterprise/form"))
                .andExpect(model().attributeExists("formError"))
                .andExpect(model().attribute("formTitle", "新增企业信息"));
    }

    /**
     * 测试：新增页面正常加载
     * 验证：新增页面包含必要的下拉选项
     */
    @Test
    void newEnterprisePageShouldLoadWithOptions() throws Exception {
        mockMvc.perform(get(BASE_URL + "/new").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("enterprise/form"))
                .andExpect(model().attributeExists("enterpriseForm", "industryOptions", "natureOptions", "scaleOptions"))
                .andExpect(model().attribute("formTitle", "新增企业信息"))
                .andExpect(model().attribute("formAction", BASE_URL));
    }

    /**
     * 测试：AJAX请求返回片段
     * 验证：带 X-Requested-With: XMLHttpRequest 返回 Thymeleaf 片段
     */
    @Test
    void ajaxRequestShouldReturnFragment() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .session(adminSession)
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("enterprise/list :: listContent"));
    }

}
