package com.mayanshe.nosocomiumonline.shared.util;

/**
 * AnsiColor: ANSI 颜色代码工具类
 */
public final class AnsiColor {

    private AnsiColor() {
    }

    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";

    public static final String YELLOW = "\u001B[33m";

    public static final String BLUE = "\u001B[34m";

    public static final String CYAN = "\u001B[36m";

    public static final String WHITE = "\u001B[37m";

    public static final String BLACK = "\u001B[30m";

    public static final String MAGENTA = "\u001B[35m";

    public static String green(String text) {
        return GREEN + text + RESET;
    }

    public static String red(String text) {
        return RED + text + RESET;
    }

    public static String yellow(String text) {
        return YELLOW + text + RESET;
    }

    public static String blue(String text) {
        return BLUE + text + RESET;
    }

    public static String cyan(String text) {
        return CYAN + text + RESET;
    }

    public static String white(String text) {
        return WHITE + text + RESET;
    }

    public static String black(String text) {
        return BLACK + text + RESET;
    }

    public static String magenta(String text) {
        return MAGENTA + text + RESET;
    }
}
