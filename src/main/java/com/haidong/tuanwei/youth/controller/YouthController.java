package com.haidong.tuanwei.youth.controller;

import com.haidong.tuanwei.common.security.AdminUserDetails;
import com.haidong.tuanwei.common.web.AjaxRequestSupport;
import com.haidong.tuanwei.common.web.PaginationSupport;
import com.haidong.tuanwei.system.service.DictionaryService;
import com.haidong.tuanwei.system.service.MasterDataService;
import com.haidong.tuanwei.system.service.RegionService;
import com.haidong.tuanwei.youth.dto.YouthFormRequest;
import com.haidong.tuanwei.youth.dto.YouthImportResult;
import com.haidong.tuanwei.youth.dto.YouthSearchRequest;
import com.haidong.tuanwei.youth.entity.YouthInfo;
import com.haidong.tuanwei.youth.service.YouthInfoService;
import com.haidong.tuanwei.youth.support.YouthTypeHelper;
import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
public class YouthController {

    private final DictionaryService dictionaryService;
    private final MasterDataService masterDataService;
    private final RegionService regionService;
    private final YouthInfoService youthInfoService;

    @GetMapping("/youth/{type}")
    public String youthPage(@PathVariable String type,
            @ModelAttribute("query") YouthSearchRequest query,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        long totalCount = youthInfoService.count(YouthTypeHelper.code(type), query);
        normalizePage(query, totalCount);

        model.addAttribute("pageTitle", "青年信息库");
        model.addAttribute("youthType", type);
        model.addAttribute("youthTypeLabel", YouthTypeHelper.label(type));
        model.addAttribute("records", youthInfoService.search(YouthTypeHelper.code(type), query));
        PaginationSupport.apply(model, query.getSafePage(), query.getSafePageSize(), totalCount);
        populateYouthOptions(model);
        return AjaxRequestSupport.isAjax(requestedWith) ? "youth/list :: listContent" : "youth/list";
    }

    @GetMapping("/youth/{type}/template")
    public ResponseEntity<byte[]> downloadYouthTemplate(@PathVariable String type) {
        return excelResponse(youthInfoService.generateImportTemplate(), YouthTypeHelper.label(type) + "信息导入模板.xlsx");
    }

    @PostMapping("/youth/{type}/import")
    public String importYouth(@PathVariable String type,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            MultipartFile file,
            RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            log.warn("Youth import skipped because no file was provided: type={}, operatorId={}",
                    type, currentUser == null ? null : currentUser.getId());
            redirectAttributes.addFlashAttribute("importMessage", "请先选择需要上传的 Excel 文件");
            return "redirect:/youth/" + type;
        }
        YouthImportResult result = youthInfoService.importFromExcel(YouthTypeHelper.code(type), file, currentUser.getId());
        redirectAttributes.addFlashAttribute("importResult", result);
        redirectAttributes.addFlashAttribute("importMessage",
                "导入完成：成功 " + result.getSuccessCount() + " 条，失败 " + result.getFailCount() + " 条");
        return "redirect:/youth/" + type;
    }

    @GetMapping("/youth/{type}/export")
    public ResponseEntity<byte[]> exportYouth(@PathVariable String type,
            @ModelAttribute YouthSearchRequest query) {
        return excelResponse(youthInfoService.exportExcel(YouthTypeHelper.code(type), query),
                YouthTypeHelper.label(type) + "导出数据.xlsx");
    }

    @GetMapping("/youth/{type}/new")
    public String newYouth(@PathVariable String type,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "青年信息库");
        model.addAttribute("youthType", type);
        model.addAttribute("youthTypeLabel", YouthTypeHelper.label(type));
        model.addAttribute("formAction", "/youth/" + type);
        model.addAttribute("formTitle", "新增" + YouthTypeHelper.label(type));
        model.addAttribute("youthForm", new YouthFormRequest());
        populateYouthOptions(model);
        return AjaxRequestSupport.isAjax(requestedWith) ? "youth/form :: drawerContent" : "youth/form";
    }

    @PostMapping("/youth/{type}")
    public String createYouth(@PathVariable String type,
            @Valid @ModelAttribute("youthForm") YouthFormRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "青年信息库");
            model.addAttribute("youthType", type);
            model.addAttribute("youthTypeLabel", YouthTypeHelper.label(type));
            model.addAttribute("formAction", "/youth/" + type);
            model.addAttribute("formTitle", "新增" + YouthTypeHelper.label(type));
            populateYouthOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "youth/form :: drawerContent" : "youth/form";
        }
        try {
            youthInfoService.create(YouthTypeHelper.code(type), request, currentUser.getId());
        } catch (IllegalStateException ex) {
            log.warn("Youth create failed: type={}, operatorId={}, reason={}",
                    type, currentUser == null ? null : currentUser.getId(), ex.getMessage());
            model.addAttribute("pageTitle", "青年信息库");
            model.addAttribute("youthType", type);
            model.addAttribute("youthTypeLabel", YouthTypeHelper.label(type));
            model.addAttribute("formAction", "/youth/" + type);
            model.addAttribute("formTitle", "新增" + YouthTypeHelper.label(type));
            model.addAttribute("formError", ex.getMessage());
            populateYouthOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "youth/form :: drawerContent" : "youth/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "青年信息新增成功");
        return "redirect:/youth/" + type;
    }

    @GetMapping("/youth/{type}/{id}")
    public String youthDetail(@PathVariable String type,
            @PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "青年信息详情");
        model.addAttribute("youthType", type);
        model.addAttribute("youthTypeLabel", YouthTypeHelper.label(type));
        model.addAttribute("record", youthInfoService.getById(id));
        return AjaxRequestSupport.isAjax(requestedWith) ? "youth/detail :: drawerContent" : "youth/detail";
    }

    @GetMapping("/youth/{type}/{id}/edit")
    public String editYouth(@PathVariable String type,
            @PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "青年信息库");
        model.addAttribute("youthType", type);
        model.addAttribute("youthTypeLabel", YouthTypeHelper.label(type));
        model.addAttribute("formAction", "/youth/" + type + "/" + id);
        model.addAttribute("formTitle", "编辑" + YouthTypeHelper.label(type));
        model.addAttribute("youthForm", toForm(youthInfoService.getById(id)));
        populateYouthOptions(model);
        return AjaxRequestSupport.isAjax(requestedWith) ? "youth/form :: drawerContent" : "youth/form";
    }

    @PostMapping("/youth/{type}/{id}")
    public String updateYouth(@PathVariable String type,
            @PathVariable Long id,
            @Valid @ModelAttribute("youthForm") YouthFormRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "青年信息库");
            model.addAttribute("youthType", type);
            model.addAttribute("youthTypeLabel", YouthTypeHelper.label(type));
            model.addAttribute("formAction", "/youth/" + type + "/" + id);
            model.addAttribute("formTitle", "编辑" + YouthTypeHelper.label(type));
            populateYouthOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "youth/form :: drawerContent" : "youth/form";
        }
        try {
            youthInfoService.update(id, request, currentUser.getId());
        } catch (IllegalStateException ex) {
            log.warn("Youth update failed: type={}, id={}, operatorId={}, reason={}",
                    type, id, currentUser == null ? null : currentUser.getId(), ex.getMessage());
            model.addAttribute("pageTitle", "青年信息库");
            model.addAttribute("youthType", type);
            model.addAttribute("youthTypeLabel", YouthTypeHelper.label(type));
            model.addAttribute("formAction", "/youth/" + type + "/" + id);
            model.addAttribute("formTitle", "编辑" + YouthTypeHelper.label(type));
            model.addAttribute("formError", ex.getMessage());
            populateYouthOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "youth/form :: drawerContent" : "youth/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "青年信息更新成功");
        return "redirect:/youth/" + type;
    }

    @PostMapping("/youth/{type}/{id}/delete")
    public String deleteYouth(@PathVariable String type,
            @PathVariable Long id,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        youthInfoService.delete(id, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "青年信息删除成功");
        return "redirect:/youth/" + type;
    }

    @PostMapping("/youth/{type}/batch-delete")
    public String batchDeleteYouth(@PathVariable String type,
            @RequestParam(value = "ids", required = false) List<Long> ids,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        int deletedCount = youthInfoService.deleteBatch(YouthTypeHelper.code(type), ids, currentUser.getId());
        if (deletedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", "已批量删除 " + deletedCount + " 条青年信息");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "请先选择需要删除的数据");
        }
        return "redirect:/youth/" + type;
    }

    private void populateYouthOptions(Model model) {
        model.addAttribute("genders", dictionaryService.getByType("gender"));
        model.addAttribute("ethnicities", dictionaryService.getByType("ethnicity"));
        model.addAttribute("politicalStatuses", dictionaryService.getByType("political_status"));
        model.addAttribute("educationLevels", dictionaryService.getByType("education_level"));
        model.addAttribute("degrees", dictionaryService.getByType("degree"));
        model.addAttribute("majorCategories", dictionaryService.getByType("major_category"));
        model.addAttribute("regions", regionService.getRegionTree());
        model.addAttribute("schools", masterDataService.getAllSchoolsForSelect());
        model.addAttribute("majors", masterDataService.getAllMajors());
    }

    private YouthFormRequest toForm(YouthInfo youthInfo) {
        YouthFormRequest form = new YouthFormRequest();
        form.setId(youthInfo.getId());
        form.setName(youthInfo.getName());
        form.setGender(youthInfo.getGender());
        form.setBirthDate(youthInfo.getBirthDate() == null ? null : youthInfo.getBirthDate().toString());
        form.setEthnicity(youthInfo.getEthnicity());
        form.setPoliticalStatus(youthInfo.getPoliticalStatus());
        form.setNativeProvinceCode(youthInfo.getNativeProvinceCode());
        form.setNativeCityCode(youthInfo.getNativeCityCode());
        form.setNativeCountyCode(youthInfo.getNativeCountyCode());
        form.setEducationLevel(youthInfo.getEducationCode());
        form.setDegreeCode(youthInfo.getDegreeCode());
        form.setSchoolCode(youthInfo.getSchoolCode());
        form.setSchoolProvinceCode(youthInfo.getSchoolProvinceCode());
        form.setSchoolCityCode(youthInfo.getSchoolCityCode());
        form.setSchoolCountyCode(youthInfo.getSchoolCountyCode());
        form.setMajorCode(youthInfo.getMajorCode());
        form.setMajorCategory(youthInfo.getMajorCategory());
        form.setRecruitmentYear(youthInfo.getRecruitmentYear() == null ? null : String.valueOf(youthInfo.getRecruitmentYear()));
        form.setGraduationDate(youthInfo.getGraduationDate() == null ? null : youthInfo.getGraduationDate().toString());
        form.setEmploymentDirection(youthInfo.getEmploymentDirection());
        form.setPhone(youthInfo.getPhone());
        form.setResidenceProvinceCode(youthInfo.getResidenceProvinceCode());
        form.setResidenceCityCode(youthInfo.getResidenceCityCode());
        form.setResidenceCountyCode(youthInfo.getResidenceCountyCode());
        form.setEmploymentStatus(youthInfo.getEmploymentStatus());
        form.setCurrentJob(youthInfo.getCurrentJob());
        form.setEmploymentCompany(youthInfo.getEmploymentCompany());
        form.setEntrepreneurshipStatus(youthInfo.getEntrepreneurshipStatus());
        form.setEntrepreneurshipProject(youthInfo.getEntrepreneurshipProject());
        form.setEntrepreneurshipDemand(youthInfo.getEntrepreneurshipDemand());
        form.setRemarks(youthInfo.getRemarks());
        return form;
    }

    private ResponseEntity<byte[]> excelResponse(byte[] bytes, String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"download.xlsx\"; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    private void normalizePage(YouthSearchRequest query, long totalCount) {
        int totalPages = (int) Math.max(1, (totalCount + query.getSafePageSize() - 1) / query.getSafePageSize());
        if (query.getSafePage() > totalPages) {
            query.setPage(totalPages);
        }
    }
}
