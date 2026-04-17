package com.vv.wikihow.util;

import com.vv.wikihow.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件工具类
 */
@Slf4j
@Component
public class FileUtil {

    @Value("${upload.path}")
    private String uploadPath;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file) {
        // 验证文件
        validateFile(file);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + extension;

        try {
            // 获取绝对路径
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            
            // 确保目录存在
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建上传目录: {}", uploadDir);
            }

            // 保存文件
            Path destPath = uploadDir.resolve(newFilename);
            file.transferTo(destPath.toFile());
            log.info("文件上传成功: {}", destPath);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }

        return "/uploads/" + newFilename;
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("只支持 JPG、PNG、GIF、WebP 格式的图片");
        }

        // 验证文件大小
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("文件大小不能超过 5MB");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
