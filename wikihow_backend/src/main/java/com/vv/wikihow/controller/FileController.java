package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUtil fileUtil;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String url = fileUtil.uploadFile(file);
        return Result.success(Map.of("url", url));
    }
}
