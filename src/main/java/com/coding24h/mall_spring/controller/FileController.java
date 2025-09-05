package com.coding24h.mall_spring.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    // 文件上传目录
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * 提供头像文件访问
     */
    @GetMapping("/uploads/avatar/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        try {
            // 构建文件路径
            Path filePath = Paths.get(uploadDir, "avatar", filename);

            // 检查文件是否存在
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // 创建资源
            Resource resource = new UrlResource(filePath.toUri());

            // 确定内容类型
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 提供通用文件访问
     */
    @GetMapping("/uploads/**")
    public ResponseEntity<Resource> getFile(HttpServletRequest request) {
        try {
            String path = request.getRequestURI().substring("/uploads/".length());
            Path filePath = Paths.get(uploadDir, path);

            if (Files.exists(filePath)) {
                Resource resource = new UrlResource(filePath.toUri());
                String contentType = Files.probeContentType(filePath);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(
                                contentType != null ? contentType : "application/octet-stream"))
                        .body(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }
}
