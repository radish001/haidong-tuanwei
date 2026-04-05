package com.haidong.tuanwei;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.haidong.tuanwei.youth.dto.YouthImportResult;
import com.haidong.tuanwei.youth.dto.YouthSearchRequest;
import com.haidong.tuanwei.youth.service.YouthInfoService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HaidongTuanweiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private YouthInfoService youthInfoService;

	@Test
	void contextLoads() {
	}

	@Test
	void adminCanLoginAndAccessCorePages() throws Exception {
		MvcResult loginResult = mockMvc.perform(formLogin("/perform_login")
						.loginProcessingUrl("/login")
						.user("username", "admin")
						.password("password", "123456"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/dashboard"))
				.andReturn();

		HttpSession session = loginResult.getRequest().getSession(false);
		assertThat(session).isNotNull();

		mockMvc.perform(get("/dashboard").session((org.springframework.mock.web.MockHttpSession) session))
				.andExpect(status().isOk());

		mockMvc.perform(get("/youth/college").session((org.springframework.mock.web.MockHttpSession) session))
				.andExpect(status().isOk());
		mockMvc.perform(get("/enterprises").session((org.springframework.mock.web.MockHttpSession) session))
				.andExpect(status().isOk());
		mockMvc.perform(get("/jobs").session((org.springframework.mock.web.MockHttpSession) session))
				.andExpect(status().isOk());
		mockMvc.perform(get("/policies").session((org.springframework.mock.web.MockHttpSession) session))
				.andExpect(status().isOk());
		mockMvc.perform(get("/analytics/college").session((org.springframework.mock.web.MockHttpSession) session))
				.andExpect(status().isOk());
	}

	@Test
	void youthExcelTemplateImportAndExportWork() throws Exception {
		byte[] templateBytes = youthInfoService.generateImportTemplate();
		try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(templateBytes))) {
			assertThat(workbook.getSheet("青年信息导入模板")).isNotNull();
			Row headerRow = workbook.getSheetAt(0).getRow(0);
			assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("姓名");
			assertThat(workbook.getSheet("hidden_1")).isNotNull();
			List<String> regionOptions = sheetValues(workbook.getSheet("hidden_5"));
			assertThat(regionOptions).contains("青海省");
			assertThat(regionOptions).contains("青海省 / 海东市");
			assertThat(regionOptions).contains("青海省 / 海东市 / 乐都区");
		}

		byte[] importFile = buildYouthImportFile(
				"集成测试青年", "13912345678", "青海省 / 海东市 / 乐都区", "青海省 / 海东市 / 乐都区");
		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"youth-import.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				importFile);

		YouthImportResult firstImport = youthInfoService.importFromExcel("COLLEGE", multipartFile, 1L);
		assertThat(firstImport.getSuccessCount()).isEqualTo(1);
		assertThat(firstImport.getFailCount()).isZero();

		MockMultipartFile duplicateFile = new MockMultipartFile(
				"file",
				"youth-import-duplicate.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				importFile);
		YouthImportResult duplicateImport = youthInfoService.importFromExcel("COLLEGE", duplicateFile, 1L);
		assertThat(duplicateImport.getSuccessCount()).isZero();
		assertThat(duplicateImport.getFailCount()).isEqualTo(1);

		YouthSearchRequest query = new YouthSearchRequest();
		query.setName("集成测试青年");
		byte[] exportBytes = youthInfoService.exportExcel("COLLEGE", query);
		try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(exportBytes))) {
			Sheet sheet = workbook.getSheetAt(0);
			assertThat(sheet.getLastRowNum()).isEqualTo(1);
			assertThat(sheet.getRow(1).getCell(0).getStringCellValue()).isEqualTo("集成测试青年");
			assertThat(sheet.getRow(1).getCell(5).getStringCellValue()).isEqualTo("青海省 / 海东市 / 乐都区");
			assertThat(sheet.getRow(1).getCell(8).getStringCellValue()).isEqualTo("青海省 / 海东市 / 乐都区");
		}
	}

	@Test
	void youthExcelImportSupportsPartialRegionLevelsAndRejectsInvalidRegionPath() throws Exception {
		byte[] importFile = buildYouthImportFile(
				"部分区域青年", "13912340001", "青海省", "青海省 / 海东市");
		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"youth-import-partial.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				importFile);

		YouthImportResult partialImport = youthInfoService.importFromExcel("COLLEGE", multipartFile, 1L);
		assertThat(partialImport.getSuccessCount()).isEqualTo(1);
		assertThat(partialImport.getFailCount()).isZero();

		byte[] invalidImportFile = buildYouthImportFile(
				"非法区域青年", "13912340002", "青海省 / 不存在市", "青海省 / 海东市");
		MockMultipartFile invalidFile = new MockMultipartFile(
				"file",
				"youth-import-invalid-region.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				invalidImportFile);

		YouthImportResult invalidImport = youthInfoService.importFromExcel("COLLEGE", invalidFile, 1L);
		assertThat(invalidImport.getSuccessCount()).isZero();
		assertThat(invalidImport.getFailCount()).isEqualTo(1);
		assertThat(invalidImport.getErrors().get(0).getMessage()).contains("籍贯不在区域主数据范围内");
	}

	private byte[] buildYouthImportFile(String name, String phone, String nativePlace, String schoolRegion) throws Exception {
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("青年信息导入模板");
			Row headerRow = sheet.createRow(0);
			String[] headers = {"姓名", "性别", "出生年月", "民族", "政治面貌", "籍贯", "学历", "学校",
					"学校所在区域", "专业", "毕业时间", "就业方向", "联系方式"};
			for (int i = 0; i < headers.length; i++) {
				headerRow.createCell(i).setCellValue(headers[i]);
			}
			Row row = sheet.createRow(1);
			row.createCell(0).setCellValue(name);
			row.createCell(1).setCellValue("男");
			row.createCell(2).setCellValue("2001-09-01");
			row.createCell(3).setCellValue("汉族");
			row.createCell(4).setCellValue("共青团员");
			row.createCell(5).setCellValue(nativePlace);
			row.createCell(6).setCellValue("本科");
			row.createCell(7).setCellValue("青海大学");
			row.createCell(8).setCellValue(schoolRegion);
			row.createCell(9).setCellValue("计算机科学与技术");
			row.createCell(10).setCellValue("2026-06-30");
			row.createCell(11).setCellValue("互联网开发");
			row.createCell(12).setCellValue(phone);
			workbook.write(outputStream);
			return outputStream.toByteArray();
		}
	}

	private List<String> sheetValues(Sheet sheet) {
		List<String> values = new ArrayList<>();
		for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row != null && row.getCell(0) != null) {
				values.add(row.getCell(0).getStringCellValue());
			}
		}
		return values;
	}

}
