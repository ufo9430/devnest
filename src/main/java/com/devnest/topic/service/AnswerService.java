package com.devnest.topic.service;

import com.devnest.topic.domain.Answer;
import com.devnest.topic.domain.Topic;
import com.devnest.topic.dto.AnswerAcceptRequestDto;
import com.devnest.topic.dto.AnswerRequestDto;
import com.devnest.topic.dto.AnswerResponseDto;
import com.devnest.topic.repository.AnswerRepository;
import com.devnest.topic.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final TopicRepository topicRepository;
    private final MarkdownService markdownService;

    @Transactional(readOnly = true)
    public List<AnswerResponseDto> getAnswersByTopicId(Long topicId) {
        return answerRepository.findByTopicIdOrderByCreatedAtDesc(topicId).stream()
                .map(AnswerResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AnswerResponseDto createAnswer(AnswerRequestDto requestDto) {
        Topic topic = topicRepository.findById(requestDto.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("해당 질문을 찾을 수 없습니다."));

        // HTML 새니타이징 적용
        String sanitizedContent = markdownService.sanitizeHtml(requestDto.getContent());

        Answer answer = Answer.builder()
                .content(sanitizedContent)  // 새니타이징된 내용 사용
                .userId(requestDto.getUserId())
                .topic(topic)
                .isAccepted(false)
                .build();

        return new AnswerResponseDto(answerRepository.save(answer));
    }

    @Transactional
    public AnswerResponseDto updateAnswer(Long answerId, AnswerRequestDto requestDto, Long currentUserId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 답변을 찾을 수 없습니다."));

        // 작성자 확인
        if (!answer.getUserId().equals(currentUserId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // HTML 새니타이징 적용
        String sanitizedContent = markdownService.sanitizeHtml(requestDto.getContent());
        answer.updateContent(sanitizedContent);  // 새니타이징된 내용으로 업데이트

        return new AnswerResponseDto(answerRepository.save(answer));
    }

    @Transactional
    public void deleteAnswer(Long answerId, Long currentUserId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 답변을 찾을 수 없습니다."));

        // 작성자 확인
        if (!answer.getUserId().equals(currentUserId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        // 채택된 답변인지 확인
        if (answer.isAccepted()) {
            throw new IllegalStateException("채택된 답변은 삭제할 수 없습니다.");
        }

        answerRepository.delete(answer);
    }

    @Transactional
    public AnswerResponseDto acceptAnswer(AnswerAcceptRequestDto requestDto) {
        // 질문 조회
        Topic topic = topicRepository.findById(requestDto.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("해당 질문을 찾을 수 없습니다."));

        // 답변 조회
        Answer answer = answerRepository.findById(requestDto.getAnswerId())
                .orElseThrow(() -> new EntityNotFoundException("해당 답변을 찾을 수 없습니다."));

        // 질문 작성자 확인
        if (!topic.getUserId().equals(requestDto.getUserId())) {
            throw new IllegalStateException("답변을 채택할 권한이 없습니다.");
        }

        // 이미 채택된 답변이 있는지 확인
        boolean hasAcceptedAnswer = answerRepository.existsByTopicIdAndIsAcceptedTrue(topic.getId());
        if (hasAcceptedAnswer) {
            throw new IllegalStateException("이미 채택된 답변이 있습니다.");
        }

        // 답변 채택 처리
        answer.accept();
        answerRepository.save(answer);

        // TODO: 답변 작성자에게 알림 전송 로직 추가

        return new AnswerResponseDto(answer);
    }

    @Transactional
    public AnswerResponseDto unacceptAnswer(AnswerAcceptRequestDto requestDto) {
        // 질문 조회
        Topic topic = topicRepository.findById(requestDto.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("해당 질문을 찾을 수 없습니다."));

        // 답변 조회
        Answer answer = answerRepository.findById(requestDto.getAnswerId())
                .orElseThrow(() -> new EntityNotFoundException("해당 답변을 찾을 수 없습니다."));

        // 질문 작성자 확인
        if (!topic.getUserId().equals(requestDto.getUserId())) {
            throw new IllegalStateException("답변 채택을 취소할 권한이 없습니다.");
        }

        // 채택 취소 처리
        answer.unaccept();
        answerRepository.save(answer);

        return new AnswerResponseDto(answer);
    }
}