package com.coding24h.mall_spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileWebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保路径以斜杠结尾
        String path = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        path = "file:" + path.replace("\\", "/"); // 统一使用正斜杠

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(path);
    }
}
