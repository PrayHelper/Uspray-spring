package com.uspray.uspray.global.util;


public class MaskingUtil {
    public static String maskName(String name) {
        if (name == null || name.length() < 2) {
            return name;
        }
        return name.substring(0, 1) + "*" + name.substring(name.length() - 1);
    }

    public static String maskUserId(String username) {
        if (username == null || username.length() <= 3) {
            return username;
        }
        // 앞의 3개는 살리고, 나머지는 다 마스킹
        return username.substring(0, 3) + "*".repeat(username.length() - 3);
    }
}
