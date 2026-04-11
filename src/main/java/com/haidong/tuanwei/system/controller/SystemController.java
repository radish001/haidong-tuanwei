package com.haidong.tuanwei.system.controller;

import com.haidong.tuanwei.common.web.AjaxRequestSupport;
import com.haidong.tuanwei.common.web.PaginationSupport;
import com.haidong.tuanwei.system.dto.DataImportResult;
import com.haidong.tuanwei.system.dto.DictionaryItemForm;
import com.haidong.tuanwei.system.dto.DictionaryWorkbenchQuery;
import com.haidong.tuanwei.system.dto.MajorForm;
import com.haidong.tuanwei.system.dto.RegionForm;
import com.haidong.tuanwei.system.dto.SchoolForm;
import com.haidong.tuanwei.system.dto.SchoolTagForm;
import com.haidong.tuanwei.system.entity.DictItem;
import com.haidong.tuanwei.system.entity.MajorCatalog;
import com.haidong.tuanwei.system.entity.Region;
import com.haidong.tuanwei.system.entity.School;
import com.haidong.tuanwei.system.entity.SchoolTag;
import com.haidong.tuanwei.system.service.DictionaryService;
import com.haidong.tuanwei.system.service.MasterDataService;
import com.haidong.tuanwei.system.service.RegionService;
import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SystemController {

    private static final String TAB_COMMON = "common";
    private static final String TAB_MAJOR_CATEGORY = "major-category";
    private static final String TAB_MAJOR = "major";
    private static final String TAB_SCHOOL_CATEGORY = "school-category";
    private static final String TAB_SCHOOL = "school";
    private static final String TAB_ENTERPRISE = "enterprise";
    private static final String TAB_ANALYTICS = "analytics";

    private static final String SECTION_REGION = "region";
    private static final String SECTION_SCHOOL_TAG = "school-tag";
    private static final String SECTION_ANALYTICS_HAIDONG_TAG = "haidong-school-tag";

    private final DictionaryService dictionaryService;
    private final RegionService regionService;
    private final MasterDataService masterDataService;

    @GetMapping("/system/dictionaries")
    public String dictionaries(@ModelAttribute("query") DictionaryWorkbenchQuery query,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        normalizeQuery(query);
        populateWorkbench(model, query);
        return AjaxRequestSupport.isAjax(requestedWith) ? "system/dictionaries :: listContent" : "system/dictionaries";
    }

    @GetMapping("/system/regions")
    public String redirectRegions() {
        return "redirect:/system/dictionaries?tab=common&section=region";
    }

    @GetMapping("/system/dictionaries/items/new")
    public String newDictionaryItem(@RequestParam String tab,
            @RequestParam String section,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        populateDictionaryItemForm(model, section, "新增" + resolveSectionTitle(tab, section),
                "/system/dictionaries/items?tab=" + tab + "&section=" + section, new DictionaryItemForm());
        return AjaxRequestSupport.isAjax(requestedWith)
                ? "system/dictionary-item-form :: drawerContent"
                : "system/dictionary-item-form";
    }

    @PostMapping("/system/dictionaries/items")
    public String createDictionaryItem(@RequestParam String tab,
            @RequestParam String section,
            @Valid @ModelAttribute("dictForm") DictionaryItemForm request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateDictionaryItemForm(model, section, "新增" + resolveSectionTitle(tab, section),
                    "/system/dictionaries/items?tab=" + tab + "&section=" + section, request);
            return AjaxRequestSupport.isAjax(requestedWith)
                    ? "system/dictionary-item-form :: drawerContent"
                    : "system/dictionary-item-form";
        }
        dictionaryService.create(resolveDictionaryType(tab, section), request);
        redirectAttributes.addFlashAttribute("successMessage", resolveSectionTitle(tab, section) + "新增成功");
        return redirectToWorkbench(tab, section, null, null);
    }

    @GetMapping("/system/dictionaries/items/{id}/edit")
    public String editDictionaryItem(@PathVariable Long id,
            @RequestParam String tab,
            @RequestParam String section,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        DictItem item = dictionaryService.getById(id);
        DictionaryItemForm form = new DictionaryItemForm();
        form.setId(item.getId());
        form.setDictLabel(item.getDictLabel());
        form.setDictValue(item.getDictValue());
        populateDictionaryItemForm(model, section, "编辑" + resolveSectionTitle(tab, section),
                "/system/dictionaries/items/" + id + "?tab=" + tab + "&section=" + section, form);
        return AjaxRequestSupport.isAjax(requestedWith)
                ? "system/dictionary-item-form :: drawerContent"
                : "system/dictionary-item-form";
    }

    @PostMapping("/system/dictionaries/items/{id}")
    public String updateDictionaryItem(@PathVariable Long id,
            @RequestParam String tab,
            @RequestParam String section,
            @Valid @ModelAttribute("dictForm") DictionaryItemForm request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateDictionaryItemForm(model, section, "编辑" + resolveSectionTitle(tab, section),
                    "/system/dictionaries/items/" + id + "?tab=" + tab + "&section=" + section, request);
            return AjaxRequestSupport.isAjax(requestedWith)
                    ? "system/dictionary-item-form :: drawerContent"
                    : "system/dictionary-item-form";
        }
        dictionaryService.update(id, request);
        redirectAttributes.addFlashAttribute("successMessage", resolveSectionTitle(tab, section) + "更新成功");
        return redirectToWorkbench(tab, section, null, null);
    }

    @PostMapping("/system/dictionaries/items/{id}/delete")
    public String deleteDictionaryItem(@PathVariable Long id,
            @RequestParam String tab,
            @RequestParam String section,
            RedirectAttributes redirectAttributes) {
        try {
            dictionaryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", resolveSectionTitle(tab, section) + "删除成功");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return redirectToWorkbench(tab, section, null, null);
    }

    @GetMapping("/system/majors/new")
    public String newMajor(@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        populateMajorForm(model, new MajorForm(), "新增专业名称", "/system/majors");
        return AjaxRequestSupport.isAjax(requestedWith) ? "system/major-form :: drawerContent" : "system/major-form";
    }

    @PostMapping("/system/majors")
    public String createMajor(@Valid @ModelAttribute("majorForm") MajorForm request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateMajorForm(model, request, "新增专业名称", "/system/majors");
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/major-form :: drawerContent" : "system/major-form";
        }
        try {
            masterDataService.createMajor(request);
            redirectAttributes.addFlashAttribute("successMessage", "专业名称新增成功");
        } catch (IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateMajorForm(model, request, "新增专业名称", "/system/majors");
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/major-form :: drawerContent" : "system/major-form";
        }
        return redirectToWorkbench(TAB_MAJOR, TAB_MAJOR, null, null);
    }

    @GetMapping("/system/majors/{id}/edit")
    public String editMajor(@PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        MajorCatalog major = masterDataService.getMajorById(id);
        MajorForm form = new MajorForm();
        form.setId(major.getId());
        form.setMajorCode(major.getMajorCode());
        form.setMajorName(major.getMajorName());
        form.setCategoryDictItemId(major.getCategoryDictItemId());
        populateMajorForm(model, form, "编辑专业名称", "/system/majors/" + id);
        return AjaxRequestSupport.isAjax(requestedWith) ? "system/major-form :: drawerContent" : "system/major-form";
    }

    @PostMapping("/system/majors/{id}")
    public String updateMajor(@PathVariable Long id,
            @Valid @ModelAttribute("majorForm") MajorForm request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateMajorForm(model, request, "编辑专业名称", "/system/majors/" + id);
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/major-form :: drawerContent" : "system/major-form";
        }
        try {
            masterDataService.updateMajor(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "专业名称更新成功");
        } catch (IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateMajorForm(model, request, "编辑专业名称", "/system/majors/" + id);
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/major-form :: drawerContent" : "system/major-form";
        }
        return redirectToWorkbench(TAB_MAJOR, TAB_MAJOR, null, null);
    }

    @PostMapping("/system/majors/{id}/delete")
    public String deleteMajor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            masterDataService.deleteMajor(id);
            redirectAttributes.addFlashAttribute("successMessage", "专业名称删除成功");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return redirectToWorkbench(TAB_MAJOR, TAB_MAJOR, null, null);
    }

    @GetMapping("/system/majors/template")
    public ResponseEntity<byte[]> downloadMajorTemplate() {
        return excelResponse(masterDataService.generateMajorImportTemplate(), "专业信息导入模板.xlsx");
    }

    @PostMapping("/system/majors/import")
    public String importMajors(MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("importMessage", "请先选择需要上传的 Excel 文件");
            return "redirect:/system/dictionaries?tab=major";
        }
        DataImportResult result = masterDataService.importMajorsFromExcel(file);
        redirectAttributes.addFlashAttribute("importResult", result);
        if (result.getFailCount() > 0) {
            redirectAttributes.addFlashAttribute("importMessage",
                    "导入失败，发现 " + result.getFailCount() + " 处问题，请修正后重新上传");
        } else {
            redirectAttributes.addFlashAttribute("importMessage",
                    "导入成功，共导入 " + result.getSuccessCount() + " 条专业");
        }
        return "redirect:/system/dictionaries?tab=major";
    }

    @GetMapping("/system/schools/template")
    public ResponseEntity<byte[]> downloadSchoolTemplate() {
        return excelResponse(masterDataService.generateSchoolImportTemplate(), "学校信息导入模板.xlsx");
    }

    @PostMapping("/system/schools/import")
    public String importSchools(MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("importMessage", "请先选择需要上传的 Excel 文件");
            return "redirect:/system/dictionaries?tab=school";
        }
        DataImportResult result = masterDataService.importSchoolsFromExcel(file);
        redirectAttributes.addFlashAttribute("importResult", result);
        if (result.getFailCount() > 0) {
            redirectAttributes.addFlashAttribute("importMessage",
                    "导入失败，发现 " + result.getFailCount() + " 处问题，请修正后重新上传");
        } else {
            redirectAttributes.addFlashAttribute("importMessage",
                    "导入成功，共导入 " + result.getSuccessCount() + " 所学校");
        }
        return "redirect:/system/dictionaries?tab=school";
    }

    @GetMapping("/system/school-tags/new")
    public String newSchoolTag(@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        populateSchoolTagForm(model, new SchoolTagForm(), "新增学校标签", "/system/school-tags");
        return AjaxRequestSupport.isAjax(requestedWith)
                ? "system/school-tag-form :: drawerContent"
                : "system/school-tag-form";
    }

    @PostMapping("/system/school-tags")
    public String createSchoolTag(@Valid @ModelAttribute("schoolTagForm") SchoolTagForm request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateSchoolTagForm(model, request, "新增学校标签", "/system/school-tags");
            return AjaxRequestSupport.isAjax(requestedWith)
                    ? "system/school-tag-form :: drawerContent"
                    : "system/school-tag-form";
        }
        try {
            masterDataService.createSchoolTag(request);
            redirectAttributes.addFlashAttribute("successMessage", "学校标签新增成功");
        } catch (IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateSchoolTagForm(model, request, "新增学校标签", "/system/school-tags");
            return AjaxRequestSupport.isAjax(requestedWith)
                    ? "system/school-tag-form :: drawerContent"
                    : "system/school-tag-form";
        }
        return redirectToWorkbench(TAB_SCHOOL_CATEGORY, SECTION_SCHOOL_TAG, null, null);
    }

    @GetMapping("/system/school-tags/{id}/edit")
    public String editSchoolTag(@PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        SchoolTag tag = masterDataService.getSchoolTagById(id);
        SchoolTagForm form = new SchoolTagForm();
        form.setId(tag.getId());
        form.setTagName(tag.getTagName());
        populateSchoolTagForm(model, form, "编辑学校标签", "/system/school-tags/" + id);
        return AjaxRequestSupport.isAjax(requestedWith)
                ? "system/school-tag-form :: drawerContent"
                : "system/school-tag-form";
    }

    @PostMapping("/system/school-tags/{id}")
    public String updateSchoolTag(@PathVariable Long id,
            @Valid @ModelAttribute("schoolTagForm") SchoolTagForm request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateSchoolTagForm(model, request, "编辑学校标签", "/system/school-tags/" + id);
            return AjaxRequestSupport.isAjax(requestedWith)
                    ? "system/school-tag-form :: drawerContent"
                    : "system/school-tag-form";
        }
        try {
            masterDataService.updateSchoolTag(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "学校标签更新成功");
        } catch (IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateSchoolTagForm(model, request, "编辑学校标签", "/system/school-tags/" + id);
            return AjaxRequestSupport.isAjax(requestedWith)
                    ? "system/school-tag-form :: drawerContent"
                    : "system/school-tag-form";
        }
        return redirectToWorkbench(TAB_SCHOOL_CATEGORY, SECTION_SCHOOL_TAG, null, null);
    }

    @PostMapping("/system/school-tags/{id}/delete")
    public String deleteSchoolTag(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            masterDataService.deleteSchoolTag(id);
            redirectAttributes.addFlashAttribute("successMessage", "学校标签删除成功");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return redirectToWorkbench(TAB_SCHOOL_CATEGORY, SECTION_SCHOOL_TAG, null, null);
    }

    @PostMapping("/system/analytics/haidong-school-tags")
    public String saveAnalyticsSchoolTags(@RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            RedirectAttributes redirectAttributes) {
        masterDataService.saveAnalyticsSchoolTagIds(tagIds);
        redirectAttributes.addFlashAttribute("successMessage", "海东籍顶尖高校分析配置保存成功");
        return redirectToWorkbench(TAB_ANALYTICS, SECTION_ANALYTICS_HAIDONG_TAG, null, null);
    }

    @GetMapping("/system/schools/new")
    public String newSchool(@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        populateSchoolForm(model, new SchoolForm(), "新增学校", "/system/schools");
        return AjaxRequestSupport.isAjax(requestedWith) ? "system/school-form :: drawerContent" : "system/school-form";
    }

    @PostMapping("/system/schools")
    public String createSchool(@Valid @ModelAttribute("schoolForm") SchoolForm request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateSchoolForm(model, request, "新增学校", "/system/schools");
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/school-form :: drawerContent" : "system/school-form";
        }
        try {
            masterDataService.createSchool(request);
            redirectAttributes.addFlashAttribute("successMessage", "学校新增成功");
        } catch (IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateSchoolForm(model, request, "新增学校", "/system/schools");
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/school-form :: drawerContent" : "system/school-form";
        }
        return redirectToWorkbench(TAB_SCHOOL, TAB_SCHOOL, null, null);
    }

    @GetMapping("/system/schools/{id}/edit")
    public String editSchool(@PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        School school = masterDataService.getSchoolById(id);
        SchoolForm form = new SchoolForm();
        form.setId(school.getId());
        form.setSchoolCode(school.getSchoolCode());
        form.setSchoolName(school.getSchoolName());
        form.setCategoryDictItemId(school.getCategoryDictItemId());
        form.setTagIds(school.getTagIds());
        populateSchoolForm(model, form, "编辑学校", "/system/schools/" + id);
        return AjaxRequestSupport.isAjax(requestedWith) ? "system/school-form :: drawerContent" : "system/school-form";
    }

    @PostMapping("/system/schools/{id}")
    public String updateSchool(@PathVariable Long id,
            @Valid @ModelAttribute("schoolForm") SchoolForm request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateSchoolForm(model, request, "编辑学校", "/system/schools/" + id);
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/school-form :: drawerContent" : "system/school-form";
        }
        try {
            masterDataService.updateSchool(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "学校更新成功");
        } catch (IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateSchoolForm(model, request, "编辑学校", "/system/schools/" + id);
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/school-form :: drawerContent" : "system/school-form";
        }
        return redirectToWorkbench(TAB_SCHOOL, TAB_SCHOOL, null, null);
    }

    @PostMapping("/system/schools/{id}/delete")
    public String deleteSchool(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            masterDataService.deleteSchool(id);
            redirectAttributes.addFlashAttribute("successMessage", "学校删除成功");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return redirectToWorkbench(TAB_SCHOOL, TAB_SCHOOL, null, null);
    }

    @GetMapping("/system/regions/new")
    public String newRegion(@RequestParam(required = false, defaultValue = "1") Integer regionLevel,
            @RequestParam(required = false, defaultValue = "0") Long parentId,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        RegionForm form = new RegionForm();
        form.setRegionLevel(regionLevel);
        form.setParentId(regionLevel != null && regionLevel == 1 ? 0L : parentId);
        populateRegionForm(model, form, "新增区域", "/system/regions");
        return AjaxRequestSupport.isAjax(requestedWith) ? "system/region-form :: drawerContent" : "system/region-form";
    }

    @PostMapping("/system/regions")
    public String createRegion(@Valid @ModelAttribute("regionForm") RegionForm request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateRegionForm(model, request, "新增区域", "/system/regions");
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/region-form :: drawerContent" : "system/region-form";
        }
        try {
            regionService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "区域新增成功");
        } catch (IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateRegionForm(model, request, "新增区域", "/system/regions");
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/region-form :: drawerContent" : "system/region-form";
        }
        return redirectToWorkbench(TAB_COMMON, SECTION_REGION, request.getRegionLevel(), request.getParentId());
    }

    @GetMapping("/system/regions/{id}/edit")
    public String editRegion(@PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        Region region = regionService.getById(id);
        RegionForm form = new RegionForm();
        form.setId(region.getId());
        form.setParentId(region.getParentId());
        form.setRegionCode(region.getRegionCode());
        form.setRegionName(region.getRegionName());
        form.setRegionLevel(region.getRegionLevel());
        populateRegionForm(model, form, "编辑区域", "/system/regions/" + id);
        return AjaxRequestSupport.isAjax(requestedWith) ? "system/region-form :: drawerContent" : "system/region-form";
    }

    @PostMapping("/system/regions/{id}")
    public String updateRegion(@PathVariable Long id,
            @Valid @ModelAttribute("regionForm") RegionForm request,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateRegionForm(model, request, "编辑区域", "/system/regions/" + id);
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/region-form :: drawerContent" : "system/region-form";
        }
        try {
            regionService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "区域更新成功");
        } catch (IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateRegionForm(model, request, "编辑区域", "/system/regions/" + id);
            return AjaxRequestSupport.isAjax(requestedWith) ? "system/region-form :: drawerContent" : "system/region-form";
        }
        return redirectToWorkbench(TAB_COMMON, SECTION_REGION, request.getRegionLevel(), request.getParentId());
    }

    @PostMapping("/system/regions/{id}/delete")
    public String deleteRegion(@PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Integer regionLevel,
            @RequestParam(required = false) Long parentId,
            RedirectAttributes redirectAttributes) {
        try {
            regionService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "区域删除成功");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return redirectToWorkbench(TAB_COMMON, SECTION_REGION, regionLevel, parentId);
    }

    @ResponseBody
    @RequestMapping("/api/dictionaries/{dictType}")
    public Object dictionariesByType(@PathVariable String dictType) {
        return dictionaryService.getByType(dictType);
    }

    @ResponseBody
    @GetMapping("/api/regions")
    public Object regionsData() {
        return regionService.getRegionTree();
    }

    private void populateWorkbench(Model model, DictionaryWorkbenchQuery query) {
        long totalCount;
        String section = query.getSection();
        String viewKind;

        switch (query.getTab()) {
            case TAB_COMMON -> {
                if (SECTION_REGION.equals(section)) {
                    viewKind = "region";
                    totalCount = regionService.count(query.getRegionLevel(), query.getParentId(), query.getKeyword());
                    normalizePage(query, totalCount);
                    model.addAttribute("records", regionService.search(
                            query.getRegionLevel(), query.getParentId(), query.getKeyword(),
                            query.getSafePage(), query.getSafePageSize()));
                    populateRegionOptions(model, query.getRegionLevel());
                } else {
                    viewKind = "dict";
                    String dictType = resolveDictionaryType(query.getTab(), section);
                    totalCount = dictionaryService.countByType(dictType, query.getKeyword());
                    normalizePage(query, totalCount);
                    model.addAttribute("records", dictionaryService.searchByType(
                            dictType, query.getKeyword(), query.getSafePage(), query.getSafePageSize()));
                }
            }
            case TAB_MAJOR_CATEGORY -> {
                viewKind = "dict";
                totalCount = dictionaryService.countByType("major_category", query.getKeyword());
                normalizePage(query, totalCount);
                model.addAttribute("records", dictionaryService.searchByType(
                        "major_category", query.getKeyword(), query.getSafePage(), query.getSafePageSize()));
            }
            case TAB_MAJOR -> {
                viewKind = "major";
                totalCount = masterDataService.countMajors(query.getKeyword());
                normalizePage(query, totalCount);
                model.addAttribute("records", masterDataService.searchMajors(
                        query.getKeyword(), query.getSafePage(), query.getSafePageSize()));
            }
            case TAB_SCHOOL_CATEGORY -> {
                if (SECTION_SCHOOL_TAG.equals(section)) {
                    viewKind = "school-tag";
                    totalCount = masterDataService.countSchoolTags(query.getKeyword());
                    normalizePage(query, totalCount);
                    model.addAttribute("records", masterDataService.searchSchoolTags(
                            query.getKeyword(), query.getSafePage(), query.getSafePageSize()));
                } else {
                    viewKind = "dict";
                    totalCount = dictionaryService.countByType("school_category", query.getKeyword());
                    normalizePage(query, totalCount);
                    model.addAttribute("records", dictionaryService.searchByType(
                            "school_category", query.getKeyword(), query.getSafePage(), query.getSafePageSize()));
                }
            }
            case TAB_SCHOOL -> {
                viewKind = "school";
                totalCount = masterDataService.countSchools(query.getKeyword());
                normalizePage(query, totalCount);
                model.addAttribute("records", masterDataService.searchSchools(
                        query.getKeyword(), query.getSafePage(), query.getSafePageSize()));
            }
            case TAB_ENTERPRISE -> {
                viewKind = "dict";
                String dictType = resolveDictionaryType(query.getTab(), section);
                totalCount = dictionaryService.countByType(dictType, query.getKeyword());
                normalizePage(query, totalCount);
                model.addAttribute("records", dictionaryService.searchByType(
                        dictType, query.getKeyword(), query.getSafePage(), query.getSafePageSize()));
            }
            case TAB_ANALYTICS -> {
                viewKind = "analytics-haidong-tag";
                totalCount = 0;
                model.addAttribute("allSchoolTags", masterDataService.getAllSchoolTags());
                model.addAttribute("selectedTagIds", masterDataService.getAnalyticsSchoolTagIds());
            }
            default -> throw new IllegalStateException("未知页签");
        }

        model.addAttribute("pageTitle", "基础数据管理");
        model.addAttribute("query", query);
        model.addAttribute("viewKind", viewKind);
        model.addAttribute("viewTitle", resolveSectionTitle(query.getTab(), query.getSection()));
        model.addAttribute("viewSubtitle", resolveSectionSubtitle(query.getTab(), query.getSection()));
        model.addAttribute("createUrl", resolveCreateUrl(query));
        PaginationSupport.apply(model, query.getSafePage(), query.getSafePageSize(), totalCount);
    }

    private void populateMajorForm(Model model, MajorForm form, String formTitle, String formAction) {
        model.addAttribute("pageTitle", "基础数据管理");
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("majorForm", form);
        model.addAttribute("majorCategories", dictionaryService.getByType("major_category"));
    }

    private void populateDictionaryItemForm(Model model, String section, String formTitle, String formAction,
            DictionaryItemForm form) {
        model.addAttribute("pageTitle", "基础数据管理");
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("sectionTitle", resolveSectionTitle(TAB_COMMON, section));
        model.addAttribute("dictLabelFieldLabel", resolveDictLabelFieldLabel(section));
        model.addAttribute("dictValueFieldLabel", resolveDictValueFieldLabel(section));
        model.addAttribute("dictForm", form);
    }

    private void populateSchoolTagForm(Model model, SchoolTagForm form, String formTitle, String formAction) {
        model.addAttribute("pageTitle", "基础数据管理");
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("schoolTagForm", form);
    }

    private void populateSchoolForm(Model model, SchoolForm form, String formTitle, String formAction) {
        model.addAttribute("pageTitle", "基础数据管理");
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("schoolForm", form);
        model.addAttribute("schoolCategories", dictionaryService.getByType("school_category"));
        model.addAttribute("schoolTags", masterDataService.getAllSchoolTags());
    }

    private void populateRegionForm(Model model, RegionForm form, String formTitle, String formAction) {
        model.addAttribute("pageTitle", "基础数据管理");
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("regionForm", form);
        model.addAttribute("provinceOptions", regionService.getRegionsByLevel(1));
        model.addAttribute("cityOptions", regionService.getRegionsByLevel(2));
        model.addAttribute("parentRegionOptions", form.getRegionLevel() == null || form.getRegionLevel() <= 1
                ? List.of()
                : regionService.getRegionsByLevel(form.getRegionLevel() - 1));
    }

    private void populateRegionOptions(Model model, Integer regionLevel) {
        model.addAttribute("provinceOptions", regionService.getRegionsByLevel(1));
        model.addAttribute("cityOptions", regionService.getRegionsByLevel(2));
        model.addAttribute("parentRegionOptions", regionLevel == null || regionLevel <= 1
                ? List.of()
                : regionService.getRegionsByLevel(regionLevel - 1));
    }

    private void normalizeQuery(DictionaryWorkbenchQuery query) {
        String tab = query.getTab();
        String section = query.getSection();
        if (!List.of(TAB_COMMON, TAB_MAJOR_CATEGORY, TAB_MAJOR, TAB_SCHOOL_CATEGORY, TAB_SCHOOL, TAB_ENTERPRISE, TAB_ANALYTICS)
                .contains(tab)) {
            query.setTab(TAB_COMMON);
        }
        switch (query.getTab()) {
            case TAB_COMMON -> {
                if (section == null || !List.of("gender", "ethnicity", "political_status", "education_level", "degree", SECTION_REGION).contains(section)) {
                    query.setSection("gender");
                }
                if (!SECTION_REGION.equals(query.getSection())) {
                    query.setParentId(null);
                }
            }
            case TAB_MAJOR_CATEGORY -> query.setSection("major_category");
            case TAB_MAJOR -> query.setSection(TAB_MAJOR);
            case TAB_SCHOOL_CATEGORY -> {
                if (section == null || !List.of("school_category", SECTION_SCHOOL_TAG).contains(section)) {
                    query.setSection("school_category");
                }
            }
            case TAB_SCHOOL -> query.setSection(TAB_SCHOOL);
            case TAB_ENTERPRISE -> {
                if (section == null || !List.of(
                        "enterprise_scale", "enterprise_nature", "enterprise_industry",
                        "experience_requirement", "salary_range").contains(section)) {
                    query.setSection("enterprise_scale");
                }
            }
            case TAB_ANALYTICS -> query.setSection(SECTION_ANALYTICS_HAIDONG_TAG);
            default -> {
            }
        }
        // For non-region sections, set default regionLevel
        if (!SECTION_REGION.equals(query.getSection())) {
            if (query.getRegionLevel() == null || query.getRegionLevel() < 1 || query.getRegionLevel() > 3) {
                query.setRegionLevel(1);
            }
        }
        // Clear parentId for level 1 or non-region sections
        if (query.getRegionLevel() == null || query.getRegionLevel() == 1 || !SECTION_REGION.equals(query.getSection())) {
            query.setParentId(null);
        }
    }

    private void normalizePage(DictionaryWorkbenchQuery query, long totalCount) {
        int totalPages = (int) Math.max(1, (totalCount + query.getSafePageSize() - 1) / query.getSafePageSize());
        if (query.getSafePage() > totalPages) {
            query.setPage(totalPages);
        }
    }

    private String resolveDictionaryType(String tab, String section) {
        return switch (tab) {
            case TAB_COMMON -> section;
            case TAB_MAJOR_CATEGORY -> "major_category";
            case TAB_SCHOOL_CATEGORY -> "school_category";
            case TAB_ENTERPRISE -> section;
            default -> throw new IllegalStateException("当前页签不使用通用字典项");
        };
    }

    private String resolveSectionTitle(String tab, String section) {
        return switch (section) {
            case "gender" -> "性别";
            case "ethnicity" -> "民族";
            case "political_status" -> "政治面貌";
            case "education_level" -> "学历";
            case "degree" -> "学位";
            case SECTION_REGION -> "区域数据";
            case "major_category" -> "专业类别";
            case TAB_MAJOR -> "专业名称";
            case "school_category" -> "学校类别";
            case SECTION_SCHOOL_TAG -> "学校标签";
            case TAB_SCHOOL -> "学校";
            case "enterprise_scale" -> "企业规模";
            case "enterprise_nature" -> "企业性质";
            case "enterprise_industry" -> "企业行业";
            case "experience_requirement" -> "经验要求";
            case "salary_range" -> "薪资待遇";
            case SECTION_ANALYTICS_HAIDONG_TAG -> "海东籍顶尖高校分析";
            default -> switch (tab) {
                case TAB_MAJOR -> "专业名称";
                case TAB_SCHOOL -> "学校";
                default -> "基础数据";
            };
        };
    }

    private String resolveSectionSubtitle(String tab, String section) {
        return switch (section) {
            case SECTION_REGION -> "支持省、市、区（县）级联浏览与维护";
            case TAB_MAJOR -> "每条专业名称必须绑定一个专业类别";
            case "school_category" -> "维护学校的主类别";
            case SECTION_SCHOOL_TAG -> "学校标签作为学校附加属性进行维护";
            case TAB_SCHOOL -> "学校绑定一个学校类别，并可关联多个学校标签";
            case "enterprise_scale", "enterprise_nature", "enterprise_industry" -> "企业信息录入与筛选使用统一字典来源";
            case "experience_requirement", "salary_range" -> "招聘岗位录入、筛选与统计使用统一字典来源";
            case SECTION_ANALYTICS_HAIDONG_TAG -> "选择参与数据分析图表的学校标签";
            default -> "统一维护受控基础数据，供录入、筛选和校验使用";
        };
    }

    private String resolveDictLabelFieldLabel(String section) {
        return switch (section) {
            case "education_level" -> "学历名称";
            case "degree" -> "学位名称";
            default -> "显示名称";
        };
    }

    private String resolveDictValueFieldLabel(String section) {
        return switch (section) {
            case "education_level" -> "学历编码";
            case "degree" -> "学位编码";
            default -> "字典值";
        };
    }

    private String resolveCreateUrl(DictionaryWorkbenchQuery query) {
        return switch (query.getTab()) {
            case TAB_COMMON, TAB_MAJOR_CATEGORY, TAB_SCHOOL_CATEGORY, TAB_ENTERPRISE ->
                    "school-tag".equals(query.getSection())
                            ? "/system/school-tags/new"
                            : SECTION_REGION.equals(query.getSection())
                                    ? "/system/regions/new?regionLevel=" + query.getRegionLevel()
                                    + (query.getParentId() == null ? "" : "&parentId=" + query.getParentId())
                                    : "/system/dictionaries/items/new?tab=" + query.getTab() + "&section=" + query.getSection();
            case TAB_MAJOR -> "/system/majors/new";
            case TAB_SCHOOL -> "/system/schools/new";
            case TAB_ANALYTICS -> null;
            default -> "#";
        };
    }

    private String redirectToWorkbench(String tab, String section, Integer regionLevel, Long parentId) {
        StringBuilder builder = new StringBuilder("redirect:/system/dictionaries?tab=").append(tab);
        if (section != null) {
            builder.append("&section=").append(section);
        }
        if (regionLevel != null) {
            builder.append("&regionLevel=").append(regionLevel);
        }
        if (parentId != null && parentId > 0) {
            builder.append("&parentId=").append(parentId);
        }
        return builder.toString();
    }

    private ResponseEntity<byte[]> excelResponse(byte[] bytes, String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"download.xlsx\"; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }
}
