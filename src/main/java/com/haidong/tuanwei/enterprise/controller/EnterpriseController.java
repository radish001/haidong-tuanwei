package com.haidong.tuanwei.enterprise.controller;

import com.haidong.tuanwei.common.security.AdminUserDetails;
import com.haidong.tuanwei.common.web.AjaxRequestSupport;
import com.haidong.tuanwei.common.web.PaginationSupport;
import com.haidong.tuanwei.enterprise.dto.EnterpriseFormRequest;
import com.haidong.tuanwei.enterprise.dto.EnterpriseSearchRequest;
import com.haidong.tuanwei.enterprise.entity.EnterpriseInfo;
import com.haidong.tuanwei.enterprise.service.EnterpriseService;
import com.haidong.tuanwei.system.service.DictionaryService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
@RequiredArgsConstructor
public class EnterpriseController {

    private static final DateTimeFormatter LICENSE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final EnterpriseService enterpriseService;
    private final DictionaryService dictionaryService;

    @Value("${app.upload.enterprise-license-dir:uploads/enterprise-license}")
    private String enterpriseLicenseDir;

    @GetMapping("/enterprises")
    public String enterprises(@ModelAttribute("query") EnterpriseSearchRequest query,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        long totalCount = enterpriseService.count(query);
        normalizePage(query, totalCount);

        model.addAttribute("pageTitle", "企业信息");
        model.addAttribute("records", enterpriseService.search(query));
        model.addAttribute("industryOptions", dictionaryService.getByType("enterprise_industry"));
        model.addAttribute("natureOptions", dictionaryService.getByType("enterprise_nature"));
        model.addAttribute("scaleOptions", dictionaryService.getByType("enterprise_scale"));
        PaginationSupport.apply(model, query.getSafePage(), query.getSafePageSize(), totalCount);
        return AjaxRequestSupport.isAjax(requestedWith) ? "enterprise/list :: listContent" : "enterprise/list";
    }

    @GetMapping("/enterprises/new")
    public String newEnterprise(@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "企业信息");
        model.addAttribute("formTitle", "新增企业信息");
        model.addAttribute("formAction", "/enterprises");
        model.addAttribute("enterpriseForm", new EnterpriseFormRequest());
        populateEnterpriseOptions(model);
        return AjaxRequestSupport.isAjax(requestedWith) ? "enterprise/form :: drawerContent" : "enterprise/form";
    }

    @GetMapping("/enterprises/{id}")
    public String enterpriseDetail(@PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "企业信息详情");
        model.addAttribute("record", enterpriseService.getById(id));
        return AjaxRequestSupport.isAjax(requestedWith) ? "enterprise/detail :: drawerContent" : "enterprise/detail";
    }

    @PostMapping("/enterprises")
    public String createEnterprise(@Valid @ModelAttribute("enterpriseForm") EnterpriseFormRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "businessLicenseFile", required = false) MultipartFile businessLicenseFile,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "企业信息");
            model.addAttribute("formTitle", "新增企业信息");
            model.addAttribute("formAction", "/enterprises");
            populateEnterpriseOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "enterprise/form :: drawerContent" : "enterprise/form";
        }
        try {
            request.setBusinessLicensePath(resolveBusinessLicensePath(
                    request.getBusinessLicensePath(), request.getEnterpriseName(), businessLicenseFile));
            enterpriseService.create(request, currentUser.getId());
        } catch (IllegalStateException ex) {
            model.addAttribute("pageTitle", "企业信息");
            model.addAttribute("formTitle", "新增企业信息");
            model.addAttribute("formAction", "/enterprises");
            model.addAttribute("formError", ex.getMessage());
            populateEnterpriseOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "enterprise/form :: drawerContent" : "enterprise/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "企业信息新增成功");
        return "redirect:/enterprises";
    }

    @GetMapping("/enterprises/{id}/edit")
    public String editEnterprise(@PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "企业信息");
        model.addAttribute("formTitle", "编辑企业信息");
        model.addAttribute("formAction", "/enterprises/" + id);
        model.addAttribute("enterpriseForm", toForm(enterpriseService.getById(id)));
        populateEnterpriseOptions(model);
        return AjaxRequestSupport.isAjax(requestedWith) ? "enterprise/form :: drawerContent" : "enterprise/form";
    }

    @PostMapping("/enterprises/{id}")
    public String updateEnterprise(@PathVariable Long id,
            @Valid @ModelAttribute("enterpriseForm") EnterpriseFormRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "businessLicenseFile", required = false) MultipartFile businessLicenseFile,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "企业信息");
            model.addAttribute("formTitle", "编辑企业信息");
            model.addAttribute("formAction", "/enterprises/" + id);
            populateEnterpriseOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "enterprise/form :: drawerContent" : "enterprise/form";
        }
        try {
            request.setBusinessLicensePath(resolveBusinessLicensePath(
                    request.getBusinessLicensePath(), request.getEnterpriseName(), businessLicenseFile));
            enterpriseService.update(id, request, currentUser.getId());
        } catch (IllegalStateException ex) {
            model.addAttribute("pageTitle", "企业信息");
            model.addAttribute("formTitle", "编辑企业信息");
            model.addAttribute("formAction", "/enterprises/" + id);
            model.addAttribute("formError", ex.getMessage());
            populateEnterpriseOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "enterprise/form :: drawerContent" : "enterprise/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "企业信息更新成功");
        return "redirect:/enterprises";
    }

    @GetMapping("/enterprises/{id}/business-license")
    public ResponseEntity<Resource> downloadBusinessLicense(@PathVariable Long id) {
        EnterpriseInfo enterprise = enterpriseService.getById(id);
        if (enterprise == null || enterprise.getBusinessLicensePath() == null || enterprise.getBusinessLicensePath().isBlank()) {
            throw new IllegalStateException("营业执照文件不存在");
        }
        Path basePath = Paths.get(enterpriseLicenseDir).toAbsolutePath().normalize();
        Path filePath = basePath.resolve(enterprise.getBusinessLicensePath()).normalize();
        if (!filePath.startsWith(basePath) || !Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new IllegalStateException("营业执照文件不存在");
        }
        try {
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if (contentType == null || contentType.isBlank()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            String encodedName = URLEncoder.encode(filePath.getFileName().toString(), StandardCharsets.UTF_8).replace("+", "%20");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"license\"; filename*=UTF-8''" + encodedName)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException ex) {
            throw new IllegalStateException("营业执照文件读取失败", ex);
        }
    }

    @PostMapping("/enterprises/{id}/delete")
    public String deleteEnterprise(@PathVariable Long id,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        enterpriseService.delete(id, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "企业信息删除成功");
        return "redirect:/enterprises";
    }

    @PostMapping("/enterprises/batch-delete")
    public String batchDeleteEnterprise(@RequestParam(value = "ids", required = false) List<Long> ids,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        int deletedCount = enterpriseService.deleteBatch(ids, currentUser.getId());
        if (deletedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", "已批量删除 " + deletedCount + " 条企业信息");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "请先选择需要删除的企业信息");
        }
        return "redirect:/enterprises";
    }

    private EnterpriseFormRequest toForm(EnterpriseInfo enterprise) {
        EnterpriseFormRequest form = new EnterpriseFormRequest();
        form.setId(enterprise.getId());
        form.setEnterpriseName(enterprise.getEnterpriseName());
        form.setIndustry(enterprise.getIndustry());
        form.setEnterpriseNature(enterprise.getEnterpriseNature());
        form.setEnterpriseScale(enterprise.getEnterpriseScale());
        form.setRegionProvinceCode(enterprise.getRegionProvinceCode());
        form.setRegionCityCode(enterprise.getRegionCityCode());
        form.setRegionCountyCode(enterprise.getRegionCountyCode());
        form.setUnifiedSocialCreditCode(enterprise.getUnifiedSocialCreditCode());
        form.setBusinessLicensePath(enterprise.getBusinessLicensePath());
        form.setAddress(enterprise.getAddress());
        form.setContactPerson(enterprise.getContactPerson());
        form.setContactPhone(enterprise.getContactPhone());
        form.setDescription(enterprise.getDescription());
        return form;
    }

    private void normalizePage(EnterpriseSearchRequest query, long totalCount) {
        int totalPages = (int) Math.max(1, (totalCount + query.getSafePageSize() - 1) / query.getSafePageSize());
        if (query.getSafePage() > totalPages) {
            query.setPage(totalPages);
        }
    }

    private void populateEnterpriseOptions(Model model) {
        model.addAttribute("industryOptions", dictionaryService.getByType("enterprise_industry"));
        model.addAttribute("natureOptions", dictionaryService.getByType("enterprise_nature"));
        model.addAttribute("scaleOptions", dictionaryService.getByType("enterprise_scale"));
    }

    private String resolveBusinessLicensePath(String currentPath, String enterpriseName, MultipartFile businessLicenseFile) {
        if (businessLicenseFile == null || businessLicenseFile.isEmpty()) {
            return currentPath;
        }
        String originalName = businessLicenseFile.getOriginalFilename();
        String baseName = "license";
        String suffix = "";
        if (originalName != null && !originalName.isBlank()) {
            int lastDot = originalName.lastIndexOf('.');
            if (lastDot >= 0) {
                if (lastDot > 0) {
                    baseName = originalName.substring(0, lastDot);
                }
                suffix = originalName.substring(lastDot);
            } else {
                baseName = originalName;
            }
        }
        String safeBaseName = sanitizeFileNamePart(baseName);
        String safeEnterpriseName = sanitizeFileNamePart(enterpriseName == null ? "enterprise" : enterpriseName);
        String timestamp = LocalDateTime.now().format(LICENSE_TIMESTAMP_FORMATTER);
        String targetFileName = safeBaseName + "_" + safeEnterpriseName + "_" + timestamp + suffix;
        Path basePath = Paths.get(enterpriseLicenseDir).toAbsolutePath().normalize();
        Path enterpriseDirPath = basePath.resolve(safeEnterpriseName).normalize();
        Path targetPath = enterpriseDirPath.resolve(targetFileName).normalize();
        if (!targetPath.startsWith(basePath)) {
            throw new IllegalStateException("营业执照文件名不合法");
        }
        try {
            Files.createDirectories(enterpriseDirPath);
            businessLicenseFile.transferTo(targetPath);
            return safeEnterpriseName + "/" + targetFileName;
        } catch (IOException ex) {
            throw new IllegalStateException("营业执照上传失败", ex);
        }
    }

    private String sanitizeFileNamePart(String value) {
        if (value == null || value.isBlank()) {
            return "file";
        }
        String sanitized = value.trim().replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
        return sanitized.isBlank() ? "file" : sanitized;
    }
}
