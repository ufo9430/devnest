package com.devnest.topic.service;

import com.devnest.topic.domain.Answer;
import com.devnest.topic.domain.Topic;
import com.devnest.topic.domain.Topic.TopicStatus;
import com.devnest.topic.dto.TopicRequestDto;
import com.devnest.topic.dto.TopicResponseDto;
import com.devnest.topic.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final MarkdownService markdownService;

    /**
     * 새로운 질문을 생성합니다.
     */
    @Transactional
    public TopicResponseDto createTopic(TopicRequestDto requestDto, Long userId) {

        // HTML 새니타이징 적용
        String sanitizedContent = markdownService.sanitizeHtml(requestDto.getContent());

        Topic topic = Topic.builder()
                .title(requestDto.getTitle())
                .content(sanitizedContent) // 새니타이징된 내용 사용
                .userId(userId)
                .status(TopicStatus.WAITING)
                .build();

        Topic savedTopic = topicRepository.save(topic);
        return new TopicResponseDto(savedTopic);
    }

    /**
     * 질문 목록을 조회합니다. (페이지네이션 적용)
     */
    @Transactional(readOnly = true)
    public Page<TopicResponseDto> getTopics(Pageable pageable) {
        return topicRepository.findAll(pageable)
                .map(TopicResponseDto::new);
    }

    /**
     * 특정 사용자의 질문 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<TopicResponseDto> getTopicsByUserId(Long userId) {
        return topicRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(TopicResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 질문 상세 정보를 조회합니다. (조회수 증가)
     */
    @Transactional
    public TopicResponseDto getDetailTopic(Long topicId) {
        Topic topic = findTopicById(topicId);
        topic.increaseViewCount();
        return new TopicResponseDto(topic);
    }

    /**
     * 질문을 수정합니다.
     */
    @Transactional
    public TopicResponseDto updateDetailTopic(Long topicId, TopicRequestDto requestDto, Long currentUserId) {
        Topic topic = findTopicById(topicId);
        validateTopicOwner(topic, currentUserId);

        topic.update(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getStatus()
        );

        // HTML 새니타이징 적용
        String sanitizedContent = markdownService.sanitizeHtml(requestDto.getContent());
        topic.updateContent(sanitizedContent);  // 새니타이징된 내용으로 업데이트

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
        if (!topic.getUserId().equals(currentUserId)) {
            throw new IllegalStateException("권한이 없습니다.");
        }
    }
}