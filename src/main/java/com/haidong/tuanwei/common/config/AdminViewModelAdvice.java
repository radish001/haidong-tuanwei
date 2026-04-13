package com.haidong.tuanwei.common.config;

import com.haidong.tuanwei.system.entity.Menu;
import com.haidong.tuanwei.system.service.MenuService;
import com.haidong.tuanwei.common.security.AdminUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(annotations = Controller.class)
@Slf4j
@RequiredArgsConstructor
public class AdminViewModelAdvice {

    private static final Set<String> HIDDEN_SIDE_MENU_PATHS = Set.of(
            "/youth/college",
            "/youth/graduate",
            "/youth/rural",
            "/youth/entrepreneur");
    private static final Set<String> HIDDEN_TOP_LEVEL_MENU_NAMES = Set.of("数据分析");

    private final MenuService menuService;

    @Value("${app.upload.max-file-size:10MB}")
    private String uploadMaxFileSizeText;

    @ModelAttribute("currentUser")
    public AdminUserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AdminUserDetails principal)) {
            return null;
        }
        return principal;
    }

    @ModelAttribute("sideMenus")
    public List<Menu> sideMenus() {
        AdminUserDetails user = currentUser();
        if (user == null) {
            return List.of();
        }
        List<Menu> menus = menuService.getMenusByUserId(user.getId());
        menus.removeIf(menu -> HIDDEN_TOP_LEVEL_MENU_NAMES.contains(menu.getMenuName()));
        menus.forEach(menu -> menu.getChildren().removeIf(child -> HIDDEN_SIDE_MENU_PATHS.contains(child.getMenuPath())));
        return normalizeMenus(menus);
    }

    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String requestUri = request.getRequestURI();
        String[] segments = requestUri.split("/");
        if (segments.length == 4 && "youth".equals(segments[1]) && "analytics".equals(segments[3])) {
            return "/youth/" + segments[2];
        }
        return requestUri;
    }

    @ModelAttribute("uploadMaxFileSizeText")
    public String uploadMaxFileSizeText() {
        return uploadMaxFileSizeText;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxUploadSizeExceeded(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String requestUri = request == null ? "" : request.getRequestURI();
        String redirectPath = requestUri != null && requestUri.endsWith("/import")
                ? requestUri.substring(0, requestUri.length() - "/import".length())
                : "/";
        log.warn("Upload rejected because file exceeded limit: requestUri={}, maxFileSize={}", requestUri, uploadMaxFileSizeText);
        redirectAttributes.addFlashAttribute("importMessage",
                "上传文件不能超过 " + uploadMaxFileSizeText + "，请压缩或拆分后重试");
        return new ModelAndView("redirect:" + redirectPath);
    }

    private List<Menu> normalizeMenus(List<Menu> menus) {
        List<Menu> normalizedMenus = new ArrayList<>();
        Menu enterpriseMenu = null;
        Menu jobMenu = null;

        for (Menu menu : menus) {
            if ("/enterprises".equals(menu.getMenuPath())) {
                enterpriseMenu = menu;
                continue;
            }
            if ("/jobs".equals(menu.getMenuPath())) {
                jobMenu = menu;
                continue;
            }
            if ("/policies".equals(menu.getMenuPath())) {
                menu.setMenuName("就业创业政策");
            }
            normalizedMenus.add(menu);
        }

        if (enterpriseMenu != null || jobMenu != null) {
            Menu mergedMenu = new Menu();
            mergedMenu.setId(jobMenu != null ? jobMenu.getId() : enterpriseMenu.getId());
            mergedMenu.setParentId(0L);
            mergedMenu.setMenuName("企业招聘信息");
            mergedMenu.setMenuPath(jobMenu != null ? jobMenu.getMenuPath() : enterpriseMenu.getMenuPath());
            mergedMenu.setIcon(jobMenu != null ? jobMenu.getIcon() : enterpriseMenu.getIcon());
            mergedMenu.setSortNo(Math.min(
                    enterpriseMenu != null ? enterpriseMenu.getSortNo() : Integer.MAX_VALUE,
                    jobMenu != null ? jobMenu.getSortNo() : Integer.MAX_VALUE));
            mergedMenu.setVisible(Boolean.TRUE);

            if (jobMenu != null) {
                jobMenu.setMenuName("招聘信息");
                mergedMenu.getChildren().add(jobMenu);
            }
            if (enterpriseMenu != null) {
                enterpriseMenu.setMenuName("企业信息");
                mergedMenu.getChildren().add(enterpriseMenu);
            }
            normalizedMenus.add(mergedMenu);
        }

        normalizedMenus.sort((left, right) -> Integer.compare(
                left.getSortNo() == null ? Integer.MAX_VALUE : left.getSortNo(),
                right.getSortNo() == null ? Integer.MAX_VALUE : right.getSortNo()));
        return normalizedMenus;
    }
}
