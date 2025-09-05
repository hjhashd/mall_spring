package com.coding24h.mall_spring.controller;

import com.coding24h.mall_spring.dto.ApiResponse;
import com.coding24h.mall_spring.service.impl.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/upload")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // 原有的聊天图片上传接口
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please supply a file to upload."));
        }

        try {
            String fileUrl = fileStorageService.storeFile(file, "chat");

            // 构建与前端约定好的返回格式
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // FileStorageService 内部会抛出自定义异常，这里可以统一捕获
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Could not store the file. Error: " + e.getMessage()));
        }
    }
}
