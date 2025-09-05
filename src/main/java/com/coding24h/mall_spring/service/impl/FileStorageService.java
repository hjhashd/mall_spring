package com.coding24h.mall_spring.service.impl;

import com.coding24h.mall_spring.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Service
public class FileStorageService {

    // 从配置文件中获取上传目录，默认为项目根目录下的 uploads
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/uploads}")
    private String baseUrl;
    /**
     * 存储上传的文件并返回相对路径
     *
     * @param file 上传的文件
     * @param subdirectory 存储的子目录(如 licenses, tax_certs)
     * @return 文件的相对路径(如 licenses/f3d5e7a1-9c8b-4a2d-b102-6c0e8f3a1b5c.jpg)
     */
    public String storeFile(MultipartFile file, String subdirectory) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("上传文件为空");
        }

        try {
            // 验证文件类型和大小（保持原有逻辑）
            String contentType = file.getContentType();
            if (contentType == null || !isValidFileType(contentType)) {
                throw new FileStorageException("不支持的文件类型: " + contentType);
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                throw new FileStorageException("文件大小超过5MB限制");
            }

            // 创建存储目录
            Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path storagePath = basePath.resolve(subdirectory);
            Files.createDirectories(storagePath);

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueName = UUID.randomUUID().toString() + extension;

            // 存储文件
            Path targetLocation = storagePath.resolve(uniqueName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 返回完整URL路径
            return baseUrl + "/" + subdirectory + "/" + uniqueName;

        } catch (IOException ex) {
            throw new FileStorageException("文件存储失败: " + ex.getMessage(), ex);
        }
    }

    /**
     * 验证文件类型是否符合要求
     *
     * @param contentType 文件MIME类型
     * @return 是否有效
     */
    private boolean isValidFileType(String contentType) {
        return Arrays.asList("image/jpeg", "image/png", "image/gif", "application/pdf")
                .contains(contentType.toLowerCase());
    }

    /**
     * 获取存储的基础路径（用于日志或其他操作）
     */
    public String getBaseStoragePath() {
        return Paths.get(uploadDir).toAbsolutePath().toString();
    }

    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return;

        try {
            // 构建完整路径（相对于上传目录）
            Path filePath = Paths.get(uploadDir).resolve(relativePath);

            // 标准化路径防止目录遍历攻击
            Path normalizedPath = filePath.normalize();
            Path basePath = Paths.get(uploadDir).normalize().toAbsolutePath();

            // 安全验证：确保路径在基础目录内
            if (!normalizedPath.startsWith(basePath)) {
                throw new SecurityException("试图访问基础目录之外的文件: " + normalizedPath);
            }

            if (Files.exists(normalizedPath)) {
                Files.delete(normalizedPath);
            }
        } catch (IOException | SecurityException ex) {
            throw new FileStorageException("文件删除失败: " + relativePath, ex);
        }
    }

    /**
     * 从完整URL中提取相对路径
     */
    public String extractRelativePath(String fullUrl) {
        if (fullUrl == null || fullUrl.isEmpty()) return "";

        // 移除 baseUrl 部分
        String relativePath = fullUrl.replaceFirst(baseUrl, "");

        // 移除开头的斜杠
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        return relativePath;
    }

    /**
     * 根据完整URL删除文件
     */
    /**
     * 根据完整URL删除文件（适配相对路径配置）
     */
    public void deleteFileByUrl(String fullUrl) {
        if (fullUrl == null || fullUrl.isEmpty()) return;

        try {
            // 提取文件相对路径（移除baseUrl部分）
            String relativePath = fullUrl.replaceFirst(baseUrl, "");

            // 移除开头的斜杠
            if (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
            }

            // 构建实际文件路径（适配相对路径配置）
            Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = basePath.resolve(relativePath);

            // 标准化路径防止目录遍历攻击
            Path normalizedPath = filePath.normalize();

            // 安全验证：确保路径在基础目录内
            if (!normalizedPath.startsWith(basePath)) {
                throw new SecurityException("试图访问基础目录之外的文件: " + normalizedPath);
            }

            // 转换为File对象（解决Windows路径问题）
            File file = normalizedPath.toFile();

            // 调试日志
            System.out.println("尝试删除文件: " + file.getAbsolutePath());

            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("成功删除文件: " + file.getAbsolutePath());
                } else {
                    throw new IOException("文件删除失败: " + file.getAbsolutePath());
                }
            }
        } catch (Exception ex) {
            throw new FileStorageException("文件删除失败: " + fullUrl, ex);
        }
    }
}
