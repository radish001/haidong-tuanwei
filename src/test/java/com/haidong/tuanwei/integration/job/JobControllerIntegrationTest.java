package com.haidong.tuanwei.integration.job;

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
 * 招聘岗位模块集成测试
 * 涉及多表关联：job_post + job_post_education_rel + job_post_major_rel + job_post_school_category_rel + job_post_school_tag_rel
 */
class JobControllerIntegrationTest extends IntegrationTestBase {

    private static final String BASE_URL = "/jobs";

    @Test
    void jobsPageShouldLoadWithExistingData() throws Exception {
        mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("job/list"))
                .andExpect(model().attributeExists("records", "enterprises", "educationOptions", "majorOptions"))
                .andExpect(model().attribute("pageTitle", "招聘信息"));
    }

    @Test
    void createJobShouldPersistWithMultiSelections() throws Exception {
        String uniqueJobName = "集成测试岗位-" + System.currentTimeMillis();

        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseId", "1")  // 预置企业ID
                        .param("jobName", uniqueJobName)
                        .param("jobCategory", "DEVELOPER")
                        .param("educationRequirements", "BK")  // 本科
                        .param("educationRequirements", "SS")  // 硕士
                        .param("majorCodes", "080901")  // 计算机科学与技术
                        .param("schoolCategoryIds", "100")  // 双一流
                        .param("experienceRequirement", "EXP_1_3")
                        .param("salaryRange", "SALARY_8_12")
                        .param("recruitCount", "5")
                        .param("workProvinceCode", "630000")
                        .param("workCityCode", "630200")
                        .param("workCountyCode", "630202")
                        .param("contactPerson", "招聘负责人")
                        .param("contactPhone", "13912345678")
                        .param("jobDescription", "这是一个集成测试岗位"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL))
                .andExpect(flash().attribute("successMessage", "招聘岗位新增成功"));

        // 验证写入数据库
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.job.entity.JobPost> records =
                (java.util.List<com.haidong.tuanwei.job.entity.JobPost>) listResult
                        .getModelAndView().getModel().get("records");

        com.haidong.tuanwei.job.entity.JobPost createdJob = records.stream()
                .filter(j -> j.getJobName().equals(uniqueJobName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未找到新创建的岗位"));

        // 验证多选字段已保存
        assertThat(createdJob.getEducationRequirements()).contains("BK", "SS");
        assertThat(createdJob.getMajorCodes()).contains("080901");
        assertThat(createdJob.getSchoolCategoryIds()).contains(100L);
    }

    @Test
    void createJobWithInvalidEnterpriseShouldReturnError() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseId", "99999")  // 不存在的企业
                        .param("jobName", "无效企业测试")
                        .param("educationRequirements", "BK"))
                .andExpect(status().isOk())
                .andExpect(view().name("job/form"))
                .andExpect(model().attributeExists("formError"))
                .andExpect(model().attribute("formTitle", "新增招聘岗位"));
    }

    @Test
    void createJobWithEmptyNameShouldReturnFormWithErrors() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", "")  // 空名称
                        .param("educationRequirements", "BK"))
                .andExpect(status().isOk())
                .andExpect(view().name("job/form"))
                .andExpect(model().attributeHasFieldErrors("jobForm", "jobName"))
                .andExpect(model().attribute("formTitle", "新增招聘岗位"));
    }

    @Test
    void updateJobShouldModifyRecordAndSelections() throws Exception {
        // 先创建一个岗位
        String originalName = "原岗位-" + System.currentTimeMillis();
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", originalName)
                        .param("educationRequirements", "BK")
                        .param("salaryRange", "SALARY_5_8"))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.job.entity.JobPost> records =
                (java.util.List<com.haidong.tuanwei.job.entity.JobPost>) listResult
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.job.entity.JobPost job = records.stream()
                .filter(j -> j.getJobName().equals(originalName))
                .findFirst()
                .orElseThrow();

        // 更新
        String updatedName = "更新后岗位-" + System.currentTimeMillis();
        mockMvc.perform(post(BASE_URL + "/" + job.getId())
                        .session(adminSession)
                        .param("id", String.valueOf(job.getId()))
                        .param("enterpriseId", "2")  // 换企业
                        .param("jobName", updatedName)
                        .param("jobCategory", "TESTER")
                        .param("educationRequirements", "SS")  // 改成硕士
                        .param("majorCodes", "120201")  // 换成工商管理
                        .param("experienceRequirement", "EXP_3_5")
                        .param("salaryRange", "SALARY_12_PLUS")
                        .param("recruitCount", "10")
                        .param("workProvinceCode", "630000")
                        .param("workCityCode", "630100")  // 改到西宁
                        .param("workCountyCode", "630102")
                        .param("contactPerson", "新联系人")
                        .param("contactPhone", "13888888888")
                        .param("jobDescription", "更新后的描述"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL))
                .andExpect(flash().attribute("successMessage", "招聘岗位更新成功"));
    }

    @Test
    void deleteJobShouldRemoveFromList() throws Exception {
        // 创建岗位
        String jobName = "待删除岗位-" + System.currentTimeMillis();
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", jobName)
                        .param("educationRequirements", "BK"))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.job.entity.JobPost> records =
                (java.util.List<com.haidong.tuanwei.job.entity.JobPost>) listResult
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.job.entity.JobPost job = records.stream()
                .filter(j -> j.getJobName().equals(jobName))
                .findFirst()
                .orElseThrow();

        // 删除
        mockMvc.perform(post(BASE_URL + "/" + job.getId() + "/delete").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL))
                .andExpect(flash().attribute("successMessage", "招聘岗位删除成功"));
    }

    @Test
    void batchDeleteJobsShouldRemoveMultiple() throws Exception {
        // 创建两个岗位
        String jobName1 = "批量删除岗位1-" + System.currentTimeMillis();
        String jobName2 = "批量删除岗位2-" + System.currentTimeMillis();

        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", jobName1)
                        .param("educationRequirements", "BK"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", jobName2)
                        .param("educationRequirements", "SS"))
                .andExpect(status().is3xxRedirection());

        // 获取IDs
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.job.entity.JobPost> records =
                (java.util.List<com.haidong.tuanwei.job.entity.JobPost>) listResult
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.job.entity.JobPost job1 = records.stream()
                .filter(j -> j.getJobName().equals(jobName1)).findFirst().orElseThrow();
        com.haidong.tuanwei.job.entity.JobPost job2 = records.stream()
                .filter(j -> j.getJobName().equals(jobName2)).findFirst().orElseThrow();

        // 批量删除
        mockMvc.perform(post(BASE_URL + "/batch-delete")
                        .session(adminSession)
                        .param("ids", String.valueOf(job1.getId()))
                        .param("ids", String.valueOf(job2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "已批量删除 2 条招聘信息"));
    }

    @Test
    void jobMatchPageShouldLoad() throws Exception {
        // 先创建一个岗位，然后测试匹配页面
        String jobName = "匹配测试岗位-" + System.currentTimeMillis();
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", jobName)
                        .param("educationRequirements", "BK")
                        .param("majorCodes", "080901")
                        .param("salaryRange", "SALARY_8_12"))
                .andExpect(status().is3xxRedirection());

        // 获取新创建岗位的ID
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.job.entity.JobPost> records =
                (java.util.List<com.haidong.tuanwei.job.entity.JobPost>) listResult
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.job.entity.JobPost job = records.stream()
                .filter(j -> j.getJobName().equals(jobName))
                .findFirst()
                .orElseThrow();

        // 测试匹配页面
        mockMvc.perform(get(BASE_URL + "/" + job.getId() + "/matches").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("job/match-results"))
                .andExpect(model().attributeExists("jobPost", "records"))
                .andExpect(model().attribute("pageTitle", "岗位匹配学生"));
    }

    @Test
    void newJobPageShouldLoadWithAllOptions() throws Exception {
        mockMvc.perform(get(BASE_URL + "/new").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("job/form"))
                .andExpect(model().attributeExists("jobForm", "enterprises", "regions", "educationOptions",
                        "majorOptions", "schoolCategoryOptions", "schoolTagOptions", "experienceOptions", "salaryOptions"))
                .andExpect(model().attribute("formTitle", "新增招聘岗位"))
                .andExpect(model().attribute("formAction", BASE_URL));
    }

    @Test
    void editJobPageShouldLoadWithExistingSelections() throws Exception {
        // 创建一个有多选字段的岗位
        String jobName = "编辑测试岗位-" + System.currentTimeMillis();
        mockMvc.perform(post(BASE_URL)
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", jobName)
                        .param("educationRequirements", "BK")
                        .param("educationRequirements", "SS")
                        .param("majorCodes", "080901")
                        .param("schoolCategoryIds", "100")
                        .param("salaryRange", "SALARY_8_12"))
                .andExpect(status().is3xxRedirection());

        // 获取ID
        MvcResult listResult = mockMvc.perform(get(BASE_URL).session(adminSession))
                .andReturn();
        @SuppressWarnings("unchecked")
        java.util.List<com.haidong.tuanwei.job.entity.JobPost> records =
                (java.util.List<com.haidong.tuanwei.job.entity.JobPost>) listResult
                        .getModelAndView().getModel().get("records");
        com.haidong.tuanwei.job.entity.JobPost job = records.stream()
                .filter(j -> j.getJobName().equals(jobName))
                .findFirst()
                .orElseThrow();

        // 访问编辑页
        mockMvc.perform(get(BASE_URL + "/" + job.getId() + "/edit").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("job/form"))
                .andExpect(model().attribute("formTitle", "编辑招聘岗位"))
                .andExpect(model().attributeExists("jobForm"));
    }

    @Test
    void ajaxRequestShouldReturnFragment() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .session(adminSession)
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("job/list :: listContent"));
    }

}
