package com.coding24h.mall_spring.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义 TypeHandler，用于在逗号分隔的字符串和 List<String> 之间转换。
 */
@MappedTypes(List.class)
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

    private static final String SEPARATOR = ",";

    /**
     * 将 List<String> 转换为逗号分隔的字符串，用于写入数据库。
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        String result = parameter.stream().collect(Collectors.joining(SEPARATOR));
        ps.setString(i, result);
    }

    /**
     * 从数据库读取字符串并转换为 List<String> (通过列名)。
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String resultString = rs.getString(columnName);
        return stringToList(resultString);
    }

    /**
     * 从数据库读取字符串并转换为 List<String> (通过列索引)。
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String resultString = rs.getString(columnIndex);
        return stringToList(resultString);
    }

    /**
     * 从数据库读取字符串并转换为 List<String> (用于存储过程)。
     */
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String resultString = cs.getString(columnIndex);
        return stringToList(resultString);
    }

    /**
     * 核心转换逻辑：将字符串按逗号分割成列表。
     */
    private List<String> stringToList(String s) {
        if (s == null || s.trim().isEmpty()) {
            return Collections.emptyList(); // 返回空列表而不是 null
        }
        return Arrays.asList(s.split(SEPARATOR));
    }
}
