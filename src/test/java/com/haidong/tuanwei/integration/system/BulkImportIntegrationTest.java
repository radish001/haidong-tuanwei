package com.haidong.tuanwei.integration.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.haidong.tuanwei.integration.IntegrationTestBase;
import com.haidong.tuanwei.system.dto.DataImportResult;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

/**
 * 专业和学校批量导入功能集成测试
 */
class BulkImportIntegrationTest extends IntegrationTestBase {

    // ─────────────────── 专业导入测试 ───────────────────

    @Test
    void downloadMajorTemplateShouldReturnExcel() throws Exception {
        mockMvc.perform(get("/system/majors/template").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String contentDisposition = result.getResponse().getHeader("Content-Disposition");
                    assertThat(contentDisposition).contains("attachment");
                    assertThat(contentDisposition).contains(".xlsx");
                    assertThat(result.getResponse().getContentLength()).isGreaterThan(0);
                });
    }

    @Test
    void importMajorsSuccessShouldPersist() throws Exception {
        String uniqueCode = "XTST" + System.currentTimeMillis();
        byte[] excel = buildMajorExcel(
                new String[]{uniqueCode, "测试专业名称" + uniqueCode, "工学"});

        MockMultipartFile file = mockXlsxFile("major-import.xlsx", excel);

        mockMvc.perform(multipart("/system/majors/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/system/dictionaries?tab=major"))
                .andExpect(flash().attribute("importMessage", "导入成功，共导入 1 条专业"));
    }

    @Test
    void importMajorsDuplicateCodeInFileShouldFail() throws Exception {
        String code = "XDUP" + System.currentTimeMillis();
        byte[] excel = buildMajorExcel(
                new String[]{code, "专业A", "工学"},
                new String[]{code, "专业B", "工学"});  // 同一 code 出现两次

        MockMultipartFile file = mockXlsxFile("major-import.xlsx", excel);

        mockMvc.perform(multipart("/system/majors/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    DataImportResult importResult = (DataImportResult)
                            result.getFlashMap().get("importResult");
                    assertThat(importResult).isNotNull();
                    assertThat(importResult.getFailCount()).isGreaterThan(0);
                    assertThat(importResult.getSuccessCount()).isEqualTo(0);
                    assertThat(importResult.getErrors())
                            .anyMatch(e -> e.getMessage().contains("已重复"));
                });
    }

    @Test
    void importMajorsDuplicateCodeInDbShouldFail() throws Exception {
        // 080901 是已存在于测试数据库的专业编码（计算机科学与技术）
        byte[] excel = buildMajorExcel(
                new String[]{"080901", "计算机科学与技术", "工学"});

        MockMultipartFile file = mockXlsxFile("major-import.xlsx", excel);

        mockMvc.perform(multipart("/system/majors/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    DataImportResult importResult = (DataImportResult)
                            result.getFlashMap().get("importResult");
                    assertThat(importResult).isNotNull();
                    assertThat(importResult.getFailCount()).isGreaterThan(0);
                    assertThat(importResult.getSuccessCount()).isEqualTo(0);
                    assertThat(importResult.getErrors())
                            .anyMatch(e -> e.getMessage().contains("已存在"));
                });
    }

    @Test
    void importMajorsMissingCodeShouldFail() throws Exception {
        byte[] excel = buildMajorExcel(
                new String[]{"", "测试专业缺编码", "工学"});  // 编码为空

        MockMultipartFile file = mockXlsxFile("major-import.xlsx", excel);

        mockMvc.perform(multipart("/system/majors/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    DataImportResult importResult = (DataImportResult)
                            result.getFlashMap().get("importResult");
                    assertThat(importResult).isNotNull();
                    assertThat(importResult.getFailCount()).isGreaterThan(0);
                    assertThat(importResult.getErrors())
                            .anyMatch(e -> e.getMessage().contains("专业编码不能为空"));
                });
    }

    @Test
    void importMajorsInvalidCategoryShouldFail() throws Exception {
        String code = "XCAT" + System.currentTimeMillis();
        byte[] excel = buildMajorExcel(
                new String[]{code, "测试专业", "不存在的学科门类"});

        MockMultipartFile file = mockXlsxFile("major-import.xlsx", excel);

        mockMvc.perform(multipart("/system/majors/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    DataImportResult importResult = (DataImportResult)
                            result.getFlashMap().get("importResult");
                    assertThat(importResult).isNotNull();
                    assertThat(importResult.getErrors())
                            .anyMatch(e -> e.getMessage().contains("不存在的学科门类") && e.getMessage().contains("不存在"));
                });
    }

    @Test
    void importMajorsWrongHeaderShouldFail() throws Exception {
        byte[] excel = buildExcelWithCustomHeaders(
                new String[]{"错误表头1", "错误表头2", "错误表头3"},
                new String[]{"123456", "测试", "工学"});

        MockMultipartFile file = mockXlsxFile("major-import.xlsx", excel);

        mockMvc.perform(multipart("/system/majors/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    DataImportResult importResult = (DataImportResult)
                            result.getFlashMap().get("importResult");
                    assertThat(importResult).isNotNull();
                    assertThat(importResult.getErrors())
                            .anyMatch(e -> e.getMessage().contains("表头不正确"));
                });
    }

    @Test
    void importMajorsEmptyFileShouldShowError() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc.perform(multipart("/system/majors/import").file(emptyFile).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/system/dictionaries?tab=major"))
                .andExpect(flash().attribute("importMessage", "请先选择需要上传的 Excel 文件"));
    }

    // ─────────────────── 学校导入测试 ───────────────────

    @Test
    void downloadSchoolTemplateShouldReturnExcel() throws Exception {
        mockMvc.perform(get("/system/schools/template").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String contentDisposition = result.getResponse().getHeader("Content-Disposition");
                    assertThat(contentDisposition).contains("attachment");
                    assertThat(contentDisposition).contains(".xlsx");
                    assertThat(result.getResponse().getContentLength()).isGreaterThan(0);
                });
    }

    @Test
    void importSchoolsSuccessShouldPersist() throws Exception {
        String uniqueCode = "XTST" + System.currentTimeMillis();
        byte[] excel = buildSchoolExcel(
                new String[]{uniqueCode, "测试学校" + uniqueCode, "普通本科"});

        MockMultipartFile file = mockXlsxFile("school-import.xlsx", excel);

        mockMvc.perform(multipart("/system/schools/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/system/dictionaries?tab=school"))
                .andExpect(flash().attribute("importMessage", "导入成功，共导入 1 所学校"));
    }

    @Test
    void importSchoolsDuplicateCodeInFileShouldFail() throws Exception {
        String code = "XDUPS" + System.currentTimeMillis();
        byte[] excel = buildSchoolExcel(
                new String[]{code, "学校A", "普通本科"},
                new String[]{code, "学校B", "普通本科"});

        MockMultipartFile file = mockXlsxFile("school-import.xlsx", excel);

        mockMvc.perform(multipart("/system/schools/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    DataImportResult importResult = (DataImportResult)
                            result.getFlashMap().get("importResult");
                    assertThat(importResult).isNotNull();
                    assertThat(importResult.getFailCount()).isGreaterThan(0);
                    assertThat(importResult.getSuccessCount()).isEqualTo(0);
                    assertThat(importResult.getErrors())
                            .anyMatch(e -> e.getMessage().contains("已重复"));
                });
    }

    @Test
    void importSchoolsDuplicateCodeInDbShouldFail() throws Exception {
        // 10743 是已存在于测试数据库的学校编码（青海大学）
        byte[] excel = buildSchoolExcel(
                new String[]{"10743", "青海大学", "双一流"});

        MockMultipartFile file = mockXlsxFile("school-import.xlsx", excel);

        mockMvc.perform(multipart("/system/schools/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    DataImportResult importResult = (DataImportResult)
                            result.getFlashMap().get("importResult");
                    assertThat(importResult).isNotNull();
                    assertThat(importResult.getFailCount()).isGreaterThan(0);
                    assertThat(importResult.getSuccessCount()).isEqualTo(0);
                    assertThat(importResult.getErrors())
                            .anyMatch(e -> e.getMessage().contains("已存在"));
                });
    }

    @Test
    void importSchoolsMissingNameShouldFail() throws Exception {
        String code = "XMISS" + System.currentTimeMillis();
        byte[] excel = buildSchoolExcel(
                new String[]{code, "", "普通本科"});  // 学校名为空

        MockMultipartFile file = mockXlsxFile("school-import.xlsx", excel);

        mockMvc.perform(multipart("/system/schools/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    DataImportResult importResult = (DataImportResult)
                            result.getFlashMap().get("importResult");
                    assertThat(importResult).isNotNull();
                    assertThat(importResult.getErrors())
                            .anyMatch(e -> e.getMessage().contains("学校名称不能为空"));
                });
    }

    @Test
    void importSchoolsInvalidCategoryShouldFail() throws Exception {
        String code = "XICAT" + System.currentTimeMillis();
        byte[] excel = buildSchoolExcel(
                new String[]{code, "测试学校", "不存在的类别"});

        MockMultipartFile file = mockXlsxFile("school-import.xlsx", excel);

        mockMvc.perform(multipart("/system/schools/import").file(file).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    DataImportResult importResult = (DataImportResult)
                            result.getFlashMap().get("importResult");
                    assertThat(importResult).isNotNull();
                    assertThat(importResult.getErrors())
                            .anyMatch(e -> e.getMessage().contains("不存在的类别") && e.getMessage().contains("不存在"));
                });
    }

    @Test
    void importSchoolsEmptyFileShouldShowError() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc.perform(multipart("/system/schools/import").file(emptyFile).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/system/dictionaries?tab=school"))
                .andExpect(flash().attribute("importMessage", "请先选择需要上传的 Excel 文件"));
    }

    // ─────────────────── 辅助方法 ───────────────────

    private byte[] buildMajorExcel(String[]... rows) throws Exception {
        return buildExcelWithCustomHeaders(new String[]{"专业编码", "专业名称", "学科门类"}, rows);
    }

    private byte[] buildSchoolExcel(String[]... rows) throws Exception {
        return buildExcelWithCustomHeaders(new String[]{"学校编码", "学校名称", "学校类别"}, rows);
    }

    private byte[] buildExcelWithCustomHeaders(String[] headers, String[]... dataRows) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            for (int r = 0; r < dataRows.length; r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < dataRows[r].length; c++) {
                    row.createCell(c).setCellValue(dataRows[r][c]);
                }
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private MockMultipartFile mockXlsxFile(String filename, byte[] content) {
        return new MockMultipartFile(
                "file", filename,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                content);
    }
}
