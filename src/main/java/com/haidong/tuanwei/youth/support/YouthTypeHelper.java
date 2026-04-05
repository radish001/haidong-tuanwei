package com.haidong.tuanwei.youth.support;

public final class YouthTypeHelper {

    private YouthTypeHelper() {
    }

    public static String code(String type) {
        return switch (type) {
            case "college" -> "COLLEGE";
            case "graduate" -> "GRADUATED_UNEMPLOYED";
            case "rural" -> "RURAL_COMMUNITY";
            case "entrepreneur" -> "ENTREPRENEUR";
            default -> "COLLEGE";
        };
    }

    public static String label(String type) {
        return switch (type) {
            case "college" -> "在校大学生";
            case "graduate" -> "毕业未就业";
            case "rural" -> "农村社区待业";
            case "entrepreneur" -> "创业青年";
            default -> "在校大学生";
        };
    }
}
