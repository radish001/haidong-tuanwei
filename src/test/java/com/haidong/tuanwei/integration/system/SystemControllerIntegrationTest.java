package com.haidong.tuanwei.integration.system;

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
 * 系统管理模块集成测试
 */
class SystemControllerIntegrationTest extends IntegrationTestBase {

    private static final String BASE_URL = "/system/dictionaries";

    @Test
    void dictionariesPageDefaultTabShouldWork() throws Exception {
        // 默认页签处理已在normalizeQuery中完成，可能返回200或重定向
        mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk());
    }

    @Test
    void dictionariesPageWithGenderSectionShouldLoad() throws Exception {
        mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "common")
                        .param("section", "gender"))
                .andExpect(status().isOk())
                .andExpect(view().name("system/dictionaries"))
                .andExpect(model().attributeExists("records"))
                .andExpect(model().attribute("viewKind", "dict"))
                .andExpect(model().attribute("pageTitle", "基础数据管理"));
    }

    @Test
    void dictionariesPageWithRegionSectionShouldLoad() throws Exception {
        mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "common")
                        .param("section", "region")
                        .param("regionLevel", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("system/dictionaries"))
                .andExpect(model().attributeExists("records"))
                .andExpect(model().attribute("viewKind", "region"));
    }

    @Test
    void createDictionaryItemShouldPersist() throws Exception {
        String uniqueValue = "TEST_" + System.currentTimeMillis();

        mockMvc.perform(post("/system/dictionaries/items")
                        .session(adminSession)
                        .param("tab", "common")
                        .param("section", "gender")
                        .param("dictLabel", "测试性别")
                        .param("dictValue", uniqueValue))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "性别新增成功"));

        // 验证
        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "common")
                        .param("section", "gender"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.system.entity.DictItem> records =
                (java.util.List<com.haidong.tuanwei.system.entity.DictItem>) result
                        .getModelAndView().getModel().get("records");

        assertThat(records)
                .extracting(com.haidong.tuanwei.system.entity.DictItem::getDictValue)
                .contains(uniqueValue);
    }

    @Test
    void updateDictionaryItemShouldModify() throws Exception {
        // 先创建
        String originalValue = "UPDATE_TEST_" + System.currentTimeMillis();
        mockMvc.perform(post("/system/dictionaries/items")
                        .session(adminSession)
                        .param("tab", "common")
                        .param("section", "political_status")
                        .param("dictLabel", "原政治面貌")
                        .param("dictValue", originalValue))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "common")
                        .param("section", "political_status"))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.system.entity.DictItem> records =
                (java.util.List<com.haidong.tuanwei.system.entity.DictItem>) result
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.system.entity.DictItem item = records.stream()
                .filter(r -> r.getDictValue().equals(originalValue))
                .findFirst()
                .orElseThrow();

        // 更新
        mockMvc.perform(post("/system/dictionaries/items/" + item.getId())
                        .session(adminSession)
                        .param("tab", "common")
                        .param("section", "political_status")
                        .param("id", String.valueOf(item.getId()))
                        .param("dictLabel", "更新后政治面貌")
                        .param("dictValue", originalValue))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "政治面貌更新成功"));
    }

    @Test
    void deleteDictionaryItemShouldRemove() throws Exception {
        // 创建
        String value = "DELETE_TEST_" + System.currentTimeMillis();
        mockMvc.perform(post("/system/dictionaries/items")
                        .session(adminSession)
                        .param("tab", "common")
                        .param("section", "ethnicity")
                        .param("dictLabel", "测试民族")
                        .param("dictValue", value))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "common")
                        .param("section", "ethnicity"))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.system.entity.DictItem> records =
                (java.util.List<com.haidong.tuanwei.system.entity.DictItem>) result
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.system.entity.DictItem item = records.stream()
                .filter(r -> r.getDictValue().equals(value))
                .findFirst()
                .orElseThrow();

        // 删除
        mockMvc.perform(post("/system/dictionaries/items/" + item.getId() + "/delete")
                        .session(adminSession)
                        .param("tab", "common")
                        .param("section", "ethnicity"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "民族删除成功"));
    }

    @Test
    void majorFormPagesShouldLoad() throws Exception {
        // 测试新增页面加载
        mockMvc.perform(get("/system/majors/new").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("system/major-form"))
                .andExpect(model().attributeExists("majorForm", "majorCategories"));

        // 测试编辑页面加载
        mockMvc.perform(get("/system/majors/1/edit").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("system/major-form"))
                .andExpect(model().attribute("formTitle", "编辑专业名称"));
    }

    @Test
    void createSchoolTagShouldPersist() throws Exception {
        String tagName = "测试标签-" + System.currentTimeMillis();

        mockMvc.perform(post("/system/school-tags")
                        .session(adminSession)
                        .param("tagName", tagName))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "学校标签新增成功"));

        // 验证
        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "school-category")
                        .param("section", "school-tag"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.system.entity.SchoolTag> records =
                (java.util.List<com.haidong.tuanwei.system.entity.SchoolTag>) result
                        .getModelAndView().getModel().get("records");

        assertThat(records)
                .extracting(com.haidong.tuanwei.system.entity.SchoolTag::getTagName)
                .contains(tagName);
    }

    @Test
    void updateSchoolTagShouldModify() throws Exception {
        // 创建一个标签
        String tagName = "UPDATE_TAG-" + System.currentTimeMillis();
        mockMvc.perform(post("/system/school-tags")
                        .session(adminSession)
                        .param("tagName", tagName))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "school-category")
                        .param("section", "school-tag"))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.system.entity.SchoolTag> records =
                (java.util.List<com.haidong.tuanwei.system.entity.SchoolTag>) result
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.system.entity.SchoolTag tag = records.stream()
                .filter(r -> r.getTagName().equals(tagName))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/system/school-tags/" + tag.getId())
                        .session(adminSession)
                        .param("id", String.valueOf(tag.getId()))
                        .param("tagName", "更新后标签"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "学校标签更新成功"));
    }

    @Test
    void deleteSchoolTagShouldRemove() throws Exception {
        // 创建
        String tagName = "DEL_TAG-" + System.currentTimeMillis();
        mockMvc.perform(post("/system/school-tags")
                        .session(adminSession)
                        .param("tagName", tagName))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "school-category")
                        .param("section", "school-tag"))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.system.entity.SchoolTag> records =
                (java.util.List<com.haidong.tuanwei.system.entity.SchoolTag>) result
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.system.entity.SchoolTag tag = records.stream()
                .filter(r -> r.getTagName().equals(tagName))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/system/school-tags/" + tag.getId() + "/delete").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "学校标签删除成功"));
    }

    @Test
    void createSchoolShouldPersist() throws Exception {
        String code = "SCH" + System.currentTimeMillis();

        mockMvc.perform(post("/system/schools")
                        .session(adminSession)
                        .param("schoolCode", code)
                        .param("schoolName", "测试学校" + code)
                        .param("categoryDictItemId", "100")  // 双一流
                        .param("tagIds", "1"))  // 985标签
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "学校新增成功"));

        // 验证
        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "school")
                        .param("section", "school"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.system.entity.School> records =
                (java.util.List<com.haidong.tuanwei.system.entity.School>) result
                        .getModelAndView().getModel().get("records");

        assertThat(records)
                .extracting(com.haidong.tuanwei.system.entity.School::getSchoolCode)
                .contains(code);
    }

    @Test
    void updateSchoolShouldModify() throws Exception {
        // 先创建一个新学校再更新，避免标签重复
        String code = "UPDSCH" + System.currentTimeMillis();
        mockMvc.perform(post("/system/schools")
                        .session(adminSession)
                        .param("schoolCode", code)
                        .param("schoolName", "原学校名")
                        .param("categoryDictItemId", "100")
                        .param("tagIds", "2"))  // 用211标签
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "school")
                        .param("section", "school"))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.system.entity.School> records =
                (java.util.List<com.haidong.tuanwei.system.entity.School>) result
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.system.entity.School school = records.stream()
                .filter(r -> r.getSchoolCode().equals(code))
                .findFirst()
                .orElseThrow();

        // 更新 - 只用标签3（双一流），避免与预置数据重复
        mockMvc.perform(post("/system/schools/" + school.getId())
                        .session(adminSession)
                        .param("id", String.valueOf(school.getId()))
                        .param("schoolCode", code)
                        .param("schoolName", "更新后学校名称")
                        .param("categoryDictItemId", "100")
                        .param("tagIds", "3"))  // 双一流标签
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "学校更新成功"));
    }

    @Test
    void createRegionShouldPersist() throws Exception {
        String regionCode = "999" + System.currentTimeMillis();

        mockMvc.perform(post("/system/regions")
                        .session(adminSession)
                        .param("regionCode", regionCode)
                        .param("regionName", "测试区域")
                        .param("regionLevel", "1")
                        .param("parentId", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "区域新增成功"));
    }

    @Test
    void updateRegionShouldModify() throws Exception {
        // 使用预置的区域ID=1
        mockMvc.perform(post("/system/regions/1")
                        .session(adminSession)
                        .param("id", "1")
                        .param("regionCode", "630000")
                        .param("regionName", "更新后区域名")
                        .param("regionLevel", "1")
                        .param("parentId", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "区域更新成功"));
    }

    @Test
    void apiDictionariesShouldReturnJson() throws Exception {
        mockMvc.perform(get("/api/dictionaries/gender").session(adminSession))
                .andExpect(status().isOk());
    }

    @Test
    void apiRegionsShouldReturnJson() throws Exception {
        mockMvc.perform(get("/api/regions").session(adminSession))
                .andExpect(status().isOk());
    }

    @Test
    void regionsRedirectShouldWork() throws Exception {
        mockMvc.perform(get("/system/regions").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "?tab=common&section=region"));
    }

    @Test
    void analyticsTabShouldLoadWithSchoolTags() throws Exception {
        mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "analytics"))
                .andExpect(status().isOk())
                .andExpect(view().name("system/dictionaries"))
                .andExpect(model().attribute("viewKind", "analytics-haidong-tag"))
                .andExpect(model().attributeExists("allSchoolTags", "selectedTagIds"));
    }

    @Test
    void saveAnalyticsSchoolTagsShouldPersistSelection() throws Exception {
        mockMvc.perform(post("/system/analytics/haidong-school-tags")
                        .session(adminSession)
                        .param("tagIds", "1", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "海东籍顶尖高校分析配置保存成功"));

        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "analytics"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<Long> selectedTagIds =
                (java.util.List<Long>) result.getModelAndView().getModel().get("selectedTagIds");

        assertThat(selectedTagIds).containsExactlyInAnyOrder(1L, 3L);
    }

    @Test
    void saveAnalyticsSchoolTagsWithEmptySelectionShouldClear() throws Exception {
        // first save some
        mockMvc.perform(post("/system/analytics/haidong-school-tags")
                        .session(adminSession)
                        .param("tagIds", "1", "2"))
                .andExpect(status().is3xxRedirection());

        // then clear
        mockMvc.perform(post("/system/analytics/haidong-school-tags")
                        .session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "海东籍顶尖高校分析配置保存成功"));

        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "analytics"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<Long> selectedTagIds =
                (java.util.List<Long>) result.getModelAndView().getModel().get("selectedTagIds");

        assertThat(selectedTagIds).isEmpty();
    }

    @Test
    void saveAnalyticsSchoolTagsShouldIgnoreInvalidTagIds() throws Exception {
        mockMvc.perform(post("/system/analytics/haidong-school-tags")
                        .session(adminSession)
                        .param("tagIds", "1", "99999"))
                .andExpect(status().is3xxRedirection());

        MvcResult result = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "analytics"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<Long> selectedTagIds =
                (java.util.List<Long>) result.getModelAndView().getModel().get("selectedTagIds");

        assertThat(selectedTagIds).containsExactly(1L);
    }

    @Test
    void deleteSchoolTagShouldCascadeRemoveFromAnalyticsConfig() throws Exception {
        String tagName = "CASCADE_TAG-" + System.currentTimeMillis();
        mockMvc.perform(post("/system/school-tags")
                        .session(adminSession)
                        .param("tagName", tagName))
                .andExpect(status().is3xxRedirection());

        MvcResult tagResult = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "school-category")
                        .param("section", "school-tag"))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.system.entity.SchoolTag> tags =
                (java.util.List<com.haidong.tuanwei.system.entity.SchoolTag>) tagResult
                        .getModelAndView().getModel().get("records");
        Long newTagId = tags.stream()
                .filter(r -> r.getTagName().equals(tagName))
                .findFirst()
                .orElseThrow()
                .getId();

        mockMvc.perform(post("/system/analytics/haidong-school-tags")
                        .session(adminSession)
                        .param("tagIds", String.valueOf(newTagId)))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/system/school-tags/" + newTagId + "/delete").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "学校标签删除成功"));

        MvcResult analyticsResult = mockMvc.perform(get(BASE_URL).session(adminSession)
                        .param("tab", "analytics"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<Long> selectedTagIds =
                (java.util.List<Long>) analyticsResult.getModelAndView().getModel().get("selectedTagIds");

        assertThat(selectedTagIds).doesNotContain(newTagId);
    }

}
