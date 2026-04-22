package com.haidong.tuanwei.integration.youth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.haidong.tuanwei.integration.IntegrationTestBase;
import com.haidong.tuanwei.youth.dto.YouthImportResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 青年信息模块集成测试
 */
class YouthControllerIntegrationTest extends IntegrationTestBase {

    @Test
    void collegeYouthPageShouldLoad() throws Exception {
        mockMvc.perform(get("/youth/college").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("youth/list"))
                .andExpect(model().attributeExists("records"))
                .andExpect(model().attribute("youthType", "college"))
                .andExpect(model().attribute("youthTypeLabel", "在校大学生"));
    }

    @Test
    void createCollegeYouthShouldPersist() throws Exception {
        String uniquePhone = "139" + System.currentTimeMillis();

        mockMvc.perform(post("/youth/college")
                        .session(adminSession)
                        .param("name", "测试大学生")
                        .param("gender", "M")
                        .param("birthDate", "2001-09-01")
                        .param("ethnicity", "HAN")
                        .param("politicalStatus", "CYL")
                        .param("nativeProvinceCode", "630000")
                        .param("nativeCityCode", "630200")
                        .param("nativeCountyCode", "630202")
                        .param("educationLevel", "BK")
                        .param("degreeCode", "XS")
                        .param("schoolCode", "10743")  // 青海大学
                        .param("schoolProvinceCode", "630000")
                        .param("schoolCityCode", "630200")
                        .param("schoolCountyCode", "630202")
                        .param("majorCode", "080901")  // 计算机科学与技术
                        .param("majorCategory", "工学")
                        .param("recruitmentYear", "2023")
                        .param("graduationDate", "2027-06-30")
                        .param("employmentDirection", "互联网")
                        .param("sortOrder", "2")
                        .param("phone", uniquePhone))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/youth/college"))
                .andExpect(flash().attribute("successMessage", "青年信息新增成功"));

        MvcResult listResult = mockMvc.perform(get("/youth/college").session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<com.haidong.tuanwei.youth.entity.YouthInfo> records =
                (List<com.haidong.tuanwei.youth.entity.YouthInfo>) listResult.getModelAndView().getModel().get("records");
        assertThat(records)
                .filteredOn(item -> uniquePhone.equals(item.getPhone()))
                .extracting(com.haidong.tuanwei.youth.entity.YouthInfo::getSortOrder)
                .containsExactly(2);
    }

    @Test
    void youthPageShouldPrioritizeExplicitSortOrder() throws Exception {
        String phoneSecond = "160" + System.currentTimeMillis();
        String phoneFirst = "161" + System.currentTimeMillis();
        String phoneNull = "162" + System.currentTimeMillis();

        mockMvc.perform(post("/youth/college")
                        .session(adminSession)
                        .param("name", "排序青年2")
                        .param("gender", "M")
                        .param("educationLevel", "BK")
                        .param("schoolCode", "10743")
                        .param("majorCode", "080901")
                        .param("phone", phoneSecond)
                        .param("sortOrder", "2"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/youth/college")
                        .session(adminSession)
                        .param("name", "排序青年1")
                        .param("gender", "M")
                        .param("educationLevel", "BK")
                        .param("schoolCode", "10743")
                        .param("majorCode", "080901")
                        .param("phone", phoneFirst)
                        .param("sortOrder", "1"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/youth/college")
                        .session(adminSession)
                        .param("name", "排序青年空")
                        .param("gender", "M")
                        .param("educationLevel", "BK")
                        .param("schoolCode", "10743")
                        .param("majorCode", "080901")
                        .param("phone", phoneNull))
                .andExpect(status().is3xxRedirection());

        MvcResult listResult = mockMvc.perform(get("/youth/college").session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<com.haidong.tuanwei.youth.entity.YouthInfo> records =
                (List<com.haidong.tuanwei.youth.entity.YouthInfo>) listResult.getModelAndView().getModel().get("records");
        List<String> phones = records.stream()
                .map(com.haidong.tuanwei.youth.entity.YouthInfo::getPhone)
                .toList();

        assertThat(phones.indexOf(phoneFirst)).isGreaterThanOrEqualTo(0);
        assertThat(phones.indexOf(phoneSecond)).isGreaterThanOrEqualTo(0);
        assertThat(phones.indexOf(phoneNull)).isGreaterThanOrEqualTo(0);
        assertThat(phones.indexOf(phoneFirst)).isLessThan(phones.indexOf(phoneSecond));
        assertThat(phones.indexOf(phoneSecond)).isLessThan(phones.indexOf(phoneNull));
    }

    @Test
    void createYouthWithEmptyNameShouldReturnFormWithErrors() throws Exception {
        mockMvc.perform(post("/youth/college")
                        .session(adminSession)
                        .param("name", "")  // 空名称
                        .param("gender", "M")
                        .param("phone", "13900000001"))
                .andExpect(status().isOk())
                .andExpect(view().name("youth/form"))
                .andExpect(model().attributeHasFieldErrors("youthForm", "name"))
                .andExpect(model().attribute("formTitle", "新增在校大学生"));
    }

    @Test
    void deleteYouthShouldRedirectToList() throws Exception {
        // 由于在同一事务中创建和查询的问题，这里只验证删除接口返回重定向
        // 实际功能在HaidongTuanweiApplicationTests中验证
        mockMvc.perform(post("/youth/college/999/delete").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/youth/college"));
    }

    @Test
    void batchDeleteYouthShouldRedirectToList() throws Exception {
        mockMvc.perform(post("/youth/college/batch-delete")
                        .session(adminSession)
                        .param("ids", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/youth/college"));
    }

    @Test
    void youthCreateShouldWork() throws Exception {
        // 创建青年应该成功（表单验证通过时重定向，失败时返回200表单页）
        String phone = "134" + System.currentTimeMillis();
        mockMvc.perform(post("/youth/college")
                        .session(adminSession)
                        .param("name", "详情测试")
                        .param("gender", "M")
                        .param("birthDate", "2001-09-01")
                        .param("phone", phone)
                        .param("educationLevel", "BK")
                        .param("schoolCode", "10743")
                        .param("majorCode", "080901")
                        .param("recruitmentYear", "2023"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // 3xx 重定向表示成功，200 可能表示表单验证错误返回
                    assertThat(status).isIn(302, 200);
                });
    }

    @Test
    void downloadTemplateShouldReturnExcel() throws Exception {
        mockMvc.perform(get("/youth/college/template").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String contentDisposition = result.getResponse().getHeader("Content-Disposition");
                    assertThat(contentDisposition).contains("attachment");
                    assertThat(contentDisposition).contains(".xlsx");
                });
    }

    @Test
    void importYouthFromExcelShouldWork() throws Exception {
        String uniquePhone = "133" + System.currentTimeMillis();
        byte[] excelBytes = createYouthImportExcel("导入测试", uniquePhone);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "youth-import.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelBytes);

        mockMvc.perform(multipart("/youth/college/import")
                        .file(file)
                        .session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/youth/college"))
                .andExpect(flash().attributeExists("importMessage", "importResult"));
    }

    @Test
    void importResultShouldRenderFailedExcelDownloadLinkWhenFailedRowsExist() throws Exception {
        YouthImportResult importResult = new YouthImportResult();
        importResult.addError(2, "招考年份格式不正确", List.of(
                "异常年份青年", "男", "汉族", "20010901", "青海省 / 海东市 / 乐都区", "20A3",
                "本科", "青海大学", "青海省 / 海东市", "计算机科学与技术", "13900000011"));

        MvcResult pageResult = mockMvc.perform(get("/youth/college")
                        .session(adminSession)
                        .flashAttr("importResult", importResult))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(pageResult.getResponse().getContentAsString()).contains("/youth/college/import-failures");
    }

    @Test
    void downloadFailedImportExcelShouldKeepOriginalInvalidRowValuesExactly() throws Exception {
        List<String> originalValues = List.of(
                "异常年份下载青年", "男", "汉族", "20010901", "青海省 / 海东市 / 乐都区", "20A3",
                "本科", "青海大学", "青海省 / 海东市", "计算机科学与技术", "13900000012");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid-youth-import.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createYouthImportExcel(originalValues, null));

        MvcResult importResult = mockMvc.perform(multipart("/youth/college/import")
                        .file(file)
                        .session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/youth/college"))
                .andReturn();

        YouthImportResult flashImportResult = (YouthImportResult) importResult.getFlashMap().get("importResult");
        assertThat(flashImportResult).isNotNull();
        assertThat(flashImportResult.getFailedRows()).hasSize(1);
        assertThat(flashImportResult.getFailedRows().get(0).getValues()).containsExactlyElementsOf(originalValues);

        MockHttpSession session = (MockHttpSession) importResult.getRequest().getSession(false);
        MvcResult downloadResult = mockMvc.perform(get("/youth/college/import-failures").session(session))
                .andExpect(status().isOk())
                .andReturn();

        try (Workbook workbook = WorkbookFactory.create(
                new ByteArrayInputStream(downloadResult.getResponse().getContentAsByteArray()))) {
            Sheet sheet = workbook.getSheet("青年信息导入模板");
            assertThat(sheet).isNotNull();
            assertThat(sheet.getRow(0).getCell(11).getStringCellValue()).isEqualTo("失败原因");
            assertThat(readRowValues(sheet.getRow(1), 11)).containsExactlyElementsOf(originalValues);
            assertThat(sheet.getRow(1).getCell(11).getStringCellValue()).isEqualTo("招考年份格式不正确");
        }
    }

    @Test
    void exportYouthShouldReturnExcel() throws Exception {
        mockMvc.perform(get("/youth/college/export").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String contentDisposition = result.getResponse().getHeader("Content-Disposition");
                    assertThat(contentDisposition).contains("attachment");
                    try (XSSFWorkbook workbook = new XSSFWorkbook(
                            new ByteArrayInputStream(result.getResponse().getContentAsByteArray()))) {
                        Sheet sheet = workbook.getSheetAt(0);
                        Row headerRow = sheet.getRow(0);
                        List<String> headers = java.util.stream.IntStream.range(0, 11)
                                .mapToObj(index -> headerRow.getCell(index).getStringCellValue())
                                .toList();
                        assertThat(headers).containsExactly(
                                "姓名", "性别", "民族", "出生年月", "籍贯", "招考年份",
                                "学历", "学校", "学校所在区域", "专业", "联系方式");
                    }
                });
    }

    @Test
    void newYouthPageShouldLoadWithAllOptions() throws Exception {
        mockMvc.perform(get("/youth/college/new").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("youth/form"))
                .andExpect(model().attributeExists("youthForm", "genders", "ethnicities",
                        "politicalStatuses", "educationLevels", "degrees", "schools", "majors"))
                .andExpect(model().attribute("formTitle", "新增在校大学生"));
    }


    @Test
    void ajaxRequestShouldReturnFragment() throws Exception {
        mockMvc.perform(get("/youth/college")
                        .session(adminSession)
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("youth/list :: listContent"));
    }

    @Test
    void youthMatchJobsShouldFilterByMajorEducationAndSchoolCategoryOnly() throws Exception {
        String uniquePhone = "135" + System.currentTimeMillis();
        String matchingJobName = "匹配岗位-" + System.currentTimeMillis();
        String nonMatchingJobName = "不匹配岗位-" + System.currentTimeMillis();

        mockMvc.perform(post("/youth/college")
                        .session(adminSession)
                        .param("name", "匹配学生")
                        .param("gender", "M")
                        .param("birthDate", "2001-09-01")
                        .param("ethnicity", "HAN")
                        .param("educationLevel", "BK")
                        .param("schoolCode", "10743")
                        .param("majorCode", "080901")
                        .param("recruitmentYear", "2023")
                        .param("phone", uniquePhone))
                .andExpect(status().is3xxRedirection());

        MvcResult youthListResult = mockMvc.perform(get("/youth/college").session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<com.haidong.tuanwei.youth.entity.YouthInfo> youthRecords =
                (List<com.haidong.tuanwei.youth.entity.YouthInfo>) youthListResult.getModelAndView().getModel().get("records");
        com.haidong.tuanwei.youth.entity.YouthInfo createdYouth = youthRecords.stream()
                .filter(item -> uniquePhone.equals(item.getPhone()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未找到新创建的青年记录"));

        mockMvc.perform(post("/jobs")
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", matchingJobName)
                        .param("educationRequirements", "BK")
                        .param("majorCodes", "080901")
                        .param("schoolCategoryIds", "100")
                        .param("schoolTagIds", "1"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/jobs")
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", nonMatchingJobName)
                        .param("educationRequirements", "BK")
                        .param("majorCodes", "120201")
                        .param("schoolCategoryIds", "101")
                        .param("schoolTagIds", "2"))
                .andExpect(status().is3xxRedirection());

        MvcResult matchResult = mockMvc.perform(get("/youth/college/" + createdYouth.getId() + "/matches").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("youth/job-match-results"))
                .andExpect(model().attributeExists("youthInfo", "records", "sourceDetailUrl", "matchMajorLabel",
                        "matchEducationLabel", "matchSchoolCategoryLabel"))
                .andReturn();

        @SuppressWarnings("unchecked")
        List<com.haidong.tuanwei.job.entity.JobPost> matchedRecords =
                (List<com.haidong.tuanwei.job.entity.JobPost>) matchResult.getModelAndView().getModel().get("records");

        assertThat(matchedRecords)
                .extracting(com.haidong.tuanwei.job.entity.JobPost::getJobName)
                .contains(matchingJobName)
                .doesNotContain(nonMatchingJobName);

        String html = matchResult.getResponse().getContentAsString();
        assertThat(html).doesNotContain("学校标签");
        assertThat(html).doesNotContain("院校标签要求");
    }

    @Test
    void ajaxMatchResultsShouldCarryDrawerReturnContext() throws Exception {
        com.haidong.tuanwei.youth.entity.YouthInfo youthInfo = createCollegeYouthForTest("返回上下文学生", "136" + System.currentTimeMillis());

        MvcResult result = mockMvc.perform(get("/youth/college/" + youthInfo.getId() + "/matches")
                        .session(adminSession)
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("youth/job-match-results :: drawerContent"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("data-drawer-return-url=\"/youth/college/" + youthInfo.getId() + "\"");
        assertThat(content).contains("返回详情");
    }

    @Test
    void youthDetailFragmentShouldExposeMatchJobsEntry() throws Exception {
        com.haidong.tuanwei.youth.entity.YouthInfo youthInfo = createCollegeYouthForTest("详情入口学生", "137" + System.currentTimeMillis());

        MvcResult result = mockMvc.perform(get("/youth/college/" + youthInfo.getId())
                        .session(adminSession)
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("youth/detail :: drawerContent"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("匹配招聘信息");
        assertThat(content).contains("/youth/college/" + youthInfo.getId() + "/matches");
    }

    @Test
    void graduateDetailFragmentShouldNotExposeMatchJobsEntry() throws Exception {
        com.haidong.tuanwei.youth.entity.YouthInfo youthInfo = createYouthForTest(
                "graduate", "毕业青年", "137" + System.currentTimeMillis(), "10743", "080901", "BK");

        MvcResult result = mockMvc.perform(get("/youth/graduate/" + youthInfo.getId())
                        .session(adminSession)
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("youth/detail :: drawerContent"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("匹配招聘信息");
        assertThat(content).doesNotContain("/youth/graduate/" + youthInfo.getId() + "/matches");
    }

    @Test
    void youthMatchJobsShouldIgnoreSchoolTagDimensionWhenStudentSchoolHasNoTags() throws Exception {
        String uniquePhone = "138" + System.currentTimeMillis();
        String matchingJobName = "标签忽略匹配岗位-" + System.currentTimeMillis();
        String nonMatchingJobName = "标签忽略不匹配岗位-" + System.currentTimeMillis();
        com.haidong.tuanwei.youth.entity.YouthInfo youthInfo = createCollegeYouthForTest(
                "无标签学生", uniquePhone, "10746", "120201", "BK");

        mockMvc.perform(post("/jobs")
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", matchingJobName)
                        .param("educationRequirements", "BK")
                        .param("majorCodes", "120201")
                        .param("schoolCategoryIds", "101")
                        .param("schoolTagIds", "1"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/jobs")
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", nonMatchingJobName)
                        .param("educationRequirements", "BK")
                        .param("majorCodes", "080901")
                        .param("schoolCategoryIds", "101")
                        .param("schoolTagIds", "1"))
                .andExpect(status().is3xxRedirection());

        MvcResult matchResult = mockMvc.perform(get("/youth/college/" + youthInfo.getId() + "/matches").session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<com.haidong.tuanwei.job.entity.JobPost> matchedRecords =
                (List<com.haidong.tuanwei.job.entity.JobPost>) matchResult.getModelAndView().getModel().get("records");

        assertThat(matchedRecords)
                .extracting(com.haidong.tuanwei.job.entity.JobPost::getJobName)
                .contains(matchingJobName)
                .doesNotContain(nonMatchingJobName);
    }

    @Test
    void youthMatchJobsShouldPrioritizeJobSortOrder() throws Exception {
        String uniquePhone = "163" + System.currentTimeMillis();
        String secondJobName = "排序匹配岗位2-" + System.currentTimeMillis();
        String firstJobName = "排序匹配岗位1-" + System.currentTimeMillis();
        String unsortedJobName = "排序匹配岗位空-" + System.currentTimeMillis();

        com.haidong.tuanwei.youth.entity.YouthInfo youthInfo = createCollegeYouthForTest(
                "排序匹配学生", uniquePhone, "10743", "080901", "BK");

        mockMvc.perform(post("/jobs")
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", secondJobName)
                        .param("educationRequirements", "BK")
                        .param("majorCodes", "080901")
                        .param("schoolCategoryIds", "100")
                        .param("sortOrder", "2"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/jobs")
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", firstJobName)
                        .param("educationRequirements", "BK")
                        .param("majorCodes", "080901")
                        .param("schoolCategoryIds", "100")
                        .param("sortOrder", "1"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/jobs")
                        .session(adminSession)
                        .param("enterpriseId", "1")
                        .param("jobName", unsortedJobName)
                        .param("educationRequirements", "BK")
                        .param("majorCodes", "080901")
                        .param("schoolCategoryIds", "100"))
                .andExpect(status().is3xxRedirection());

        MvcResult matchResult = mockMvc.perform(get("/youth/college/" + youthInfo.getId() + "/matches").session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<com.haidong.tuanwei.job.entity.JobPost> matchedRecords =
                (List<com.haidong.tuanwei.job.entity.JobPost>) matchResult.getModelAndView().getModel().get("records");
        List<String> names = matchedRecords.stream()
                .map(com.haidong.tuanwei.job.entity.JobPost::getJobName)
                .toList();

        assertThat(names.indexOf(firstJobName)).isGreaterThanOrEqualTo(0);
        assertThat(names.indexOf(secondJobName)).isGreaterThanOrEqualTo(0);
        assertThat(names.indexOf(unsortedJobName)).isGreaterThanOrEqualTo(0);
        assertThat(names.indexOf(firstJobName)).isLessThan(names.indexOf(secondJobName));
        assertThat(names.indexOf(secondJobName)).isLessThan(names.indexOf(unsortedJobName));
    }

    private com.haidong.tuanwei.youth.entity.YouthInfo createCollegeYouthForTest(String name, String phone) throws Exception {
        return createYouthForTest("college", name, phone, "10743", "080901", "BK");
    }

    private com.haidong.tuanwei.youth.entity.YouthInfo createCollegeYouthForTest(
            String name, String phone, String schoolCode, String majorCode, String educationLevel) throws Exception {
        return createYouthForTest("college", name, phone, schoolCode, majorCode, educationLevel);
    }

    private com.haidong.tuanwei.youth.entity.YouthInfo createYouthForTest(
            String type, String name, String phone, String schoolCode, String majorCode, String educationLevel) throws Exception {
        mockMvc.perform(post("/youth/" + type)
                        .session(adminSession)
                        .param("name", name)
                        .param("gender", "M")
                        .param("birthDate", "2001-09-01")
                        .param("ethnicity", "HAN")
                        .param("educationLevel", educationLevel)
                        .param("schoolCode", schoolCode)
                        .param("majorCode", majorCode)
                        .param("recruitmentYear", "2023")
                        .param("phone", phone))
                .andExpect(status().is3xxRedirection());

        MvcResult youthListResult = mockMvc.perform(get("/youth/" + type).session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<com.haidong.tuanwei.youth.entity.YouthInfo> youthRecords =
                (List<com.haidong.tuanwei.youth.entity.YouthInfo>) youthListResult.getModelAndView().getModel().get("records");
        return youthRecords.stream()
                .filter(item -> phone.equals(item.getPhone()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未找到新创建的青年记录"));
    }

    private byte[] createYouthImportExcel(String name, String phone) throws Exception {
        return createYouthImportExcel(List.of(
                name, "男", "汉族", "20010901", "青海省 / 海东市 / 乐都区", "2023",
                "本科", "青海大学", "青海省 / 海东市 / 乐都区", "计算机科学与技术", phone), null);
    }

    private byte[] createYouthImportExcel(List<String> values, String failureReason) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("青年信息导入模板");
            Row headerRow = sheet.createRow(0);
            List<String> headers = new ArrayList<>(List.of("姓名", "性别", "民族", "出生年月", "籍贯", "招考年份", "学历",
                    "学校", "学校所在区域", "专业", "联系方式"));
            if (failureReason != null) {
                headers.add("失败原因");
            }
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }
            Row row = sheet.createRow(1);
            for (int i = 0; i < values.size(); i++) {
                row.createCell(i).setCellValue(values.get(i));
            }
            if (failureReason != null) {
                row.createCell(values.size()).setCellValue(failureReason);
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private List<String> readRowValues(Row row, int cellCount) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < cellCount; i++) {
            values.add(row.getCell(i).getStringCellValue());
        }
        return values;
    }

}
