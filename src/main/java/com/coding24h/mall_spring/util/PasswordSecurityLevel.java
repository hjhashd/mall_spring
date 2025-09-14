package com.coding24h.mall_spring.entity.myEnum;

public enum PasswordSecurityLevel {
    // 定义枚举常量，分别对应1-4级及描述
    EXTREMELY_STRONG(1, "极强"),
    STRONG(2, "强"),
    MEDIUM(3, "中"),
    WEAK(4, "弱");

    // 级别数值
    private final int level;
    // 描述字符串
    private final String description;

    // 构造方法
    PasswordSecurityLevel(int level, String description) {
        this.level = level;
        this.description = description;
    }

    // 获取级别数值
    public int getLevel() {
        return level;
    }

    // 获取描述字符串
    public String getDescription() {
        return description;
    }

    // 根据级别获取对应的描述
    public static String getDescriptionByLevel(int level) {
        for (PasswordSecurityLevel securityLevel : values()) {
            if (securityLevel.level == level) {
                return securityLevel.description;
            }
        }
        throw new IllegalArgumentException("无效的密码安全级别: " + level + "，必须是1-4之间的整数");
    }
}
    