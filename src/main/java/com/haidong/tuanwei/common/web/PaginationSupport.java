package com.haidong.tuanwei.common.web;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ui.Model;

public final class PaginationSupport {

    private PaginationSupport() {
    }

    public static void apply(Model model, int currentPage, int pageSize, long totalCount) {
        int safePageSize = Math.max(pageSize, 1);
        int totalPages = (int) Math.max(1, (totalCount + safePageSize - 1) / safePageSize);
        int safeCurrentPage = Math.min(Math.max(currentPage, 1), totalPages);
        int startPage = Math.max(1, safeCurrentPage - 2);
        int endPage = Math.min(totalPages, startPage + 4);
        startPage = Math.max(1, endPage - 4);
        List<Integer> pageNumbers = new ArrayList<>();
        for (int page = startPage; page <= endPage; page++) {
            pageNumbers.add(page);
        }

        model.addAttribute("currentPage", safeCurrentPage);
        model.addAttribute("pageSize", safePageSize);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasPrevious", safeCurrentPage > 1);
        model.addAttribute("hasNext", safeCurrentPage < totalPages);
        model.addAttribute("previousPage", Math.max(1, safeCurrentPage - 1));
        model.addAttribute("nextPage", Math.min(totalPages, safeCurrentPage + 1));
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("showFirstPage", startPage > 1);
        model.addAttribute("showLeadingEllipsis", startPage > 2);
        model.addAttribute("showLastPage", endPage < totalPages);
        model.addAttribute("showTrailingEllipsis", endPage < totalPages - 1);
        model.addAttribute("firstPage", 1);
        model.addAttribute("lastPage", totalPages);
    }
}
