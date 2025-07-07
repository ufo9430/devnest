package com.devnest.topic.controller;

import com.devnest.topic.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 파일 저장
        String imageUrl = fileStorageService.save(file);

        // files 테이블에 insert
        fileStorageService.saveFileInfo(file, imageUrl);

        return Collections.singletonMap("url", imageUrl);
    }
}