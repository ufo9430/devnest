package com.devnest.topic.service;

import com.devnest.topic.domain.File;
import com.devnest.topic.domain.File.TargetType;
import com.devnest.topic.repository.FileRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private final FileRepository fileRepository;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉터리를 만들 수 없습니다.", e);
        }
    }

    public List<File> storeFiles(List<MultipartFile> files, TargetType targetType, Long targetId) {
        List<File> storedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                storedFiles.add(storeFile(file, targetType, targetId));
            }
        }
        return storedFiles;
    }

    public File storeFile(MultipartFile file, TargetType targetType, Long targetId) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String storedFilename = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = Paths.get(uploadDir).resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            File fileEntity = File.builder()
                    .targetType(targetType)
                    .targetId(targetId)
                    .filename(originalFilename)
                    .url("/uploads/" + storedFilename)
                    .build();

            return fileRepository.save(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("파일을 저장 할 수 없습니다. " + originalFilename, e);
        }
    }

    public void deleteFiles(TargetType targetType, Long targetId) {
        fileRepository.deleteByTargetTypeAndTargetId(targetType, targetId);
    }

    public List<File> getFiles(TargetType targetType, Long targetId) {
        return fileRepository.findByTargetTypeAndTargetIdOrderByUploadedAtDesc(targetType, targetId);
    }
}