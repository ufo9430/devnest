package com.devnest.topic.service;

import com.devnest.topic.repository.FileRepository;
import com.devnest.topic.repository.TagRepository;
import com.devnest.topic.repository.TopicRepository;
import com.devnest.topic.domain.Topic;
import com.devnest.topic.domain.*;
import com.devnest.topic.dto.TopicRequestDto;
import com.devnest.topic.dto.TopicResponseDto;
import com.devnest.topic.repository.VoteRepository;
import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.ast.Node;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final FileRepository fileRepository;

    // 마크다운 → HTML 변환
    private String convertMarkdownToHtml(String markdown) {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                TaskListExtension.create()
        ));
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(markdown == null ? "" : markdown);
        return renderer.render(document);
    }

    // HTML 새니타이징
    private String sanitizeHtml(String html) {
        Safelist safelist = Safelist.basicWithImages()
                .addTags("input", "table", "thead", "tbody", "tfoot", "tr", "th", "td", "h1", "h2", "h3", "h4", "h5", "h6")
                .addAttributes("input", "type", "checked", "disabled")
                .addAttributes("table", "border", "cellspacing", "cellpadding");
        return Jsoup.clean(html, safelist);
    }

    /**
     * 새로운 질문을 생성합니다.
     */
    @Transactional
    public void createTopic(TopicRequestDto requestDto, Long userId) {
        // 마크다운 원본과 변환된 HTML
        String markdown = requestDto.getMarkdownContent();
        String html = convertMarkdownToHtml(markdown);
        String sanitizedHtml = sanitizeHtml(html);

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        // Topic 생성 (HTML/마크다운 모두 저장)
        Topic newTopic = Topic.builder()
                .title(requestDto.getTitle())
                .content(sanitizedHtml) // 변환+새니타이징된 HTML
                .markdownContent(markdown) // 마크다운 원본
                .user(user)
                .status(requestDto.getStatus())
                .tags(new ArrayList<>())
                .build();

        // 태그 연결
        for (String tagName : requestDto.getTags()) {
            Tag tag;
            try{
                tag = tagRepository.findByName(tagName).orElseThrow(EntityNotFoundException::new);
            }catch (EntityNotFoundException e){
                Tag newTag = new Tag(tagName);
                tag = tagRepository.saveAndFlush(newTag);
            }

            newTopic.getTags().add(tag);
        }

        // 1. 질문 저장
        Topic saved = topicRepository.save(newTopic);

        // 2. 마크다운에서 이미지 url 추출
        List<String> imageUrls = extractImageUrls(markdown);

        // 3. files 테이블의 targetId update
        int updated = fileRepository.updateTargetIdByUrls(saved.getId(), imageUrls, File.TargetType.TOPIC);

        topicRepository.save(newTopic);
    }


    /**
     * 질문 상세 정보를 조회합니다. (조회수 증가)
     */
    @Transactional
    public TopicResponseDto getDetailTopic(Long topicId) {
        Topic topic = findTopicById(topicId);
        TopicResponseDto dto = new TopicResponseDto(topic);

        topic.increaseViewCount();

        // 사용자 정보 조회
        User user = topic.getUser();
        Long userId = (user != null) ? user.getUserId() : null;

        // 사용자 닉네임 조회
        String nickname = userRepository.findById(topic.getUser().getUserId())
                .map(User::getNickname)
                .orElse("알 수 없음");

        String ProfileImage = (user != null && user.getProfileImage() != null && !user.getProfileImage().isBlank())
                ? user.getProfileImage()
                : "/images/default-profile.png"; // 기본 이미지 경로

        // 추천수 세팅
        int voteCount = voteRepository.countByTargetIdAndType(topicId, Vote.VoteType.LIKE)
                - voteRepository.countByTargetIdAndType(topicId, Vote.VoteType.DISLIKE);
        dto.setVoteCount(voteCount);

        // 이미지 파일만 추출 (targetType=TOPIC, targetId=topicId)
        List<File> files = fileRepository.findByTargetTypeAndTargetId(File.TargetType.TOPIC, topicId);
        List<String> imageUrls = files.stream()
                .filter(f -> isImageFile(f.getFilename()))
                .map(File::getUrl)
                .collect(Collectors.toList());
        dto.setImageUrls(imageUrls);

        // 파일 첨부(비이미지)도 필요하다면 fileUrls도 세팅
        List<String> fileUrls = files.stream()
                .filter(f -> !isImageFile(f.getFilename()))
                .map(File::getUrl)
                .collect(Collectors.toList());
        dto.setFileUrls(fileUrls);

        dto.setUserId(userId);
        dto.setUserNickname(nickname); // 닉네임 세팅
        dto.setProfileImage(ProfileImage); // 이미지 URL 세팅
        return dto;
    }

    /**
     * 질문을 수정합니다.
     */
    @Transactional
    public TopicResponseDto updateDetailTopic(Long topicId, TopicRequestDto requestDto, Long currentUserId) {
        Topic topic = findTopicById(topicId);
        validateTopicOwner(topic, currentUserId);

        // 마크다운 원본과 변환된 HTML
        String markdown = requestDto.getMarkdownContent();
        String html = convertMarkdownToHtml(markdown);
        String sanitizedHtml = sanitizeHtml(html);

        // 내용 및 상태 업데이트
        topic.setTitle(requestDto.getTitle());
        topic.setContent(sanitizedHtml);        // 새니타이징된 HTML
        topic.setMarkdownContent(markdown);     // 마크다운 원본
        topic.setStatus(requestDto.getStatus());

        // 태그 변경도 필요하다면 여기에 추가

        return new TopicResponseDto(topic);
    }

    /**
     * 질문을 삭제합니다.
     */
    @Transactional
    public void deleteDetailTopic(Long topicId, Long currentUserId) {
        Topic topic = findTopicById(topicId);
        validateTopicOwner(topic, currentUserId);
        topicRepository.delete(topic);
    }

    /**
     * ID로 질문을 조회합니다. (도우미 메서드)
     */
    private Topic findTopicById(Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("해당 질문을 찾을 수 없습니다."));
    }

    /**
     * 질문 소유자 확인 (도우미 메서드)
     */
    private void validateTopicOwner(Topic topic, Long currentUserId) {
        if (!topic.getUser().getUserId().equals(currentUserId)) {
            throw new IllegalStateException("권한이 없습니다.");
        }
    }

    // 이미지 url 추출 유틸
    private List<String> extractImageUrls(String markdown) {
        if (markdown == null || markdown.isEmpty()) return new ArrayList<>();
        Pattern pattern = Pattern.compile("!\\[[^\\]]*\\]\\((/uploads/[^)]+)\\)");
        Matcher matcher = pattern.matcher(markdown);
        List<String> urls = new ArrayList<>();
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }

    // 이미지 확장자 판별 유틸
    private boolean isImageFile(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif") || lower.endsWith(".bmp") || lower.endsWith(".webp");
    }
}