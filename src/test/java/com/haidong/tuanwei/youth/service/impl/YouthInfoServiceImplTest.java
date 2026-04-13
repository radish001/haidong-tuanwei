package com.haidong.tuanwei.youth.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.haidong.tuanwei.system.dao.DictionaryDao;
import com.haidong.tuanwei.system.dao.MajorCatalogDao;
import com.haidong.tuanwei.system.dao.RegionDao;
import com.haidong.tuanwei.system.dao.SchoolDao;
import com.haidong.tuanwei.system.entity.DictItem;
import com.haidong.tuanwei.system.entity.MajorCatalog;
import com.haidong.tuanwei.system.entity.Region;
import com.haidong.tuanwei.system.entity.School;
import com.haidong.tuanwei.system.support.RegionSelectionSupport;
import com.haidong.tuanwei.youth.dao.YouthInfoDao;
import com.haidong.tuanwei.youth.dto.YouthImportResult;
import com.haidong.tuanwei.youth.dto.YouthSearchRequest;
import com.haidong.tuanwei.youth.entity.YouthInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

class YouthInfoServiceImplTest {

    private YouthInfoDao youthInfoDao;
    private DictionaryDao dictionaryDao;
    private RegionDao regionDao;
    private MajorCatalogDao majorCatalogDao;
    private SchoolDao schoolDao;
    private YouthInfoServiceImpl youthInfoService;

    @BeforeEach
    void setUp() {
        youthInfoDao = mock(YouthInfoDao.class);
        dictionaryDao = mock(DictionaryDao.class);
        regionDao = mock(RegionDao.class);
        majorCatalogDao = mock(MajorCatalogDao.class);
        schoolDao = mock(SchoolDao.class);

        stubDictionary("gender", "男", "女");
        stubDictionary("ethnicity", "汉族", "藏族");
        stubDictionary("political_status", "共青团员", "中共党员");
        stubDictionary("education_level", "本科", "硕士");
        stubDictionary("degree", "学士", "硕士");

        School school = new School();
        school.setId(1L);
        school.setSchoolCode("10743");
        school.setSchoolName("青海大学");
        when(schoolDao.findAll()).thenReturn(List.of(school));
        when(schoolDao.findByName("青海大学")).thenReturn(school);
        when(schoolDao.findByCode("10743")).thenReturn(school);

        MajorCatalog major = new MajorCatalog();
        major.setId(1L);
        major.setMajorCode("080901");
        major.setMajorName("计算机科学与技术");
        major.setCategoryLabel("工学");
        when(majorCatalogDao.findAll()).thenReturn(List.of(major));
        when(majorCatalogDao.findByName("计算机科学与技术")).thenReturn(major);
        when(majorCatalogDao.findByCode("080901")).thenReturn(major);

        List<Region> regions = sampleRegions();
        when(regionDao.findAll()).thenReturn(regions);
        for (Region region : regions) {
            when(regionDao.findById(region.getId())).thenReturn(region);
            when(regionDao.findByCode(region.getRegionCode())).thenReturn(region);
        }

        RegionSelectionSupport regionSelectionSupport = new RegionSelectionSupport(regionDao);
        youthInfoService = new YouthInfoServiceImpl(
                youthInfoDao, dictionaryDao, regionDao, majorCatalogDao, schoolDao, regionSelectionSupport);
    }

    @Test
    void generateImportTemplateUsesFullRegionPathOptions() throws Exception {
        byte[] templateBytes = youthInfoService.generateImportTemplate();

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(templateBytes))) {
            Sheet mainSheet = workbook.getSheet("青年信息导入模板");
            assertThat(mainSheet).isNotNull();
            assertThat(mainSheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("姓名");
            assertThat(mainSheet.getRow(0).getCell(2).getStringCellValue()).isEqualTo("民族");
            assertThat(mainSheet.getRow(0).getCell(5).getStringCellValue()).isEqualTo("招考年份");
            assertThat(mainSheet.getRow(0).getCell(10).getStringCellValue()).isEqualTo("联系方式");
            assertThat(mainSheet.getRow(0).getLastCellNum()).isEqualTo((short) 11);

            List<String> nativePlaceOptions = sheetValues(workbook.getSheet("hidden_5"));
            assertThat(nativePlaceOptions).containsExactly(
                    "青海省",
                    "青海省 / 海东市",
                    "青海省 / 海东市 / 乐都区");

            List<String> schoolRegionOptions = sheetValues(workbook.getSheet("hidden_9"));
            assertThat(schoolRegionOptions).containsExactlyElementsOf(nativePlaceOptions);
        }
    }

    @Test
    void importFromExcelParsesFullAndPartialRegionPaths() throws Exception {
        when(youthInfoDao.countDuplicate(any(), any(), any())).thenReturn(0);

        MockMultipartFile fullPathFile = new MockMultipartFile(
                "file",
                "full-path.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                buildYouthImportFile("完整路径青年", "13900000001", "青海省 / 海东市 / 乐都区", "青海省 / 海东市 / 乐都区"));
        YouthImportResult fullPathResult = youthInfoService.importFromExcel("COLLEGE", fullPathFile, 1L);
        assertThat(fullPathResult.getSuccessCount()).isEqualTo(1);
        assertThat(fullPathResult.getFailCount()).isZero();

        MockMultipartFile partialPathFile = new MockMultipartFile(
                "file",
                "partial-path.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                buildYouthImportFile("部分路径青年", "13900000002", "青海省", "青海省 / 海东市"));
        YouthImportResult partialPathResult = youthInfoService.importFromExcel("COLLEGE", partialPathFile, 1L);
        assertThat(partialPathResult.getSuccessCount()).isEqualTo(1);
        assertThat(partialPathResult.getFailCount()).isZero();

        ArgumentCaptor<YouthInfo> captor = ArgumentCaptor.forClass(YouthInfo.class);
        verify(youthInfoDao, times(2)).insert(captor.capture());
        List<YouthInfo> importedRows = captor.getAllValues();

        YouthInfo fullPathYouth = importedRows.get(0);
        assertThat(fullPathYouth.getNativeProvinceCode()).isEqualTo("630000");
        assertThat(fullPathYouth.getNativeCityCode()).isEqualTo("630200");
        assertThat(fullPathYouth.getNativeCountyCode()).isEqualTo("630202");
        assertThat(fullPathYouth.getSchoolProvinceCode()).isEqualTo("630000");
        assertThat(fullPathYouth.getSchoolCityCode()).isEqualTo("630200");
        assertThat(fullPathYouth.getSchoolCountyCode()).isEqualTo("630202");
        assertThat(fullPathYouth.getSchoolCode()).isEqualTo("10743");
        assertThat(fullPathYouth.getMajorCode()).isEqualTo("080901");
        assertThat(fullPathYouth.getRecruitmentYear()).isEqualTo(2023);
        assertThat(fullPathYouth.getPoliticalStatus()).isNull();
        assertThat(fullPathYouth.getDegreeCode()).isNull();
        assertThat(fullPathYouth.getGraduationDate()).isNull();
        assertThat(fullPathYouth.getEmploymentDirection()).isNull();

        YouthInfo partialPathYouth = importedRows.get(1);
        assertThat(partialPathYouth.getNativeProvinceCode()).isEqualTo("630000");
        assertThat(partialPathYouth.getNativeCityCode()).isNull();
        assertThat(partialPathYouth.getNativeCountyCode()).isNull();
        assertThat(partialPathYouth.getSchoolProvinceCode()).isEqualTo("630000");
        assertThat(partialPathYouth.getSchoolCityCode()).isEqualTo("630200");
        assertThat(partialPathYouth.getSchoolCountyCode()).isNull();
        assertThat(partialPathYouth.getRecruitmentYear()).isEqualTo(2023);
    }

    @Test
    void importFromExcelRejectsInvalidRegionPathInvalidYearAndInvalidHeader() throws Exception {
        MockMultipartFile invalidRegionFile = new MockMultipartFile(
                "file",
                "invalid-region.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                buildYouthImportFile("非法区域青年", "13900000003", "青海省 / 不存在市", "青海省 / 海东市"));
        YouthImportResult invalidRegionResult = youthInfoService.importFromExcel("COLLEGE", invalidRegionFile, 1L);
        assertThat(invalidRegionResult.getSuccessCount()).isZero();
        assertThat(invalidRegionResult.getFailCount()).isEqualTo(1);
        assertThat(invalidRegionResult.getErrors().get(0).getMessage()).contains("籍贯不在区域主数据范围内");

        MockMultipartFile invalidYearFile = new MockMultipartFile(
                "file",
                "invalid-year.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                buildYouthImportFile("非法年份青年", "13900000005", "青海省 / 海东市 / 乐都区", "青海省 / 海东市", "20A3"));
        YouthImportResult invalidYearResult = youthInfoService.importFromExcel("COLLEGE", invalidYearFile, 1L);
        assertThat(invalidYearResult.getSuccessCount()).isZero();
        assertThat(invalidYearResult.getFailCount()).isEqualTo(1);
        assertThat(invalidYearResult.getErrors().get(0).getMessage()).contains("招考年份格式不正确");

        MockMultipartFile invalidHeaderFile = new MockMultipartFile(
                "file",
                "invalid-header.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                buildYouthImportFileWithHeaderChange("联系电话"));
        YouthImportResult invalidHeaderResult = youthInfoService.importFromExcel("COLLEGE", invalidHeaderFile, 1L);
        assertThat(invalidHeaderResult.getSuccessCount()).isZero();
        assertThat(invalidHeaderResult.getFailCount()).isEqualTo(1);
        assertThat(invalidHeaderResult.getErrors().get(0).getMessage()).contains("导入模板列顺序或表头不正确");
    }

    @Test
    void exportExcelOutputsFullRegionPathsInTemplateOrder() throws Exception {
        YouthInfo youthInfo = new YouthInfo();
        youthInfo.setName("导出青年");
        youthInfo.setGender("男");
        youthInfo.setBirthDate(LocalDate.of(2001, 9, 1));
        youthInfo.setEthnicity("汉族");
        youthInfo.setPoliticalStatus("共青团员");
        youthInfo.setNativeProvinceCode("630000");
        youthInfo.setNativeCityCode("630200");
        youthInfo.setNativeCountyCode("630202");
        youthInfo.setEducationLevelName("本科");
        youthInfo.setDegreeName("学士");
        youthInfo.setSchoolName("青海大学");
        youthInfo.setSchoolProvinceCode("630000");
        youthInfo.setSchoolCityCode("630200");
        youthInfo.setSchoolCountyCode("630202");
        youthInfo.setMajor("计算机科学与技术");
        youthInfo.setMajorCategory("工学");
        youthInfo.setRecruitmentYear(2023);
        youthInfo.setPoliticalStatus("共青团员");
        youthInfo.setDegreeName("学士");
        youthInfo.setGraduationDate(LocalDate.of(2026, 6, 30));
        youthInfo.setEmploymentDirection("互联网开发");
        youthInfo.setPhone("13900000004");
        when(youthInfoDao.search(eq("COLLEGE"), any(YouthSearchRequest.class))).thenReturn(List.of(youthInfo));

        byte[] exportBytes = youthInfoService.exportExcel("COLLEGE", new YouthSearchRequest());

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(exportBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("姓名");
            assertThat(sheet.getRow(0).getCell(4).getStringCellValue()).isEqualTo("籍贯");
            assertThat(sheet.getRow(0).getCell(5).getStringCellValue()).isEqualTo("招考年份");
            assertThat(sheet.getRow(0).getCell(7).getStringCellValue()).isEqualTo("学历");
            assertThat(sheet.getRow(0).getCell(9).getStringCellValue()).isEqualTo("专业类别");
            assertThat(sheet.getRow(0).getCell(10).getStringCellValue()).isEqualTo("学校所在区域");
            assertThat(sheet.getRow(0).getCell(11).getStringCellValue()).isEqualTo("联系方式");
            assertThat(sheet.getRow(1).getCell(4).getStringCellValue()).isEqualTo("青海省-海东市-乐都区");
            assertThat(sheet.getRow(1).getCell(5).getStringCellValue()).isEqualTo("2023");
            assertThat(sheet.getRow(1).getCell(6).getStringCellValue()).isEqualTo("青海大学");
            assertThat(sheet.getRow(1).getCell(7).getStringCellValue()).isEqualTo("本科");
            assertThat(sheet.getRow(1).getCell(8).getStringCellValue()).isEqualTo("计算机科学与技术");
            assertThat(sheet.getRow(1).getCell(9).getStringCellValue()).isEqualTo("工学");
            assertThat(sheet.getRow(1).getCell(10).getStringCellValue()).isEqualTo("青海省-海东市-乐都区");
            assertThat(sheet.getRow(1).getCell(11).getStringCellValue()).isEqualTo("13900000004");
        }
    }

    private void stubDictionary(String dictType, String... values) {
        List<DictItem> items = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            DictItem item = new DictItem();
            item.setId((long) (i + 1));
            item.setDictType(dictType);
            item.setDictLabel(values[i]);
            item.setDictValue(values[i]);
            item.setSortNo(i + 1);
            items.add(item);
        }
        when(dictionaryDao.findByType(dictType)).thenReturn(items);
    }

    private List<Region> sampleRegions() {
        Region province = new Region();
        province.setId(1L);
        province.setRegionCode("630000");
        province.setRegionName("青海省");
        province.setRegionLevel(1);
        province.setSortNo(1);

        Region city = new Region();
        city.setId(2L);
        city.setParentId(1L);
        city.setRegionCode("630200");
        city.setRegionName("海东市");
        city.setRegionLevel(2);
        city.setSortNo(1);

        Region county = new Region();
        county.setId(3L);
        county.setParentId(2L);
        county.setRegionCode("630202");
        county.setRegionName("乐都区");
        county.setRegionLevel(3);
        county.setSortNo(1);

        return List.of(province, city, county);
    }

    private byte[] buildYouthImportFile(String name, String phone, String nativePlace, String schoolRegion) throws Exception {
        return buildYouthImportFile(name, phone, nativePlace, schoolRegion, "2023");
    }

    private byte[] buildYouthImportFile(String name, String phone, String nativePlace, String schoolRegion, String recruitmentYear) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("青年信息导入模板");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"姓名", "性别", "民族", "出生年月", "籍贯", "招考年份", "学历",
                    "学校", "学校所在区域", "专业", "联系方式"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(name);
            row.createCell(1).setCellValue("男");
            row.createCell(2).setCellValue("汉族");
            row.createCell(3).setCellValue("20010901");
            row.createCell(4).setCellValue(nativePlace);
            row.createCell(5).setCellValue(recruitmentYear);
            row.createCell(6).setCellValue("本科");
            row.createCell(7).setCellValue("青海大学");
            row.createCell(8).setCellValue(schoolRegion);
            row.createCell(9).setCellValue("计算机科学与技术");
            row.createCell(10).setCellValue(phone);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] buildYouthImportFileWithHeaderChange(String lastHeader) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("青年信息导入模板");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"姓名", "性别", "民族", "出生年月", "籍贯", "招考年份", "学历",
                    "学校", "学校所在区域", "专业", lastHeader};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
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
