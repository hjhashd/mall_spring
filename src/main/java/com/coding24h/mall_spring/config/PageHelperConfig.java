package com.coding24h.mall_spring.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * PageHelper分页插件配置
 */
@Configuration
public class PageHelperConfig {

    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();

        // 设置数据库方言
        properties.setProperty("helperDialect", "mysql");

        // 分页参数合理化，默认false禁用
        // 启用合理化时，如果pageNum<1会查询第一页，如果pageNum>pages会查询最后一页
        properties.setProperty("reasonable", "true");

        // 支持通过Mapper接口参数来传递分页参数，默认false
        properties.setProperty("supportMethodsArguments", "true");

        // 分页插件会自动检测当前的数据库链接，自动选择合适的分页方式
        properties.setProperty("autoRuntimeDialect", "true");

        // 设置为true时，会将RowBounds第一个参数offset当成pageNum页码使用
        properties.setProperty("offsetAsPageNum", "true");

        // 设置为true时，使用RowBounds分页会进行count查询
        properties.setProperty("rowBoundsWithCount", "true");

        // 设置为true时，如果pageSize=0或者RowBounds.limit = 0就会查询出全部的结果
        properties.setProperty("pageSizeZero", "true");

        pageHelper.setProperties(properties);
        return pageHelper;
    }
}
