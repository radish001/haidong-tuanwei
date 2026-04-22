package com.haidong.tuanwei.youth.service.impl;

import com.haidong.tuanwei.system.dao.DictionaryDao;
import com.haidong.tuanwei.system.dao.MajorCatalogDao;
import com.haidong.tuanwei.system.dao.RegionDao;
import com.haidong.tuanwei.system.dao.SchoolDao;
import com.haidong.tuanwei.youth.dao.YouthInfoDao;
import com.haidong.tuanwei.youth.dto.YouthFormRequest;
import com.haidong.tuanwei.youth.dto.YouthImportFailedRow;
import com.haidong.tuanwei.youth.dto.YouthImportResult;
import com.haidong.tuanwei.youth.dto.YouthSearchRequest;
import com.haidong.tuanwei.system.entity.DictItem;
import com.haidong.tuanwei.system.entity.MajorCatalog;
import com.haidong.tuanwei.system.entity.Region;
import com.haidong.tuanwei.system.entity.School;
import com.haidong.tuanwei.system.support.RegionSelectionSupport;
import com.haidong.tuanwei.youth.entity.YouthInfo;
import com.haidong.tuanwei.youth.service.YouthInfoService;
import com.haidong.tuanwei.common.util.ExcelUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class YouthInfoServiceImpl implements YouthInfoService {

    private static final String REGION_PATH_DELIMITER = " / ";
    private static final String FAILURE_REASON_HEADER = "失败原因";
    private static final String[] IMPORT_TEMPLATE_HEADERS = {
            "姓名", "性别", "民族", "出生年月", "籍贯", "招考年份", "学历",
            "学校", "学校所在区域", "专业", "联系方式"
    };
    private static final String[] EXPORT_HEADERS = {
            "姓名", "性别", "民族", "出生年月", "籍贯", "招考年份", "学历",
            "学校", "学校所在区域", "专业", "联系方式"
    };

    private final YouthInfoDao youthInfoDao;
    private final DictionaryDao dictionaryDao;
    private final RegionDao regionDao;
    private final MajorCatalogDao majorCatalogDao;
    private final SchoolDao schoolDao;
    private final RegionSelectionSupport regionSelectionSupport;

    @Override
    public List<YouthInfo> search(String youthType, YouthSearchRequest query) {
        return youthInfoDao.search(youthType, query);
    }

    @Override
    public long count(String youthType, YouthSearchRequest query) {
        return youthInfoDao.count(youthType, query);
    }

    @Override
    public YouthInfo getById(Long id) {
        return youthInfoDao.findById(id);
    }

    @Override
    public void create(String youthType, YouthFormRequest request, Long operatorId) {
        YouthInfo youthInfo = toEntity(request);
        youthInfo.setYouthType(youthType);
        youthInfo.setCreateBy(operatorId);
        youthInfo.setUpdateBy(operatorId);
        youthInfoDao.insert(youthInfo);
        log.info("Youth record created: type={}, id={}, operatorId={}", youthType, youthInfo.getId(), operatorId);
    }

    @Override
    public void update(Long id, YouthFormRequest request, Long operatorId) {
        YouthInfo youthInfo = toEntity(request);
        youthInfo.setId(id);
        youthInfo.setUpdateBy(operatorId);
        youthInfoDao.update(youthInfo);
        log.info("Youth record updated: id={}, operatorId={}", id, operatorId);
    }

    @Override
    public void delete(Long id, Long operatorId) {
        youthInfoDao.softDelete(id, operatorId);
        log.info("Youth record deleted: id={}, operatorId={}", id, operatorId);
    }

    @Override
    public int deleteBatch(String youthType, List<Long> ids, Long operatorId) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        int deletedCount = youthInfoDao.softDeleteBatch(youthType, ids, operatorId);
        log.info("Youth records batch deleted: type={}, requestedCount={}, deletedCount={}, operatorId={}",
                youthType, ids.size(), deletedCount, operatorId);
        return deletedCount;
    }

    @Override
    public byte[] generateImportTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet mainSheet = workbook.createSheet("青年信息导入模板");
            createHeader(mainSheet, IMPORT_TEMPLATE_HEADERS);

            List<String> genderOptions = dictionaryLabels("gender");
            List<String> ethnicityOptions = dictionaryLabels("ethnicity");
            List<String> regionOptions = regionPaths();
            List<String> educationOptions = dictionaryLabels("education_level");
            List<String> schoolOptions = schoolNames();
            List<String> majorOptions = majorNames();

            createHiddenSheet(workbook, "hidden_1", "genderOptions", genderOptions);
            createHiddenSheet(workbook, "hidden_3", "ethnicityOptions", ethnicityOptions);
            createHiddenSheet(workbook, "hidden_5", "nativePlaceOptions", regionOptions);
            createHiddenSheet(workbook, "hidden_6", "educationOptions", educationOptions);
            createHiddenSheet(workbook, "hidden_8", "schoolOptions", schoolOptions);
            createHiddenSheet(workbook, "hidden_9", "schoolRegionOptions", regionOptions);
            createHiddenSheet(workbook, "hidden_10", "majorOptions", majorOptions);

            applyDropDown(mainSheet, "genderOptions", 1);
            applyDropDown(mainSheet, "ethnicityOptions", 2);
            applyDropDown(mainSheet, "nativePlaceOptions", 4);
            applyDropDown(mainSheet, "educationOptions", 6);
            applyDropDown(mainSheet, "schoolOptions", 7);
            applyDropDown(mainSheet, "schoolRegionOptions", 8);
            applyDropDown(mainSheet, "majorOptions", 9);

            for (int i = 0; i < IMPORT_TEMPLATE_HEADERS.length; i++) {
                mainSheet.autoSizeColumn(i);
                mainSheet.setColumnWidth(i, Math.max(mainSheet.getColumnWidth(i), 16 * 256));
            }
            return ExcelUtils.toBytes(workbook);
        } catch (IOException e) {
            log.error("Failed to generate youth import template", e);
            throw new IllegalStateException("导入模板生成失败", e);
        }
    }

    @Override
    public YouthImportResult importFromExcel(String youthType, MultipartFile file, Long operatorId) {
        YouthImportResult result = new YouthImportResult();
        Map<String, RegionSelectionSupport.RegionSelection> regionPathMap = regionPathMap();
        Set<String> fileDuplicates = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            try {
                validateTemplateHeader(sheet);
            } catch (IllegalArgumentException ex) {
                result.addError(1, ex.getMessage());
                log.warn("Youth import rejected because template header was invalid: type={}, operatorId={}, fileName={}",
                        youthType, operatorId, file.getOriginalFilename());
                return result;
            }
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }
                int displayRow = rowIndex + 1;
                List<String> rawValues = extractImportRowValues(row);
                try {
                    YouthInfo youthInfo = mapRowToYouth(row, regionPathMap);
                    youthInfo.setYouthType(youthType);
                    youthInfo.setCreateBy(operatorId);
                    youthInfo.setUpdateBy(operatorId);

                    String duplicateKey = youthType + "|" + youthInfo.getName() + "|" + youthInfo.getPhone();
                    if (!fileDuplicates.add(duplicateKey)) {
                        result.addError(displayRow, "文件中存在重复数据", rawValues);
                        continue;
                    }
                    if (youthInfoDao.countDuplicate(youthType, youthInfo.getName(), youthInfo.getPhone()) > 0) {
                        result.addError(displayRow, "系统中已存在同名同联系方式数据", rawValues);
                        continue;
                    }

                    youthInfoDao.insert(youthInfo);
                    result.addSuccess();
                } catch (IllegalArgumentException ex) {
                    result.addError(displayRow, ex.getMessage(), rawValues);
                }
            }
            log.info("Youth import finished: type={}, operatorId={}, fileName={}, successCount={}, failCount={}",
                    youthType, operatorId, file.getOriginalFilename(), result.getSuccessCount(), result.getFailCount());
            return result;
        } catch (IOException e) {
            log.error("Failed to read youth import file: type={}, operatorId={}, fileName={}",
                    youthType, operatorId, file.getOriginalFilename(), e);
            throw new IllegalStateException("导入文件读取失败", e);
        }
    }

    @Override
    public byte[] generateFailedImportExcel(YouthImportResult result) {
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(generateImportTemplate()))) {
            Sheet mainSheet = workbook.getSheet("青年信息导入模板");
            CellStyle failureReasonStyle = createFailureReasonStyle(workbook);
            Cell failureReasonHeaderCell = mainSheet.getRow(0).createCell(IMPORT_TEMPLATE_HEADERS.length);
            failureReasonHeaderCell.setCellValue(FAILURE_REASON_HEADER);
            failureReasonHeaderCell.setCellStyle(failureReasonStyle);

            int rowIndex = 1;
            for (YouthImportFailedRow failedRow : result.getFailedRows()) {
                Row row = mainSheet.createRow(rowIndex++);
                List<String> values = failedRow.getValues();
                for (int i = 0; i < IMPORT_TEMPLATE_HEADERS.length; i++) {
                    row.createCell(i).setCellValue(i < values.size() ? values.get(i) : "");
                }
                Cell failureReasonCell = row.createCell(IMPORT_TEMPLATE_HEADERS.length);
                failureReasonCell.setCellValue(failedRow.getMessage());
                failureReasonCell.setCellStyle(failureReasonStyle);
            }

            for (int i = 0; i < IMPORT_TEMPLATE_HEADERS.length; i++) {
                mainSheet.autoSizeColumn(i);
                mainSheet.setColumnWidth(i, Math.max(mainSheet.getColumnWidth(i), 16 * 256));
            }
            mainSheet.autoSizeColumn(IMPORT_TEMPLATE_HEADERS.length);
            mainSheet.setColumnWidth(IMPORT_TEMPLATE_HEADERS.length,
                    Math.max(mainSheet.getColumnWidth(IMPORT_TEMPLATE_HEADERS.length), 24 * 256));
            return ExcelUtils.toBytes(workbook);
        } catch (IOException e) {
            log.error("Failed to generate youth failed import file", e);
            throw new IllegalStateException("失败数据文件生成失败", e);
        }
    }

    @Override
    public byte[] exportExcel(String youthType, YouthSearchRequest query) {
        query.setPaged(false);
        List<YouthInfo> records = search(youthType, query);
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(200)) {
            Sheet sheet = workbook.createSheet("青年信息导出");
            createHeader(sheet, EXPORT_HEADERS);

            int rowIndex = 1;
            for (YouthInfo item : records) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(safe(item.getName()));
                row.createCell(1).setCellValue(safe(item.getGender()));
                row.createCell(2).setCellValue(safe(item.getEthnicity()));
                row.createCell(3).setCellValue(ExcelUtils.formatDate(item.getBirthDate()));
                row.createCell(4).setCellValue(exportRegionPath(
                        item.getNativePlaceName(),
                        item.getNativeProvinceCode(),
                        item.getNativeCityCode(),
                        item.getNativeCountyCode()));
                row.createCell(5).setCellValue(item.getRecruitmentYear() == null ? "" : String.valueOf(item.getRecruitmentYear()));
                row.createCell(6).setCellValue(safe(item.getEducationLevelName()));
                row.createCell(7).setCellValue(safe(item.getSchoolName()));
                row.createCell(8).setCellValue(exportRegionPath(
                        item.getSchoolRegionName(),
                        item.getSchoolProvinceCode(),
                        item.getSchoolCityCode(),
                        item.getSchoolCountyCode()));
                row.createCell(9).setCellValue(safe(item.getMajor()));
                row.createCell(10).setCellValue(safe(item.getPhone()));
            }
            for (int i = 0; i < EXPORT_HEADERS.length; i++) {
                sheet.setColumnWidth(i, 18 * 256);
            }
            log.info("Youth export finished: type={}, recordCount={}", youthType, records.size());
            return ExcelUtils.toBytes(workbook);
        } catch (IOException e) {
            log.error("Failed to generate youth export file: type={}", youthType, e);
            throw new IllegalStateException("导出文件生成失败", e);
        }
    }

    private YouthInfo toEntity(YouthFormRequest request) {
        YouthInfo youthInfo = new YouthInfo();
        youthInfo.setName(request.getName());
        youthInfo.setGender(validateDictValue("gender", request.getGender(), "性别"));
        youthInfo.setBirthDate(parseDate(request.getBirthDate()));
        youthInfo.setEthnicity(validateDictValue("ethnicity", request.getEthnicity(), "民族"));
        youthInfo.setPoliticalStatus(validateDictValue("political_status", request.getPoliticalStatus(), "政治面貌"));
        RegionSelectionSupport.RegionSelection nativeSelection = regionSelectionSupport.normalize(
                request.getNativeProvinceCode(), request.getNativeCityCode(), request.getNativeCountyCode(), "籍贯");
        youthInfo.setNativeProvinceCode(nativeSelection.getProvinceCode());
        youthInfo.setNativeCityCode(nativeSelection.getCityCode());
        youthInfo.setNativeCountyCode(nativeSelection.getCountyCode());
        youthInfo.setEducationCode(validateDictValue("education_level", request.getEducationLevel(), "学历"));
        youthInfo.setDegreeCode(validateDictValue("degree", request.getDegreeCode(), "学位"));
        School school = requireSchoolByCode(request.getSchoolCode());
        youthInfo.setSchoolCode(school.getSchoolCode());
        youthInfo.setSchoolName(school.getSchoolName());
        RegionSelectionSupport.RegionSelection schoolSelection = regionSelectionSupport.normalize(
                request.getSchoolProvinceCode(), request.getSchoolCityCode(), request.getSchoolCountyCode(), "学校所在地");
        youthInfo.setSchoolProvinceCode(schoolSelection.getProvinceCode());
        youthInfo.setSchoolCityCode(schoolSelection.getCityCode());
        youthInfo.setSchoolCountyCode(schoolSelection.getCountyCode());
        MajorCatalog majorCatalog = requireMajorByCode(request.getMajorCode());
        youthInfo.setMajorCode(majorCatalog.getMajorCode());
        youthInfo.setMajor(majorCatalog.getMajorName());
        youthInfo.setMajorCategory(majorCatalog.getCategoryDictValue());
        try {
            youthInfo.setRecruitmentYear(parseRecruitmentYear(request.getRecruitmentYear(), "招考年份格式不正确"));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
        youthInfo.setGraduationDate(parseDate(request.getGraduationDate()));
        youthInfo.setEmploymentDirection(request.getEmploymentDirection());
        youthInfo.setPhone(request.getPhone());
        RegionSelectionSupport.RegionSelection residenceSelection = regionSelectionSupport.normalize(
                request.getResidenceProvinceCode(), request.getResidenceCityCode(), request.getResidenceCountyCode(), "现居住地");
        youthInfo.setResidenceProvinceCode(residenceSelection.getProvinceCode());
        youthInfo.setResidenceCityCode(residenceSelection.getCityCode());
        youthInfo.setResidenceCountyCode(residenceSelection.getCountyCode());
        youthInfo.setEmploymentStatus(request.getEmploymentStatus());
        youthInfo.setCurrentJob(request.getCurrentJob());
        youthInfo.setEmploymentCompany(request.getEmploymentCompany());
        youthInfo.setEntrepreneurshipStatus(request.getEntrepreneurshipStatus());
        youthInfo.setEntrepreneurshipProject(request.getEntrepreneurshipProject());
        youthInfo.setEntrepreneurshipDemand(request.getEntrepreneurshipDemand());
        youthInfo.setSortOrder(request.getSortOrder());
        youthInfo.setRemarks(request.getRemarks());
        return youthInfo;
    }

    private LocalDate parseDate(String dateText) {
        if (dateText == null || dateText.isBlank()) {
            return null;
        }
        return LocalDate.parse(dateText);
    }

    private Integer parseRecruitmentYear(String yearText, String errorMessage) {
        if (yearText == null || yearText.isBlank()) {
            return null;
        }
        String normalized = yearText.trim();
        if (!normalized.matches("\\d{4}")) {
            throw new IllegalArgumentException(errorMessage);
        }
        int year = Integer.parseInt(normalized);
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException(errorMessage);
        }
        return year;
    }

    private void createHeader(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    private void createHiddenSheet(Workbook workbook, String sheetName, String rangeName, List<String> values) {
        Sheet hiddenSheet = workbook.createSheet(sheetName);
        for (int i = 0; i < values.size(); i++) {
            hiddenSheet.createRow(i).createCell(0).setCellValue(values.get(i));
        }
        Name namedRange = workbook.createName();
        namedRange.setNameName(rangeName);
        namedRange.setRefersToFormula(sheetName + "!$A$1:$A$" + Math.max(values.size(), 1));
        workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);
    }

    private void applyDropDown(Sheet sheet, String rangeName, int columnIndex) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createFormulaListConstraint(rangeName);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 500, columnIndex, columnIndex);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setSuppressDropDownArrow(true);
        sheet.addValidationData(validation);
    }

    private List<String> dictionaryValues(String dictType) {
        List<DictItem> items = dictionaryDao.findByType(dictType);
        List<String> values = new ArrayList<>();
        for (DictItem item : items) {
            values.add(item.getDictValue());
        }
        return values;
    }

    private List<String> dictionaryLabels(String dictType) {
        List<DictItem> items = dictionaryDao.findByType(dictType);
        List<String> labels = new ArrayList<>();
        for (DictItem item : items) {
            labels.add(item.getDictLabel());
        }
        return labels;
    }

    private Map<String, String> dictionaryLabelToValueMap(String dictType) {
        List<DictItem> items = dictionaryDao.findByType(dictType);
        Map<String, String> map = new LinkedHashMap<>();
        for (DictItem item : items) {
            map.put(item.getDictLabel(), item.getDictValue());
        }
        return map;
    }

    private List<String> regionPaths() {
        List<Region> regions = regionDao.findAll();
        Map<Long, Region> regionMap = new HashMap<>();
        Map<Long, String> pathCache = new HashMap<>();
        Set<String> paths = new LinkedHashSet<>();
        for (Region region : regions) {
            regionMap.put(region.getId(), region);
        }
        for (Region region : regions) {
            paths.add(buildRegionPath(region, regionMap, pathCache));
        }
        return new ArrayList<>(paths);
    }

    private List<String> schoolNames() {
        List<School> schools = schoolDao.findAll();
        List<String> names = new ArrayList<>();
        for (School school : schools) {
            names.add(school.getSchoolName());
        }
        return names;
    }

    private List<String> majorNames() {
        List<MajorCatalog> majors = majorCatalogDao.findAll();
        List<String> names = new ArrayList<>();
        for (MajorCatalog major : majors) {
            names.add(major.getMajorName());
        }
        return names;
    }

    private Map<String, RegionSelectionSupport.RegionSelection> regionPathMap() {
        List<Region> regions = regionDao.findAll();
        Map<Long, Region> regionMap = new HashMap<>();
        Map<Long, String> pathCache = new HashMap<>();
        Map<String, RegionSelectionSupport.RegionSelection> map = new LinkedHashMap<>();
        for (Region region : regions) {
            regionMap.put(region.getId(), region);
        }
        for (Region region : regions) {
            map.put(normalizeRegionPath(buildRegionPath(region, regionMap, pathCache)),
                    buildRegionSelection(region, regionMap));
        }
        return map;
    }

    private YouthInfo mapRowToYouth(Row row, Map<String, RegionSelectionSupport.RegionSelection> regionPathMap) {
        String name = requiredText(row.getCell(0), "姓名不能为空");
        String gender = requiredText(row.getCell(1), "性别不能为空");
        String ethnicity = requiredText(row.getCell(2), "民族不能为空");
        String nativePlaceName = requiredText(row.getCell(4), "籍贯不能为空");
        String educationLevel = requiredText(row.getCell(6), "学历不能为空");
        String schoolName = requiredText(row.getCell(7), "学校不能为空");
        String schoolLocationName = requiredText(row.getCell(8), "学校所在地不能为空");
        String major = requiredText(row.getCell(9), "专业不能为空");
        String phone = requiredText(row.getCell(10), "联系方式不能为空");

        String genderCode = validateImportDictLabel("gender", gender, "性别");
        String ethnicityCode = validateImportDictLabel("ethnicity", ethnicity, "民族");
        String educationCode = validateImportDictLabel("education_level", educationLevel, "学历");

        RegionSelectionSupport.RegionSelection nativeSelection = requireRegionSelection(nativePlaceName, regionPathMap, "籍贯");
        RegionSelectionSupport.RegionSelection schoolSelection = requireRegionSelection(
                schoolLocationName, regionPathMap, "学校所在地");
        School school = requireSchoolByName(schoolName);
        MajorCatalog majorCatalog = requireMajorByName(major);

        YouthInfo youthInfo = new YouthInfo();
        youthInfo.setName(name);
        youthInfo.setGender(genderCode);
        youthInfo.setBirthDate(parseOptionalDate(ExcelUtils.getCellText(row.getCell(3)), "出生年月格式不正确"));
        youthInfo.setEthnicity(ethnicityCode);
        youthInfo.setNativeProvinceCode(nativeSelection.getProvinceCode());
        youthInfo.setNativeCityCode(nativeSelection.getCityCode());
        youthInfo.setNativeCountyCode(nativeSelection.getCountyCode());
        youthInfo.setEducationCode(educationCode);
        youthInfo.setSchoolCode(school.getSchoolCode());
        youthInfo.setSchoolName(school.getSchoolName());
        youthInfo.setSchoolProvinceCode(schoolSelection.getProvinceCode());
        youthInfo.setSchoolCityCode(schoolSelection.getCityCode());
        youthInfo.setSchoolCountyCode(schoolSelection.getCountyCode());
        youthInfo.setMajorCode(majorCatalog.getMajorCode());
        youthInfo.setMajor(majorCatalog.getMajorName());
        youthInfo.setMajorCategory(majorCatalog.getCategoryDictValue());
        youthInfo.setRecruitmentYear(parseRecruitmentYear(ExcelUtils.getCellText(row.getCell(5)), "招考年份格式不正确"));
        youthInfo.setPhone(phone);
        youthInfo.setEmploymentStatus("待确认");
        youthInfo.setRemarks("通过 Excel 模板导入");
        return youthInfo;
    }

    private RegionSelectionSupport.RegionSelection requireRegionSelection(String rawPath,
            Map<String, RegionSelectionSupport.RegionSelection> regionPathMap,
            String fieldLabel) {
        String normalizedPath = normalizeRegionPath(rawPath);
        RegionSelectionSupport.RegionSelection selection = regionPathMap.get(normalizedPath);
        if (selection == null) {
            throw new IllegalArgumentException(fieldLabel + "不在区域主数据范围内");
        }
        return selection;
    }

    private School requireSchoolByCode(String schoolCode) {
        if (schoolCode == null || schoolCode.isBlank()) {
            throw new IllegalStateException("学校不能为空");
        }
        School school = schoolDao.findByCode(schoolCode.trim());
        if (school == null) {
            throw new IllegalStateException("学校不在基础数据范围内");
        }
        return school;
    }

    private School requireSchoolByName(String schoolName) {
        if (schoolName == null || schoolName.isBlank()) {
            throw new IllegalArgumentException("学校不能为空");
        }
        School school = schoolDao.findByName(schoolName.trim());
        if (school == null) {
            throw new IllegalArgumentException("学校不在基础数据范围内");
        }
        return school;
    }

    private String validateDictValue(String dictType, String value, String label) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (!dictionaryValues(dictType).contains(value)) {
            throw new IllegalStateException(label + "不在基础数据范围内");
        }
        return value;
    }

    private String validateImportDictLabel(String dictType, String labelText, String fieldLabel) {
        String dictValue = dictionaryLabelToValueMap(dictType).get(labelText);
        if (dictValue == null) {
            throw new IllegalArgumentException(fieldLabel + "不在字典范围内");
        }
        return dictValue;
    }

    private MajorCatalog requireMajorByCode(String majorCode) {
        if (majorCode == null || majorCode.isBlank()) {
            throw new IllegalStateException("专业不能为空");
        }
        MajorCatalog majorCatalog = majorCatalogDao.findByCode(majorCode.trim());
        if (majorCatalog == null) {
            throw new IllegalStateException("专业不在基础数据范围内");
        }
        return majorCatalog;
    }

    private MajorCatalog requireMajorByName(String majorName) {
        if (majorName == null || majorName.isBlank()) {
            throw new IllegalArgumentException("专业不能为空");
        }
        MajorCatalog majorCatalog = majorCatalogDao.findByName(majorName.trim());
        if (majorCatalog == null) {
            throw new IllegalArgumentException("专业不在基础数据范围内");
        }
        return majorCatalog;
    }

    private LocalDate parseOptionalDate(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return ExcelUtils.parseDate(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private String requiredText(Cell cell, String errorMessage) {
        String value = ExcelUtils.getCellText(cell);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value.trim();
    }

    private List<String> extractImportRowValues(Row row) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < IMPORT_TEMPLATE_HEADERS.length; i++) {
            values.add(rawCellText(row.getCell(i)));
        }
        return values;
    }

    private CellStyle createFailureReasonStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private String rawCellText(Cell cell) {
        if (cell == null) {
            return "";
        }
        CellType cellType = cell.getCellType();
        if (cellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate().toString();
            }
            double value = cell.getNumericCellValue();
            long longValue = (long) value;
            return value == longValue ? String.valueOf(longValue) : String.valueOf(value);
        }
        if (cellType == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        if (cellType == CellType.FORMULA) {
            try {
                return cell.getStringCellValue();
            } catch (Exception ignored) {
                return String.valueOf(cell.getNumericCellValue());
            }
        }
        if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        }
        return cell.toString();
    }

    private void validateTemplateHeader(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("导入模板表头缺失，请使用最新模板");
        }
        for (int i = 0; i < IMPORT_TEMPLATE_HEADERS.length; i++) {
            String actualHeader = ExcelUtils.getCellText(headerRow.getCell(i)).trim();
            if (!IMPORT_TEMPLATE_HEADERS[i].equals(actualHeader)) {
                throw new IllegalArgumentException("导入模板列顺序或表头不正确，请使用最新模板");
            }
        }
        short lastCellNum = headerRow.getLastCellNum();
        if (lastCellNum > IMPORT_TEMPLATE_HEADERS.length) {
            for (int i = IMPORT_TEMPLATE_HEADERS.length; i < lastCellNum; i++) {
                String headerText = ExcelUtils.getCellText(headerRow.getCell(i)).trim();
                if (headerText.isBlank()) {
                    continue;
                }
                if (i == IMPORT_TEMPLATE_HEADERS.length && FAILURE_REASON_HEADER.equals(headerText)) {
                    continue;
                }
                if (!headerText.isBlank()) {
                    throw new IllegalArgumentException("导入模板列顺序或表头不正确，请使用最新模板");
                }
            }
        }
    }

    private String buildRegionPath(Region region, Map<Long, Region> regionMap, Map<Long, String> pathCache) {
        if (region == null) {
            return "";
        }
        String cached = pathCache.get(region.getId());
        if (cached != null) {
            return cached;
        }
        String path;
        if (region.getParentId() == null || region.getRegionLevel() == null || region.getRegionLevel() <= 1) {
            path = region.getRegionName();
        } else {
            Region parent = regionMap.get(region.getParentId());
            if (parent == null) {
                path = region.getRegionName();
            } else {
                path = buildRegionPath(parent, regionMap, pathCache) + REGION_PATH_DELIMITER + region.getRegionName();
            }
        }
        pathCache.put(region.getId(), path);
        return path;
    }

    private RegionSelectionSupport.RegionSelection buildRegionSelection(Region region, Map<Long, Region> regionMap) {
        if (region == null || region.getRegionLevel() == null) {
            throw new IllegalArgumentException("区域主数据不完整");
        }
        if (region.getRegionLevel() == 1) {
            return new RegionSelectionSupport.RegionSelection(region.getRegionCode(), null, null);
        }
        Region cityOrProvince = regionMap.get(region.getParentId());
        if (cityOrProvince == null) {
            throw new IllegalArgumentException("区域主数据缺少上级");
        }
        if (region.getRegionLevel() == 2) {
            return new RegionSelectionSupport.RegionSelection(cityOrProvince.getRegionCode(), region.getRegionCode(), null);
        }
        Region province = regionMap.get(cityOrProvince.getParentId());
        if (province == null) {
            throw new IllegalArgumentException("区域主数据缺少上级");
        }
        return new RegionSelectionSupport.RegionSelection(province.getRegionCode(), cityOrProvince.getRegionCode(),
                region.getRegionCode());
    }

    private String normalizeRegionPath(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String[] parts = value.trim().split("\\s*[-/／]\\s*");
        List<String> normalizedParts = new ArrayList<>();
        for (String part : parts) {
            if (!part.isBlank()) {
                normalizedParts.add(part.trim());
            }
        }
        return String.join(REGION_PATH_DELIMITER, normalizedParts);
    }

    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < IMPORT_TEMPLATE_HEADERS.length; i++) {
            if (!ExcelUtils.getCellText(row.getCell(i)).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String exportRegionPath(String displayName, String provinceCode, String cityCode, String countyCode) {
        if (displayName != null && !displayName.isBlank()) {
            return displayName;
        }
        return regionSelectionSupport.buildExcelPath(provinceCode, cityCode, countyCode).replace(REGION_PATH_DELIMITER, "-");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
