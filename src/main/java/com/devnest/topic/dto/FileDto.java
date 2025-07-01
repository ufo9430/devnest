package com.devnest.topic.dto;

import com.devnest.topic.domain.File;
import com.devnest.topic.domain.File.TargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class FileDto {
    private Long id;
    private TargetType targetType;
    private Long targetId;
    private String filename;
    private String url;
    private String uploadedAt;

    public FileDto(File file) {
        this.id = file.getId();
        this.targetType = file.getTargetType();
        this.targetId = file.getTargetId();
        this.filename = file.getFilename();
        this.url = file.getUrl();
        this.uploadedAt = file.getUploadedAt().toString();
    }
}