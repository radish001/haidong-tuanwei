package com.haidong.tuanwei.job.controller;

import com.haidong.tuanwei.common.security.AdminUserDetails;
import com.haidong.tuanwei.common.web.AjaxRequestSupport;
import com.haidong.tuanwei.common.web.PaginationSupport;
import com.haidong.tuanwei.enterprise.service.EnterpriseService;
import com.haidong.tuanwei.job.dto.JobFormRequest;
import com.haidong.tuanwei.job.dto.JobSearchRequest;
import com.haidong.tuanwei.job.entity.JobPost;
import com.haidong.tuanwei.job.service.JobPostService;
import com.haidong.tuanwei.system.service.DictionaryService;
import com.haidong.tuanwei.system.service.RegionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JobController {

    private final JobPostService jobPostService;
    private final EnterpriseService enterpriseService;
    private final DictionaryService dictionaryService;
    private final RegionService regionService;

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
        jobPostService.create(request, currentUser.getId());
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
        jobPostService.update(id, request, currentUser.getId());
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

    private JobFormRequest toForm(JobPost jobPost) {
        JobFormRequest form = new JobFormRequest();
        form.setId(jobPost.getId());
        form.setEnterpriseId(jobPost.getEnterpriseId());
        form.setJobName(jobPost.getJobName());
        form.setJobCategory(jobPost.getJobCategory());
        form.setEducationRequirement(jobPost.getEducationRequirement());
        form.setExperienceRequirement(jobPost.getExperienceRequirement());
        form.setSalaryRange(jobPost.getSalaryRange());
        form.setRecruitCount(jobPost.getRecruitCount());
        form.setWorkProvinceCode(jobPost.getWorkProvinceCode());
        form.setWorkCityCode(jobPost.getWorkCityCode());
        form.setWorkCountyCode(jobPost.getWorkCountyCode());
        form.setContactPerson(jobPost.getContactPerson());
        form.setContactPhone(jobPost.getContactPhone());
        form.setJobDescription(jobPost.getJobDescription());
        return form;
    }

    private void populateJobOptions(Model model) {
        model.addAttribute("enterprises", enterpriseService.getAllEnabled());
        model.addAttribute("regions", regionService.getRegionTree());
        model.addAttribute("educationOptions", dictionaryService.getByType("education_level"));
        model.addAttribute("experienceOptions", dictionaryService.getByType("experience_requirement"));
        model.addAttribute("salaryOptions", dictionaryService.getByType("salary_range"));
    }

    private void normalizePage(JobSearchRequest query, long totalCount) {
        int totalPages = (int) Math.max(1, (totalCount + query.getSafePageSize() - 1) / query.getSafePageSize());
        if (query.getSafePage() > totalPages) {
            query.setPage(totalPages);
        }
    }
}
