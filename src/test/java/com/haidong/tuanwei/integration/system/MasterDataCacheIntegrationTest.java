package com.haidong.tuanwei.integration.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.haidong.tuanwei.integration.IntegrationTestBase;
import com.haidong.tuanwei.system.entity.MajorCatalog;
import com.haidong.tuanwei.system.entity.School;
import com.haidong.tuanwei.system.service.MasterDataService;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.mock.web.MockMultipartFile;

class MasterDataCacheIntegrationTest extends IntegrationTestBase {

    private static final String MAJOR_CACHE_NAME = "majorSelectOptions";
    private static final String SCHOOL_CACHE_NAME = "schoolSelectOptions";

    @Autowired
    private MasterDataService masterDataService;

    @Autowired
    private CacheManager cacheManager;

    private Object getCacheKey() {
        return SimpleKey.EMPTY;
    }

    @Test
    void majorOptionsShouldBeCachedAfterFirstCall() {
        List<MajorCatalog> result = masterDataService.getAllMajors();
        assertThat(result).isNotEmpty();

        Cache cache = cacheManager.getCache(MAJOR_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();
    }

    @Test
    void majorCacheShouldBeEvictedAfterCreate() throws Exception {
        masterDataService.getAllMajors();
        Cache cache = cacheManager.getCache(MAJOR_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        String majorCode = "MCR" + System.currentTimeMillis();
        mockMvc.perform(post("/system/majors")
                        .session(adminSession)
                        .param("majorCode", majorCode)
                        .param("majorName", "缓存新增专业-" + majorCode)
                        .param("categoryDictItemId", "140"))
                .andExpect(status().is3xxRedirection());

        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void majorCacheShouldBeEvictedAfterImportFailure() throws Exception {
        masterDataService.getAllMajors();
        Cache cache = cacheManager.getCache(MAJOR_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        String majorCode = "MIMPF" + System.currentTimeMillis();
        MockMultipartFile file = buildImportFile(
                "major-import-invalid-header.xlsx",
                "错误表头1",
                "错误表头2",
                "错误表头3",
                majorCode,
                "导入失败缓存测试专业-" + majorCode,
                "工学");

        mockMvc.perform(multipart("/system/majors/import")
                        .file(file)
                        .session(adminSession))
                .andExpect(status().is3xxRedirection());

        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void majorCacheShouldBeEvictedAfterImport() throws Exception {
        masterDataService.getAllMajors();
        Cache cache = cacheManager.getCache(MAJOR_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        String majorCode = "MIMP" + System.currentTimeMillis();
        MockMultipartFile file = buildMajorImportFile(majorCode, "导入缓存测试专业-" + majorCode, "工学");

        mockMvc.perform(multipart("/system/majors/import")
                        .file(file)
                        .session(adminSession))
                .andExpect(status().is3xxRedirection());

        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void majorCacheShouldBeEvictedAfterUpdate() throws Exception {
        masterDataService.getAllMajors();
        Cache cache = cacheManager.getCache(MAJOR_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        mockMvc.perform(post("/system/majors/1")
                        .session(adminSession)
                        .param("id", "1")
                        .param("majorCode", "080901")
                        .param("majorName", "缓存更新专业-" + System.currentTimeMillis())
                        .param("categoryDictItemId", "140"))
                .andExpect(status().is3xxRedirection());

        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void majorCacheShouldBeEvictedAfterDelete() throws Exception {
        String majorCode = "MDEL" + System.currentTimeMillis();
        mockMvc.perform(post("/system/majors")
                        .session(adminSession)
                        .param("majorCode", majorCode)
                        .param("majorName", "待删专业-" + majorCode)
                        .param("categoryDictItemId", "140"))
                .andExpect(status().is3xxRedirection());

        masterDataService.getAllMajors();
        Cache cache = cacheManager.getCache(MAJOR_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        Long majorId = masterDataService.searchMajors(majorCode, 1, 20).stream()
                .filter(item -> majorCode.equals(item.getMajorCode()))
                .map(MajorCatalog::getId)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/system/majors/" + majorId + "/delete")
                        .session(adminSession))
                .andExpect(status().is3xxRedirection());

        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void schoolOptionsShouldBeCachedAfterFirstCall() {
        List<School> result = masterDataService.getAllSchoolsForSelect();
        assertThat(result).isNotEmpty();

        Cache cache = cacheManager.getCache(SCHOOL_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();
    }

    @Test
    void schoolCacheShouldBeEvictedAfterCreate() throws Exception {
        masterDataService.getAllSchoolsForSelect();
        Cache cache = cacheManager.getCache(SCHOOL_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        String schoolCode = "SC" + System.currentTimeMillis();
        mockMvc.perform(post("/system/schools")
                        .session(adminSession)
                        .param("schoolCode", schoolCode)
                        .param("schoolName", "缓存新增学校-" + schoolCode)
                        .param("categoryDictItemId", "100"))
                .andExpect(status().is3xxRedirection());

        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void schoolCacheShouldBeEvictedAfterUpdate() throws Exception {
        masterDataService.getAllSchoolsForSelect();
        Cache cache = cacheManager.getCache(SCHOOL_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        mockMvc.perform(post("/system/schools/1")
                        .session(adminSession)
                        .param("id", "1")
                        .param("schoolCode", "10743")
                        .param("schoolName", "缓存更新学校-" + System.currentTimeMillis())
                        .param("categoryDictItemId", "100"))
                .andExpect(status().is3xxRedirection());

        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void schoolCacheShouldBeEvictedAfterDelete() throws Exception {
        String schoolCode = "SDEL" + System.currentTimeMillis();
        mockMvc.perform(post("/system/schools")
                        .session(adminSession)
                        .param("schoolCode", schoolCode)
                        .param("schoolName", "待删学校-" + schoolCode)
                        .param("categoryDictItemId", "100"))
                .andExpect(status().is3xxRedirection());

        masterDataService.getAllSchoolsForSelect();
        Cache cache = cacheManager.getCache(SCHOOL_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        Long schoolId = masterDataService.searchSchools(schoolCode, 1, 20).stream()
                .filter(item -> schoolCode.equals(item.getSchoolCode()))
                .map(School::getId)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/system/schools/" + schoolId + "/delete")
                        .session(adminSession))
                .andExpect(status().is3xxRedirection());

        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void schoolCacheShouldBeEvictedAfterImport() throws Exception {
        masterDataService.getAllSchoolsForSelect();
        Cache cache = cacheManager.getCache(SCHOOL_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        String schoolCode = "SIMP" + System.currentTimeMillis();
        MockMultipartFile file = buildSchoolImportFile(schoolCode, "导入缓存测试学校-" + schoolCode, "双一流");

        mockMvc.perform(multipart("/system/schools/import")
                        .file(file)
                        .session(adminSession))
                .andExpect(status().is3xxRedirection());

        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void schoolCacheShouldBeEvictedAfterImportFailure() throws Exception {
        masterDataService.getAllSchoolsForSelect();
        Cache cache = cacheManager.getCache(SCHOOL_CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        String schoolCode = "SIMPF" + System.currentTimeMillis();
        MockMultipartFile file = buildImportFile(
                "school-import-invalid-header.xlsx",
                "错误表头1",
                "错误表头2",
                "错误表头3",
                schoolCode,
                "导入失败缓存测试学校-" + schoolCode,
                "双一流");

        mockMvc.perform(multipart("/system/schools/import")
                        .file(file)
                        .session(adminSession))
                .andExpect(status().is3xxRedirection());

        assertThat(cache.get(getCacheKey())).isNull();
    }

    private MockMultipartFile buildMajorImportFile(String majorCode, String majorName, String categoryLabel) throws Exception {
        return buildImportFile("major-import.xlsx", "专业编码", "专业名称", "学科门类", majorCode, majorName, categoryLabel);
    }

    private MockMultipartFile buildSchoolImportFile(String schoolCode, String schoolName, String categoryLabel) throws Exception {
        return buildImportFile("school-import.xlsx", "学校编码", "学校名称", "学校类别", schoolCode, schoolName, categoryLabel);
    }

    private MockMultipartFile buildImportFile(
            String fileName,
            String header1,
            String header2,
            String header3,
            String value1,
            String value2,
            String value3) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("导入模板");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue(header1);
            header.createCell(1).setCellValue(header2);
            header.createCell(2).setCellValue(header3);

            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue(value1);
            data.createCell(1).setCellValue(value2);
            data.createCell(2).setCellValue(value3);

            workbook.write(output);
            return new MockMultipartFile(
                    "file",
                    fileName,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    output.toByteArray());
        }
    }
}
