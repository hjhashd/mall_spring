package com.coding24h.mall_spring.util;

import com.fasterxml.jackson.core.JsonParseException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@MappedTypes(Map.class)
public class JsonMapTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    Map<String, Object> parameter,
                                    JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, mapper.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("Error converting Map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    /**
     * 修改后的解析方法，增加了对输入字符串的检查。
     */
    private Map<String, Object> parseJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        // 增加一个简单的检查，只有当字符串看起来像一个JSON对象时才进行解析
        String trimmedJson = json.trim();
        if (trimmedJson.startsWith("{") && trimmedJson.endsWith("}")) {
            try {
                return mapper.readValue(trimmedJson,
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                // 如果它看起来像JSON但解析失败，这是一个数据错误，我们抛出异常
                throw new RuntimeException("Error parsing a JSON-like string to Map: " + json, e);
            }
        }

        // 如果字符串不是一个JSON对象，我们不能将它转换为Map。
        // 返回null以防止程序因解析普通字符串（如 'web_123'）而崩溃。
        return null;
    }
}
