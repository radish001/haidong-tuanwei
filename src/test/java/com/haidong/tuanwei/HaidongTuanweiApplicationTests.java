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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
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
	void youthExcelTemplateGenerationWorks() throws Exception {
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
	}

	private byte[] buildYouthImportFileNew(String name, String phone, String nativePlace, String schoolRegion) throws Exception {
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("青年信息导入模板");
			Row headerRow = sheet.createRow(0);
			// 新模板表头：11列
			String[] headers = {"姓名", "性别", "民族", "出生年月", "籍贯", "招考年份", "学历",
					"学校", "学校所在区域", "专业", "联系方式"};
			for (int i = 0; i < headers.length; i++) {
				headerRow.createCell(i).setCellValue(headers[i]);
			}
			Row row = sheet.createRow(1);
			row.createCell(0).setCellValue(name);
			row.createCell(1).setCellValue("男");
			row.createCell(2).setCellValue("汉族");
			row.createCell(3).setCellValue("2001-09-01");
			row.createCell(4).setCellValue(nativePlace);
			row.createCell(5).setCellValue("2023");
			row.createCell(6).setCellValue("本科");
			row.createCell(7).setCellValue("青海大学");
			row.createCell(8).setCellValue(schoolRegion);
			row.createCell(9).setCellValue("计算机科学与技术");
			row.createCell(10).setCellValue(phone);
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
