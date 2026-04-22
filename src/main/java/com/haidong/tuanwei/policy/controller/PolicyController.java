package com.haidong.tuanwei.policy.controller;

import com.haidong.tuanwei.common.security.AdminUserDetails;
import com.haidong.tuanwei.common.web.AjaxRequestSupport;
import com.haidong.tuanwei.common.web.PaginationSupport;
import com.haidong.tuanwei.policy.dto.PolicyFormRequest;
import com.haidong.tuanwei.policy.dto.PolicySearchRequest;
import com.haidong.tuanwei.policy.entity.PolicyArticle;
import com.haidong.tuanwei.policy.service.PolicyArticleService;
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
public class PolicyController {

    private final PolicyArticleService policyArticleService;

    @GetMapping("/policies")
    public String policies(@ModelAttribute("query") PolicySearchRequest query,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        long totalCount = policyArticleService.count(query);
        normalizePage(query, totalCount);

        model.addAttribute("pageTitle", "政策管理");
        model.addAttribute("records", policyArticleService.search(query));
        PaginationSupport.apply(model, query.getSafePage(), query.getSafePageSize(), totalCount);
        return AjaxRequestSupport.isAjax(requestedWith) ? "policy/list :: listContent" : "policy/list";
    }

    @GetMapping("/policies/new")
    public String newPolicy(@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "政策管理");
        model.addAttribute("formTitle", "新增政策文章");
        model.addAttribute("formAction", "/policies");
        model.addAttribute("policyForm", new PolicyFormRequest());
        return AjaxRequestSupport.isAjax(requestedWith) ? "policy/form :: drawerContent" : "policy/form";
    }

    @PostMapping("/policies")
    public String createPolicy(@Valid @ModelAttribute("policyForm") PolicyFormRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "政策管理");
            model.addAttribute("formTitle", "新增政策文章");
            model.addAttribute("formAction", "/policies");
            return AjaxRequestSupport.isAjax(requestedWith) ? "policy/form :: drawerContent" : "policy/form";
        }
        policyArticleService.create(request, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "政策文章新增成功");
        return "redirect:/policies";
    }

    @GetMapping("/policies/{id}")
    public String policyDetail(@PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "政策详情");
        model.addAttribute("record", policyArticleService.getById(id));
        return AjaxRequestSupport.isAjax(requestedWith) ? "policy/detail :: drawerContent" : "policy/detail";
    }

    @GetMapping("/policies/{id}/edit")
    public String editPolicy(@PathVariable Long id,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        model.addAttribute("pageTitle", "政策管理");
        model.addAttribute("formTitle", "编辑政策文章");
        model.addAttribute("formAction", "/policies/" + id);
        model.addAttribute("policyForm", toForm(policyArticleService.getById(id)));
        return AjaxRequestSupport.isAjax(requestedWith) ? "policy/form :: drawerContent" : "policy/form";
    }

    @PostMapping("/policies/{id}")
    public String updatePolicy(@PathVariable Long id,
            @Valid @ModelAttribute("policyForm") PolicyFormRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "政策管理");
            model.addAttribute("formTitle", "编辑政策文章");
            model.addAttribute("formAction", "/policies/" + id);
            return AjaxRequestSupport.isAjax(requestedWith) ? "policy/form :: drawerContent" : "policy/form";
        }
        policyArticleService.update(id, request, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "政策文章更新成功");
        return "redirect:/policies";
    }

    @PostMapping("/policies/{id}/status")
    public String updatePolicyStatus(@PathVariable Long id,
            Integer status,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        policyArticleService.updateStatus(id, status, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", status != null && status == 1 ? "政策已发布" : "政策已下线");
        return "redirect:/policies";
    }

    @PostMapping("/policies/{id}/delete")
    public String deletePolicy(@PathVariable Long id,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        policyArticleService.delete(id, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "政策文章删除成功");
        return "redirect:/policies";
    }

    @PostMapping("/policies/batch-delete")
    public String batchDeletePolicy(@RequestParam(value = "ids", required = false) List<Long> ids,
            @AuthenticationPrincipal AdminUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        int deletedCount = policyArticleService.deleteBatch(ids, currentUser.getId());
        if (deletedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", "已批量删除 " + deletedCount + " 条政策文章");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "请先选择需要删除的政策文章");
        }
        return "redirect:/policies";
    }

    private PolicyFormRequest toForm(PolicyArticle article) {
        PolicyFormRequest form = new PolicyFormRequest();
        form.setId(article.getId());
        form.setTitle(article.getTitle());
        form.setIssuingOrganization(article.getIssuingOrganization());
        form.setPolicySource(article.getPolicySource());
        form.setSummary(article.getSummary());
        form.setSortOrder(article.getSortOrder());
        form.setContentHtml(article.getContentHtml());
        return form;
    }

    private void normalizePage(PolicySearchRequest query, long totalCount) {
        int totalPages = (int) Math.max(1, (totalCount + query.getSafePageSize() - 1) / query.getSafePageSize());
        if (query.getSafePage() > totalPages) {
            query.setPage(totalPages);
        }
    }
}
