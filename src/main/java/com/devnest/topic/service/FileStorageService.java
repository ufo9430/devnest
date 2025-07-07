package com.devnest.topic.service;

import com.devnest.topic.domain.Topic;
import com.devnest.topic.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileRepository fileRepository;

    @Value("${file.upload-dir:uploads}") // application.yml/properties에 설정 가능
    private String uploadDir;

    @Value("${server.servlet.context-path:}") // 필요시 context-path 고려
    private String contextPath;

    public String save(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("빈 파일 업로드 불가");

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("업로드 파일 이름이 비어 있습니다.");
        }

        LocalDate today = LocalDate.now();
        String datePath = today.toString().replace("-", File.separator);
        Path uploadPath = Paths.get(uploadDir, datePath);

        try {
            Files.createDirectories(uploadPath);

            String ext = StringUtils.getFilenameExtension(originalFilename);
            String fileName = UUID.randomUUID().toString() + (ext != null ? "." + ext : "");
            Path filePath = uploadPath.resolve(fileName);

            file.transferTo(filePath);

            String urlPath = "/uploads/" + datePath.replace(File.separator, "/") + "/" + fileName;
            if (contextPath != null && !contextPath.isEmpty() && !contextPath.equals("/")) {
                urlPath = contextPath + urlPath;
            }
            return urlPath;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    public void saveFileInfo(MultipartFile file, String url) {
        // files 테이블에 insert (targetType/targetId는 null로 저장)
        com.devnest.topic.domain.File fileEntity = com.devnest.topic.domain.File.builder()
                .targetType(com.devnest.topic.domain.File.TargetType.TOPIC)  // 또는 TargetType.TOPIC 등, 상황에 맞게
                .targetId(null) // 질문/답변 등록 시 실제 값으로 update 필요
                .filename(file.getOriginalFilename())
                .url(url)
                .build();
        fileRepository.save(fileEntity);
    }
}