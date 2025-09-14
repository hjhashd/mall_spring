package com.coding24h.mall_spring.util;

import java.util.regex.Pattern;

/**
 * 密码强度校验工具类
 * 规则：
 * - 弱：长度<8位 / 字符类型<2种 / 含连续/重复字符
 * - 中：8≤长度<12位 且 字符类型=2种
 * - 强：12≤长度<16位 且 字符类型≥3种
 * - 极强：长度≥16位 且 字符类型=4种（大写+小写+数字+符号）
 */
public class PasswordValidator {

    // 正则表达式：匹配不同字符类型
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");
    // 特殊符号（可根据业务扩展，如增加_、-等）
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
    // 连续字符（如1234、abcd）
    private static final Pattern CONTINUOUS_PATTERN = Pattern.compile(
            "0123|1234|2345|3456|4567|5678|6789|7890|" +
                    "abcde|bcdef|cdefg|defgh|efghi|fghij|ghijk|hijkl|ijklm|jklmn|" +
                    "klmno|mnopq|nopqr|opqrs|pqrst|qrstu|rstuv|stuvw|tuvwx|uvwxy|vwxyz",
            Pattern.CASE_INSENSITIVE // 不区分大小写
    );
    // 5个及以上重复字符（如aaaaa、11111）
    private static final Pattern REPEATED_PATTERN = Pattern.compile("(.)\\1{4,}");


    /**
     * 校验密码强度
     *
     * @param password 待校验的密码
     * @return 校验结果（包含是否通过、强度等级、提示信息）
     */
    public static ValidationResult checkStrength(String password) {
        // 1. 空值校验
        if (password == null || password.trim().isEmpty()) {
            return new ValidationResult(false, 4);
        }
        String trimmedPwd = password.trim();

        // 2. 检查连续/重复字符（直接判定为弱密码）
        if (CONTINUOUS_PATTERN.matcher(trimmedPwd).find() || REPEATED_PATTERN.matcher(trimmedPwd).find()) {
            return new ValidationResult(false, 4);
        }

        // 3. 统计字符类型数量（0-4种）
        int charTypeCount = 0;
        if (LOWERCASE_PATTERN.matcher(trimmedPwd).find()) charTypeCount++;
        if (UPPERCASE_PATTERN.matcher(trimmedPwd).find()) charTypeCount++;
        if (NUMBER_PATTERN.matcher(trimmedPwd).find()) charTypeCount++;
        if (SYMBOL_PATTERN.matcher(trimmedPwd).find()) charTypeCount++;

        // 4. 按「长度+字符类型」判定强度
        int length = trimmedPwd.length();
        if (length < 8 || charTypeCount < 2) {
            return new ValidationResult(false, 4);
        } else if (length < 12 && charTypeCount == 2) {
            return new ValidationResult(true, 3);
        } else if (length < 16 && charTypeCount >= 3) {
            return new ValidationResult(true, 2);
        } else if (length >= 16 && charTypeCount == 4) {
            return new ValidationResult(true, 1);
        } else {
            return new ValidationResult(true, 3);
        }
    }


    /**
     * 校验结果封装类
     * 用于返回清晰的校验信息给调用方
     */
    public static class ValidationResult {
        private boolean isValid;       // 是否通过校验（弱密码为false，中/强/极强为true）方便以后扩展暂时不需要理会
        private int strengthLevel;  // 强度等级（弱/中/强/极强）

        public ValidationResult(boolean isValid, int strengthLevel) {
            this.isValid = isValid;
            this.strengthLevel = strengthLevel;
        }

        // Getter（无Setter，确保结果不可篡改）
        public boolean isValid() {
            return isValid;
        }

        public int getStrengthLevel() {
            return strengthLevel;
        }
    }
}