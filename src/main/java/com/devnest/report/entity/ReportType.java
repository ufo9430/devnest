package com.devnest.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
    SPAM("스팸/광고"),
    INAPPROPRIATE_CONTENT("부적절한 내용"),
    HARASSMENT("괴롭힘/욕설"),
    FAKE_INFORMATION("허위정보"),
    COPYRIGHT_VIOLATION("저작권 침해"),
    PRIVACY_VIOLATION("개인정보 침해"),
    HATE_SPEECH("혐오 발언"),
    VIOLENCE("폭력적 내용"),
    SEXUAL_CONTENT("성적 내용"),
    OTHER("기타");

    private final String description;
}