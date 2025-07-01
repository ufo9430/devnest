package com.devnest.topic.controller;

import com.devnest.topic.domain.File;
import com.devnest.topic.domain.File.TargetType;
import com.devnest.topic.dto.FileDto;
import com.devnest.topic.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<List<FileDto>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam TargetType targetType,
            @RequestParam Long targetId) {

        List<File> storedFiles = fileStorageService.storeFiles(files, targetType, targetId);
        List<FileDto> fileDtos = storedFiles.stream()
                .map(FileDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(fileDtos);
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<List<FileDto>> getFiles(
            @PathVariable TargetType targetType,
            @PathVariable Long targetId) {

        List<File> files = fileStorageService.getFiles(targetType, targetId);
        List<FileDto> fileDtos = files.stream()
                .map(FileDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(fileDtos);
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}