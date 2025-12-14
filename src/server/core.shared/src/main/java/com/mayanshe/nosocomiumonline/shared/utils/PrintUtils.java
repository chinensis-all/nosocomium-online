package com.mayanshe.nosocomiumonline.shared.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;

/**
 * PrintUtils: 控制台打印工具类
 */
public final class PrintUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final long COST_THRESHOLD_MS = 50;
    private static final boolean IS_PROD = isProdEnv();

    private PrintUtils() {
    }

    /**
     * 普通打印
     */
    public static void print(String template, Object obj) {
        if (IS_PROD) {
            return;
        }
        String message = format(template, obj);
        System.out.println(prefix() + message);
    }

    /**
     * 带耗时的打印（控制台彩色）
     */
    public static void print(String template, Object obj, long startMillis) {
        if (IS_PROD) {
            return;
        }
        long cost = System.currentTimeMillis() - startMillis;
        String message = format(template, obj) + " | cost=" + cost + "ms";

        if (cost > COST_THRESHOLD_MS) {
            System.out.println(AnsiColor.red(prefix() + message));
        } else {
            System.out.println(AnsiColor.green(prefix() + message));
        }
    }

    /**
     * 带自定义耗时阈值的打印
     */
    public static void print(String template, Object obj, long startMillis, long limits) {
        if (IS_PROD) {
            return;
        }
        long cost = System.currentTimeMillis() - startMillis;
        String message = format(template, obj) + " | cost=" + cost + "ms";

        if (cost > limits) {
            System.out.println(AnsiColor.red(prefix() + message));
        } else {
            System.out.println(AnsiColor.green(prefix() + message));
        }
    }

    private static String format(String template, Object obj) {
        String value;
        try {
            value = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            value = String.valueOf(obj);
        }
        return template.replace("{}", value);
    }

    private static String prefix() {
        return "[PrintUtils " + LocalDateTime.now() + "] ";
    }

    private static boolean isProdEnv() {
        String profile = System.getProperty("spring.profiles.active");
        if (profile == null || profile.isBlank()) {
            profile = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        return profile != null && profile.toLowerCase().contains("prod");
    }
}
