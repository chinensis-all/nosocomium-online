package com.mayanshe.nosocomiumonline.shared.utils;

/**
 * AnsiColor: ANSI 颜色代码工具类
 */
public final class AnsiColor {

    private AnsiColor() {
    }

    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";

    public static String green(String text) {
        return GREEN + text + RESET;
    }

    public static String red(String text) {
        return RED + text + RESET;
    }
}
