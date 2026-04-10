package com.haidong.tuanwei.system.service.impl;

import com.haidong.tuanwei.common.util.ExcelUtils;
import com.haidong.tuanwei.system.dao.DictionaryDao;
import com.haidong.tuanwei.job.dao.JobPostDao;
import com.haidong.tuanwei.system.dao.MajorCatalogDao;
import com.haidong.tuanwei.system.dao.SchoolDao;
import com.haidong.tuanwei.system.dao.SchoolTagDao;
import com.haidong.tuanwei.system.dto.DataImportError;
import com.haidong.tuanwei.system.dto.DataImportResult;
import com.haidong.tuanwei.system.dto.MajorForm;
import com.haidong.tuanwei.system.dto.SchoolForm;
import com.haidong.tuanwei.system.dto.SchoolTagForm;
import com.haidong.tuanwei.system.entity.DictItem;
import com.haidong.tuanwei.system.entity.MajorCatalog;
import com.haidong.tuanwei.system.entity.School;
import com.haidong.tuanwei.system.entity.SchoolTag;
import com.haidong.tuanwei.system.service.MasterDataService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private final MajorCatalogDao majorCatalogDao;
    private final SchoolTagDao schoolTagDao;
    private final SchoolDao schoolDao;
    private final DictionaryDao dictionaryDao;
    private final JobPostDao jobPostDao;

    @Override
    public List<MajorCatalog> searchMajors(String keyword, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        return majorCatalogDao.search(keyword, (safePage - 1) * safePageSize, safePageSize);
    }

    @Override
    public long countMajors(String keyword) {
        return majorCatalogDao.count(keyword);
    }

    @Override
    public List<MajorCatalog> getAllMajors() {
        return majorCatalogDao.findAll();
    }

    @Override
    public MajorCatalog getMajorById(Long id) {
        return majorCatalogDao.findById(id);
    }

    @Override
    public void createMajor(MajorForm request) {
        String majorCode = request.getMajorCode().trim();
        String majorName = request.getMajorName().trim();
        if (majorCatalogDao.findByCode(majorCode) != null) {
            throw new IllegalStateException("专业编码已存在");
        }
        if (majorCatalogDao.findByName(majorName) != null) {
            throw new IllegalStateException("专业名称已存在");
        }
        DictItem category = requireDictItem(request.getCategoryDictItemId(), "major_category");
        MajorCatalog majorCatalog = new MajorCatalog();
        majorCatalog.setMajorCode(majorCode);
        majorCatalog.setMajorName(majorName);
        majorCatalog.setCategoryDictItemId(category.getId());
        majorCatalogDao.insert(majorCatalog);
    }

    @Override
    public void updateMajor(Long id, MajorForm request) {
        MajorCatalog existing = requireMajor(id);
        String majorCode = request.getMajorCode().trim();
        String majorName = request.getMajorName().trim();
        MajorCatalog duplicateByCode = majorCatalogDao.findByCode(majorCode);
        if (duplicateByCode != null && !duplicateByCode.getId().equals(id)) {
            throw new IllegalStateException("专业编码已存在");
        }
        MajorCatalog duplicate = majorCatalogDao.findByName(majorName);
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new IllegalStateException("专业名称已存在");
        }
        DictItem category = requireDictItem(request.getCategoryDictItemId(), "major_category");
        existing.setMajorCode(majorCode);
        existing.setMajorName(majorName);
        existing.setCategoryDictItemId(category.getId());
        majorCatalogDao.update(existing);
    }

    @Override
    public void deleteMajor(Long id) {
        MajorCatalog existing = requireMajor(id);
        if (majorCatalogDao.countYouthUsageByMajorCode(existing.getMajorCode()) > 0) {
            throw new IllegalStateException("该专业名称已被青年信息使用，无法删除");
        }
        if (jobPostDao.countMajorUsage(existing.getMajorCode()) > 0) {
            throw new IllegalStateException("该专业名称已被招聘岗位使用，无法删除");
        }
        majorCatalogDao.softDelete(id);
    }

    @Override
    public List<SchoolTag> searchSchoolTags(String keyword, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        return schoolTagDao.search(keyword, (safePage - 1) * safePageSize, safePageSize);
    }

    @Override
    public long countSchoolTags(String keyword) {
        return schoolTagDao.count(keyword);
    }

    @Override
    public List<SchoolTag> getAllSchoolTags() {
        return schoolTagDao.findAll();
    }

    @Override
    public SchoolTag getSchoolTagById(Long id) {
        return schoolTagDao.findById(id);
    }

    @Override
    public void createSchoolTag(SchoolTagForm request) {
        if (schoolTagDao.findByName(request.getTagName().trim()) != null) {
            throw new IllegalStateException("学校标签已存在");
        }
        SchoolTag schoolTag = new SchoolTag();
        schoolTag.setTagName(request.getTagName().trim());
        schoolTagDao.insert(schoolTag);
    }

    @Override
    public void updateSchoolTag(Long id, SchoolTagForm request) {
        SchoolTag existing = requireSchoolTag(id);
        SchoolTag duplicate = schoolTagDao.findByName(request.getTagName().trim());
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new IllegalStateException("学校标签已存在");
        }
        existing.setTagName(request.getTagName().trim());
        schoolTagDao.update(existing);
    }

    @Override
    public void deleteSchoolTag(Long id) {
        requireSchoolTag(id);
        if (schoolTagDao.countSchoolUsage(id) > 0) {
            throw new IllegalStateException("该学校标签存在关联学校，无法删除");
        }
        if (jobPostDao.countSchoolTagUsage(id) > 0) {
            throw new IllegalStateException("该学校标签已被招聘岗位使用，无法删除");
        }
        schoolTagDao.softDelete(id);
    }

    @Override
    public List<School> searchSchools(String keyword, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        List<School> schools = schoolDao.search(keyword, (safePage - 1) * safePageSize, safePageSize);
        populateTagIds(schools);
        return schools;
    }

    @Override
    public long countSchools(String keyword) {
        return schoolDao.count(keyword);
    }

    @Override
    public List<School> getAllSchools() {
        List<School> schools = schoolDao.findAll();
        populateTagIds(schools);
        return schools;
    }

    @Override
    public List<School> getAllSchoolsForSelect() {
        return schoolDao.findAllForSelect();
    }

    @Override
    public School getSchoolById(Long id) {
        School school = schoolDao.findById(id);
        if (school != null) {
            school.setTagIds(schoolDao.findTagIdsBySchoolId(id));
        }
        return school;
    }

    @Override
    public void createSchool(SchoolForm request) {
        String schoolCode = request.getSchoolCode().trim();
        String schoolName = request.getSchoolName().trim();
        if (schoolDao.findByCode(schoolCode) != null) {
            throw new IllegalStateException("学校编码已存在");
        }
        if (schoolDao.findByName(schoolName) != null) {
            throw new IllegalStateException("学校名称已存在");
        }
        DictItem category = requireDictItem(request.getCategoryDictItemId(), "school_category");
        School school = new School();
        school.setSchoolCode(schoolCode);
        school.setSchoolName(schoolName);
        school.setCategoryDictItemId(category.getId());
        schoolDao.insert(school);
        replaceSchoolTags(school.getId(), request.getTagIds());
    }

    @Override
    public void updateSchool(Long id, SchoolForm request) {
        School existing = requireSchool(id);
        String schoolCode = request.getSchoolCode().trim();
        String schoolName = request.getSchoolName().trim();
        School duplicateByCode = schoolDao.findByCode(schoolCode);
        if (duplicateByCode != null && !duplicateByCode.getId().equals(id)) {
            throw new IllegalStateException("学校编码已存在");
        }
        School duplicate = schoolDao.findByName(schoolName);
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new IllegalStateException("学校名称已存在");
        }
        DictItem category = requireDictItem(request.getCategoryDictItemId(), "school_category");
        existing.setSchoolCode(schoolCode);
        existing.setSchoolName(schoolName);
        existing.setCategoryDictItemId(category.getId());
        schoolDao.update(existing);
        replaceSchoolTags(id, request.getTagIds());
    }

    @Override
    public void deleteSchool(Long id) {
        School existing = requireSchool(id);
        if (schoolDao.countYouthUsageBySchoolCode(existing.getSchoolCode()) > 0) {
            throw new IllegalStateException("该学校存在关联青年信息，无法删除");
        }
        schoolDao.softDelete(id);
        schoolDao.deleteTagRelations(id);
    }

    @Override
    public byte[] generateMajorImportTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet mainSheet = workbook.createSheet("专业信息导入模板");
            Row headerRow = mainSheet.createRow(0);
            String[] headers = {"专业编码", "专业名称", "学科门类"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            List<String> categoryOptions = majorCategoryLabels();
            createImportHiddenSheet(workbook, "hidden_cat", "majorCategoryOptions", categoryOptions);
            applyImportDropDown(mainSheet, "majorCategoryOptions", 2);
            for (int i = 0; i < headers.length; i++) {
                mainSheet.autoSizeColumn(i);
                mainSheet.setColumnWidth(i, Math.max(mainSheet.getColumnWidth(i), 16 * 256));
            }
            return ExcelUtils.toBytes(workbook);
        } catch (IOException e) {
            throw new IllegalStateException("专业导入模板生成失败", e);
        }
    }

    @Override
    @Transactional
    public DataImportResult importMajorsFromExcel(MultipartFile file) {
        DataImportResult result = new DataImportResult();
        List<String[]> rows = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            String[] expectedHeaders = {"专业编码", "专业名称", "学科门类"};
            if (!validateImportHeader(sheet, expectedHeaders)) {
                result.addError(1, "导入模板列顺序或表头不正确，请使用最新模板");
                return result;
            }
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                String code = ExcelUtils.getCellText(row.getCell(0)).trim();
                String name = ExcelUtils.getCellText(row.getCell(1)).trim();
                String categoryLabel = ExcelUtils.getCellText(row.getCell(2)).trim();
                if (code.isEmpty() && name.isEmpty() && categoryLabel.isEmpty()) continue;
                rows.add(new String[]{code, name, categoryLabel, String.valueOf(rowIndex + 1)});
            }
        } catch (IOException e) {
            throw new IllegalStateException("导入文件读取失败", e);
        }

        Map<String, String> categoryLabelToId = majorCategoryLabelToIdMap();
        Map<String, Integer> seenCodes = new LinkedHashMap<>();
        List<String[]> validRows = new ArrayList<>();

        for (String[] row : rows) {
            int displayRow = Integer.parseInt(row[3]);
            String code = row[0], name = row[1], categoryLabel = row[2];
            if (code.isEmpty()) { result.addError(displayRow, "专业编码不能为空"); continue; }
            if (name.isEmpty()) { result.addError(displayRow, "专业名称不能为空"); continue; }
            if (categoryLabel.isEmpty()) { result.addError(displayRow, "学科门类不能为空"); continue; }
            if (!categoryLabelToId.containsKey(categoryLabel)) {
                result.addError(displayRow, "学科门类 \"" + categoryLabel + "\" 不存在"); continue;
            }
            if (seenCodes.containsKey(code)) {
                result.addError(displayRow, "专业编码 \"" + code + "\" 在第 " + seenCodes.get(code) + " 行已重复");
                continue;
            }
            seenCodes.put(code, displayRow);
            validRows.add(row);
        }

        if (!result.getErrors().isEmpty()) {
            return result;
        }

        List<String> allCodes = new ArrayList<>(seenCodes.keySet());
        List<MajorCatalog> existing = majorCatalogDao.findExistingCodes(allCodes);
        if (!existing.isEmpty()) {
            Map<String, String> rowByCode = new HashMap<>();
            for (String[] row : validRows) {
                rowByCode.put(row[0], row[3]);
            }
            for (MajorCatalog m : existing) {
                int displayRow = Integer.parseInt(rowByCode.get(m.getMajorCode()));
                result.addError(displayRow, "专业编码 \"" + m.getMajorCode() + "\" 已存在（" + m.getMajorName() + "）");
            }
            return result;
        }

        for (String[] row : validRows) {
            MajorCatalog major = new MajorCatalog();
            major.setMajorCode(row[0]);
            major.setMajorName(row[1]);
            major.setCategoryDictItemId(Long.parseLong(categoryLabelToId.get(row[2])));
            majorCatalogDao.insert(major);
            result.addSuccess();
        }
        return result;
    }

    @Override
    public byte[] generateSchoolImportTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet mainSheet = workbook.createSheet("学校信息导入模板");
            Row headerRow = mainSheet.createRow(0);
            String[] headers = {"学校编码", "学校名称", "学校类别"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            List<String> categoryOptions = schoolCategoryLabels();
            createImportHiddenSheet(workbook, "hidden_cat", "schoolCategoryOptions", categoryOptions);
            applyImportDropDown(mainSheet, "schoolCategoryOptions", 2);
            for (int i = 0; i < headers.length; i++) {
                mainSheet.autoSizeColumn(i);
                mainSheet.setColumnWidth(i, Math.max(mainSheet.getColumnWidth(i), 16 * 256));
            }
            return ExcelUtils.toBytes(workbook);
        } catch (IOException e) {
            throw new IllegalStateException("学校导入模板生成失败", e);
        }
    }

    @Override
    @Transactional
    public DataImportResult importSchoolsFromExcel(MultipartFile file) {
        DataImportResult result = new DataImportResult();
        List<String[]> rows = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            String[] expectedHeaders = {"学校编码", "学校名称", "学校类别"};
            if (!validateImportHeader(sheet, expectedHeaders)) {
                result.addError(1, "导入模板列顺序或表头不正确，请使用最新模板");
                return result;
            }
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                String code = ExcelUtils.getCellText(row.getCell(0)).trim();
                String name = ExcelUtils.getCellText(row.getCell(1)).trim();
                String categoryLabel = ExcelUtils.getCellText(row.getCell(2)).trim();
                if (code.isEmpty() && name.isEmpty() && categoryLabel.isEmpty()) continue;
                rows.add(new String[]{code, name, categoryLabel, String.valueOf(rowIndex + 1)});
            }
        } catch (IOException e) {
            throw new IllegalStateException("导入文件读取失败", e);
        }

        Map<String, String> categoryLabelToId = schoolCategoryLabelToIdMap();
        Map<String, Integer> seenCodes = new LinkedHashMap<>();
        List<String[]> validRows = new ArrayList<>();

        for (String[] row : rows) {
            int displayRow = Integer.parseInt(row[3]);
            String code = row[0], name = row[1], categoryLabel = row[2];
            if (code.isEmpty()) { result.addError(displayRow, "学校编码不能为空"); continue; }
            if (name.isEmpty()) { result.addError(displayRow, "学校名称不能为空"); continue; }
            if (categoryLabel.isEmpty()) { result.addError(displayRow, "学校类别不能为空"); continue; }
            if (!categoryLabelToId.containsKey(categoryLabel)) {
                result.addError(displayRow, "学校类别 \"" + categoryLabel + "\" 不存在"); continue;
            }
            if (seenCodes.containsKey(code)) {
                result.addError(displayRow, "学校编码 \"" + code + "\" 在第 " + seenCodes.get(code) + " 行已重复");
                continue;
            }
            seenCodes.put(code, displayRow);
            validRows.add(row);
        }

        if (!result.getErrors().isEmpty()) {
            return result;
        }

        List<String> allCodes = new ArrayList<>(seenCodes.keySet());
        List<School> existing = schoolDao.findExistingCodes(allCodes);
        if (!existing.isEmpty()) {
            Map<String, String> rowByCode = new HashMap<>();
            for (String[] row : validRows) {
                rowByCode.put(row[0], row[3]);
            }
            for (School s : existing) {
                int displayRow = Integer.parseInt(rowByCode.get(s.getSchoolCode()));
                result.addError(displayRow, "学校编码 \"" + s.getSchoolCode() + "\" 已存在（" + s.getSchoolName() + "）");
            }
            return result;
        }

        for (String[] row : validRows) {
            School school = new School();
            school.setSchoolCode(row[0]);
            school.setSchoolName(row[1]);
            school.setCategoryDictItemId(Long.parseLong(categoryLabelToId.get(row[2])));
            schoolDao.insert(school);
            result.addSuccess();
        }
        return result;
    }

    private List<String> majorCategoryLabels() {
        List<DictItem> items = dictionaryDao.findByType("major_category");
        List<String> labels = new ArrayList<>();
        for (DictItem item : items) {
            labels.add(item.getDictLabel());
        }
        return labels;
    }

    private Map<String, String> majorCategoryLabelToIdMap() {
        List<DictItem> items = dictionaryDao.findByType("major_category");
        Map<String, String> map = new LinkedHashMap<>();
        for (DictItem item : items) {
            map.put(item.getDictLabel(), String.valueOf(item.getId()));
        }
        return map;
    }

    private List<String> schoolCategoryLabels() {
        List<DictItem> items = dictionaryDao.findByType("school_category");
        List<String> labels = new ArrayList<>();
        for (DictItem item : items) {
            labels.add(item.getDictLabel());
        }
        return labels;
    }

    private Map<String, String> schoolCategoryLabelToIdMap() {
        List<DictItem> items = dictionaryDao.findByType("school_category");
        Map<String, String> map = new LinkedHashMap<>();
        for (DictItem item : items) {
            map.put(item.getDictLabel(), String.valueOf(item.getId()));
        }
        return map;
    }

    private boolean validateImportHeader(Sheet sheet, String[] expectedHeaders) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return false;
        for (int i = 0; i < expectedHeaders.length; i++) {
            String actual = ExcelUtils.getCellText(headerRow.getCell(i)).trim();
            if (!expectedHeaders[i].equals(actual)) return false;
        }
        return true;
    }

    private void createImportHiddenSheet(Workbook workbook, String sheetName, String rangeName, List<String> values) {
        Sheet hiddenSheet = workbook.createSheet(sheetName);
        for (int i = 0; i < values.size(); i++) {
            hiddenSheet.createRow(i).createCell(0).setCellValue(values.get(i));
        }
        Name namedRange = workbook.createName();
        namedRange.setNameName(rangeName);
        namedRange.setRefersToFormula(sheetName + "!$A$1:$A$" + Math.max(values.size(), 1));
        workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);
    }

    private void applyImportDropDown(Sheet sheet, String rangeName, int columnIndex) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createFormulaListConstraint(rangeName);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 500, columnIndex, columnIndex);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setSuppressDropDownArrow(true);
        sheet.addValidationData(validation);
    }

    private void replaceSchoolTags(Long schoolId, List<Long> tagIds) {
        schoolDao.deleteTagRelations(schoolId);
        if (tagIds == null) {
            return;
        }
        for (Long tagId : tagIds) {
            if (tagId == null) {
                continue;
            }
            requireSchoolTag(tagId);
            schoolDao.insertTagRelation(schoolId, tagId);
        }
    }

    private void populateTagIds(List<School> schools) {
        for (School school : schools) {
            school.setTagIds(schoolDao.findTagIdsBySchoolId(school.getId()));
        }
    }

    private DictItem requireDictItem(Long id, String dictType) {
        DictItem item = dictionaryDao.findById(id);
        if (item == null || !dictType.equals(item.getDictType())) {
            throw new IllegalStateException("关联字典不存在或类型不匹配");
        }
        return item;
    }

    private MajorCatalog requireMajor(Long id) {
        MajorCatalog majorCatalog = majorCatalogDao.findById(id);
        if (majorCatalog == null) {
            throw new IllegalStateException("专业名称不存在或已删除");
        }
        return majorCatalog;
    }

    private SchoolTag requireSchoolTag(Long id) {
        SchoolTag schoolTag = schoolTagDao.findById(id);
        if (schoolTag == null) {
            throw new IllegalStateException("学校标签不存在或已删除");
        }
        return schoolTag;
    }

    private School requireSchool(Long id) {
        School school = schoolDao.findById(id);
        if (school == null) {
            throw new IllegalStateException("学校不存在或已删除");
        }
        return school;
    }
}
