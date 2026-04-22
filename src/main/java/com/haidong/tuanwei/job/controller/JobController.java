package com.haidong.tuanwei.job.controller;

import com.haidong.tuanwei.common.security.AdminUserDetails;
import com.haidong.tuanwei.common.web.AjaxRequestSupport;
import com.haidong.tuanwei.common.web.PaginationSupport;
import com.haidong.tuanwei.enterprise.service.EnterpriseService;
import com.haidong.tuanwei.job.dto.JobFormRequest;
import com.haidong.tuanwei.job.dto.JobSearchRequest;
import com.haidong.tuanwei.job.entity.JobPost;
import com.haidong.tuanwei.job.service.JobPostService;
import com.haidong.tuanwei.job.support.JobRequirementOptionSupport;
import com.haidong.tuanwei.system.service.DictionaryService;
import com.haidong.tuanwei.system.service.MasterDataService;
import com.haidong.tuanwei.youth.dto.YouthSearchRequest;
import com.haidong.tuanwei.youth.service.YouthInfoService;
import com.haidong.tuanwei.youth.support.YouthTypeHelper;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
public class JobController {

    private final JobPostService jobPostService;
    private final EnterpriseService enterpriseService;
    private final DictionaryService dictionaryService;
    private final MasterDataService masterDataService;
    private final YouthInfoService youthInfoService;

    @GetMapping("/jobs")
    public String jobs(@ModelAttribute("query") JobSearchRequest query,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        long totalCount = jobPostService.count(query);
        normalizePage(query, totalCount);

        model.addAttribute("pageTitle", "招聘信息");
        model.addAttribute("records", jobPostService.search(query));
        PaginationSupport.apply(model, query.getSafePage(), query.getSafePageSize(), totalCount);
        populateJobOptions(model);
        return AjaxRequestSupport.isAjax(requestedWith) ? "job/list :: listContent" : "job/list";
    }

    @GetMapping("/jobs/new")
    public String newJob(@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "招聘信息");
        model.addAttribute("formTitle", "新增招聘岗位");
        model.addAttribute("formAction", "/jobs");
        model.addAttribute("jobForm", new JobFormRequest());
        populateJobOptions(model);
        return AjaxRequestSupport.isAjax(requestedWith) ? "job/form :: drawerContent" : "job/form";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        JobPost record = jobPostService.getById(id);
        model.addAttribute("pageTitle", "招聘信息详情");
        model.addAttribute("record", record);
        model.addAttribute("experienceRequirementLabel",
                resolveDictLabel("experience_requirement", record == null ? null : record.getExperienceRequirement()));
        model.addAttribute("salaryRangeLabel",
                resolveDictLabel("salary_range", record == null ? null : record.getSalaryRange()));
        return AjaxRequestSupport.isAjax(requestedWith) ? "job/detail :: drawerContent" : "job/detail";
    }

    @PostMapping("/jobs")
    public String createJob(@Valid @ModelAttribute("jobForm") JobFormRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "招聘信息");
            model.addAttribute("formTitle", "新增招聘岗位");
            model.addAttribute("formAction", "/jobs");
            populateJobOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "job/form :: drawerContent" : "job/form";
        }
        try {
            jobPostService.create(request, currentUser.getId());
        } catch (IllegalStateException ex) {
            log.warn("Job post create failed: operatorId={}, reason={}",
                    currentUser == null ? null : currentUser.getId(), ex.getMessage());
            model.addAttribute("pageTitle", "招聘信息");
            model.addAttribute("formTitle", "新增招聘岗位");
            model.addAttribute("formAction", "/jobs");
            model.addAttribute("formError", ex.getMessage());
            populateJobOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "job/form :: drawerContent" : "job/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "招聘岗位新增成功");
        return "redirect:/jobs";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJob(@PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "招聘信息");
        model.addAttribute("formTitle", "编辑招聘岗位");
        model.addAttribute("formAction", "/jobs/" + id);
        model.addAttribute("jobForm", toForm(jobPostService.getById(id)));
        populateJobOptions(model);
        return AjaxRequestSupport.isAjax(requestedWith) ? "job/form :: drawerContent" : "job/form";
    }

    @PostMapping("/jobs/{id}")
    public String updateJob(@PathVariable Long id,
            @Valid @ModelAttribute("jobForm") JobFormRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "招聘信息");
            model.addAttribute("formTitle", "编辑招聘岗位");
            model.addAttribute("formAction", "/jobs/" + id);
            populateJobOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "job/form :: drawerContent" : "job/form";
        }
        try {
            jobPostService.update(id, request, currentUser.getId());
        } catch (IllegalStateException ex) {
            log.warn("Job post update failed: id={}, operatorId={}, reason={}",
                    id, currentUser == null ? null : currentUser.getId(), ex.getMessage());
            model.addAttribute("pageTitle", "招聘信息");
            model.addAttribute("formTitle", "编辑招聘岗位");
            model.addAttribute("formAction", "/jobs/" + id);
            model.addAttribute("formError", ex.getMessage());
            populateJobOptions(model);
            return AjaxRequestSupport.isAjax(requestedWith) ? "job/form :: drawerContent" : "job/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "招聘岗位更新成功");
        return "redirect:/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        jobPostService.delete(id, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "招聘岗位删除成功");
        return "redirect:/jobs";
    }

    @PostMapping("/jobs/batch-delete")
    public String batchDeleteJob(@RequestParam(value = "ids", required = false) List<Long> ids,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        int deletedCount = jobPostService.deleteBatch(ids, currentUser.getId());
        if (deletedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", "已批量删除 " + deletedCount + " 条招聘信息");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "请先选择需要删除的招聘信息");
        }
        return "redirect:/jobs";
    }

    @GetMapping("/jobs/{id}/matches")
    public String matchStudents(@PathVariable Long id,
            @ModelAttribute("query") YouthSearchRequest query,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        JobPost jobPost = jobPostService.getById(id);
        if (jobPost == null) {
            throw new IllegalStateException("招聘岗位不存在");
        }
        applyMatchConditions(query, jobPost);

        long totalCount = youthInfoService.count(YouthTypeHelper.code("college"), query);
        normalizeYouthPage(query, totalCount);

        model.addAttribute("pageTitle", "岗位匹配学生");
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("salaryRangeLabel",
                resolveDictLabel("salary_range", jobPost.getSalaryRange()));
        model.addAttribute("records", youthInfoService.search(YouthTypeHelper.code("college"), query));
        PaginationSupport.apply(model, query.getSafePage(), query.getSafePageSize(), totalCount);
        applyCompactMatchPagination(model, query.getSafePage(), query.getSafePageSize(), totalCount);
        return AjaxRequestSupport.isAjax(requestedWith) ? "job/match-results :: drawerContent" : "job/match-results";
    }

    private JobFormRequest toForm(JobPost jobPost) {
        JobFormRequest form = new JobFormRequest();
        form.setId(jobPost.getId());
        form.setEnterpriseId(jobPost.getEnterpriseId());
        form.setJobName(jobPost.getJobName());
        form.setJobCategory(jobPost.getJobCategory());
        form.setEducationRequirements(JobRequirementOptionSupport.toFormSelections(jobPost.getEducationRequirements()));
        form.setMajorCodes(JobRequirementOptionSupport.toFormSelections(jobPost.getMajorCodes()));
        form.setSchoolCategoryIds(jobPost.getSchoolCategoryIds());
        form.setSchoolTagIds(jobPost.getSchoolTagIds());
        form.setExperienceRequirement(jobPost.getExperienceRequirement());
        form.setSalaryRange(jobPost.getSalaryRange());
        form.setRecruitCount(jobPost.getRecruitCount());
        form.setWorkProvinceCode(jobPost.getWorkProvinceCode());
        form.setWorkCityCode(jobPost.getWorkCityCode());
        form.setWorkCountyCode(jobPost.getWorkCountyCode());
        form.setContactPerson(jobPost.getContactPerson());
        form.setContactPhone(jobPost.getContactPhone());
        form.setJobDescription(jobPost.getJobDescription());
        form.setSortOrder(jobPost.getSortOrder());
        return form;
    }

    private void populateJobOptions(Model model) {
        model.addAttribute("enterprises", enterpriseService.getAllEnabled());
        model.addAttribute("educationOptions", dictionaryService.getByType("education_level"));
        model.addAttribute("majorOptions", masterDataService.getAllMajors());
        model.addAttribute("schoolCategoryOptions", dictionaryService.getByType("school_category"));
        model.addAttribute("schoolTagOptions", masterDataService.getAllSchoolTags());
        model.addAttribute("experienceOptions", dictionaryService.getByType("experience_requirement"));
        model.addAttribute("salaryOptions", dictionaryService.getByType("salary_range"));
        model.addAttribute("unlimitedOptionValue", JobRequirementOptionSupport.UNLIMITED_OPTION_VALUE);
    }

    private void normalizePage(JobSearchRequest query, long totalCount) {
        int totalPages = (int) Math.max(1, (totalCount + query.getSafePageSize() - 1) / query.getSafePageSize());
        if (query.getSafePage() > totalPages) {
            query.setPage(totalPages);
        }
    }

    private void normalizeYouthPage(YouthSearchRequest query, long totalCount) {
        int totalPages = (int) Math.max(1, (totalCount + query.getSafePageSize() - 1) / query.getSafePageSize());
        if (query.getSafePage() > totalPages) {
            query.setPage(totalPages);
        }
    }

    private void applyMatchConditions(YouthSearchRequest query, JobPost jobPost) {
        query.setMajorCodes(jobPost.getMajorCodes());
        query.setEducationCodes(jobPost.getEducationRequirements());
        query.setSchoolCategoryIds(jobPost.getSchoolCategoryIds());
        query.setSchoolTagIds(List.of());
    }

    private void applyCompactMatchPagination(Model model, int currentPage, int pageSize, long totalCount) {
        int safePageSize = Math.max(pageSize, 1);
        int totalPages = (int) Math.max(1, (totalCount + safePageSize - 1) / safePageSize);
        int safeCurrentPage = Math.min(Math.max(currentPage, 1), totalPages);
        int startPage = Math.max(1, safeCurrentPage - 1);
        int endPage = Math.min(totalPages, startPage + 2);
        startPage = Math.max(1, endPage - 2);

        List<Integer> pageNumbers = new ArrayList<>();
        for (int page = startPage; page <= endPage; page++) {
            pageNumbers.add(page);
        }

        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("showFirstPage", false);
        model.addAttribute("showLeadingEllipsis", startPage > 1);
        model.addAttribute("showLastPage", false);
        model.addAttribute("showTrailingEllipsis", endPage < totalPages);
    }

    private String resolveDictLabel(String dictType, String dictValue) {
        if (dictValue == null || dictValue.isBlank()) {
            return "";
        }
        return dictionaryService.getByType(dictType).stream()
                .filter(item -> dictValue.equals(item.getDictValue()))
                .findFirst()
                .map(item -> item.getDictLabel())
                .orElse(dictValue);
    }
}
