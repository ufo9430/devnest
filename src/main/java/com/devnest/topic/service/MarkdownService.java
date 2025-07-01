package com.devnest.topic.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class MarkdownService {

    /**
     * HTML을 안전하게 새니타이징합니다.
     * @param content 사용자 입력 HTML
     * @return 안전한 HTML
     */
    public String sanitizeHtml(String content) {
        if (content == null) {
            return "";
        }
        return Jsoup.clean(content, Safelist.relaxed()
                .addTags("h1", "h2", "h3", "h4", "h5", "h6") // 제목 태그 허용
                .addAttributes("a", "href", "title") // 링크에 href와 title 속성 허용
                .addAttributes("img", "src", "alt", "title") // 이미지에 src, alt, title 속성 허용
                .addProtocols("img", "src", "http", "https") // 이미지 주소는 http, https만 허용
                .addEnforcedAttribute("a", "rel", "nofollow") // 모든 링크에 rel="nofollow" 추가
                .addEnforcedAttribute("a", "target", "_blank")); // 모든 링크에 target="_blank" 추가
    }

    /**
     * 일반 텍스트를 HTML로 변환합니다.
     * 줄바꿈을 <br> 태그로 변환하고, HTML 특수문자를 이스케이프 처리합니다.
     * @param text 일반 텍스트
     * @return HTML로 변환된 텍스트
     */
    public String textToHtml(String text) {
        if (text == null) {
            return "";
        }
        return Jsoup.clean(text, Safelist.none())
                .replace("\n", "<br>");
    }
}