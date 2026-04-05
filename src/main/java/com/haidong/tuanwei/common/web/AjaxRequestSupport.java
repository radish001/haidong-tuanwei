package com.haidong.tuanwei.common.web;

public final class AjaxRequestSupport {

    private AjaxRequestSupport() {
    }

    public static boolean isAjax(String requestedWith) {
        return "XMLHttpRequest".equalsIgnoreCase(requestedWith);
    }
}
