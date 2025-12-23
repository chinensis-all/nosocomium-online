package com.mayanshe.nosocomiumonline.shared.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
     * 格式化打印
     */
    public static void printFmt(String template, Object... objs) {
        if (IS_PROD) {
            return;
        }
        String message = template;
        for (Object obj : objs) {
            message = format(message, obj);
        }
        System.out.println(prefix() + message);
    }

    /**
     * 对象打印
     */
    public static void print(Object... objs) {
        if (IS_PROD) {
            return;
        }

        StringBuilder message = new StringBuilder();
        for (Object obj : objs) {
            message.append("\n--- ");
            String value;
            try {
                value = OBJECT_MAPPER.writeValueAsString(obj);
            } catch (Exception e) {
                value = String.valueOf(obj);
            }
            message.append(value);
        }
        System.out.println(prefix() + "\n" + message.toString().trim());
    }

    /**
     * 成功打印（绿色）
     */
    public static void successFmt(String template, Object... objs) {
        if (IS_PROD) {
            return;
        }
        String message = template;
        for (Object obj : objs) {
            message = format(message, obj);
        }
        System.out.println(prefix() + AnsiColor.green(message));
    }

    /**
     * 成功打印（绿色）
     */
    public static void successPrint(Object... objs) {
        if (IS_PROD) {
            return;
        }

        StringBuilder message = new StringBuilder();
        for (Object obj : objs) {
            message.append("\n--- ");
            String value;
            try {
                value = OBJECT_MAPPER.writeValueAsString(obj);
            } catch (Exception e) {
                value = String.valueOf(obj);
            }
            message.append(value);
        }
        System.out.println(prefix() + "\n" + AnsiColor.green(message.toString().trim()));
    }

    /**
     * 失败打印（红色）
     */
    public static void failedFmt(String template, Object... objs) {
        if (IS_PROD) {
            return;
        }
        String message = template;
        for (Object obj : objs) {
            message = format(message, obj);
        }
        System.out.println(prefix() + AnsiColor.red(message));
    }

    /**
     * 失败打印（红色）
     */
    public static void failedPrint(Object... objs) {
        if (IS_PROD) {
            return;
        }

        StringBuilder message = new StringBuilder();
        for (Object obj : objs) {
            message.append("\n--- ");
            String value;
            try {
                value = OBJECT_MAPPER.writeValueAsString(obj);
            } catch (Exception e) {
                value = String.valueOf(obj);
            }
            message.append(value);
        }
        System.out.println(prefix() + "\n" + AnsiColor.red(message.toString().trim()));
    }

    /**
     * 带耗时的打印（控制台彩色）
     */
    public static void performance(String template, Object obj, long startMillis) {
        if (IS_PROD) {
            return;
        }
        long cost = System.currentTimeMillis() - startMillis;
        String message = format(template, obj) + " | cost=" + cost + "ms" + " , threshold=" + COST_THRESHOLD_MS + "ms";

        if (cost > COST_THRESHOLD_MS) {
            System.out.println(prefix() + AnsiColor.red(message));
        } else {
            System.out.println(prefix() + AnsiColor.green(message));
        }
    }

    /**
     * 带自定义耗时阈值的打印
     */
    public static void performance(String template, Object obj, long startMillis, long limits) {
        if (IS_PROD) {
            return;
        }
        long cost = System.currentTimeMillis() - startMillis;
        String message = format(template, obj) + " | cost=" + cost + "ms" + " , limits=" + limits + "ms";

        if (cost > limits) {
            System.out.println(prefix() + AnsiColor.red(message));
        } else {
            System.out.println(prefix() + AnsiColor.green(message));
        }
    }

    private static String format(String template, Object obj) {
        String value;
        try {
            value = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            value = String.valueOf(obj);
        }

        int index = template.indexOf("{}");

        if (index == -1) {
            return template;
        }

        return template.substring(0, index) + value + template.substring(index + 2);
    }

    private static String prefix() {
        OffsetDateTime now = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return now.format(formatter) + AnsiColor.magenta("  采蘑菇的小姑娘 \u263A : ");
    }

    private static boolean isProdEnv() {
        String profile = System.getProperty("spring.profiles.active");
        if (profile == null || profile.isBlank()) {
            profile = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        return profile != null && profile.toLowerCase().contains("prod");
    }
}
