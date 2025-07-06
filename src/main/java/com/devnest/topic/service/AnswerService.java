package com.devnest.topic.service;

import com.devnest.topic.domain.Answer;
import com.devnest.topic.domain.Topic;
import com.devnest.topic.domain.Vote;
import com.devnest.topic.dto.AnswerAcceptRequestDto;
import com.devnest.topic.dto.AnswerRequestDto;
import com.devnest.topic.dto.AnswerResponseDto;
import com.devnest.topic.repository.AnswerRepository;
import com.devnest.topic.repository.TopicRepository;
import com.devnest.topic.repository.VoteRepository;
import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    public List<AnswerResponseDto> getAnswersByTopicId(Long topicId) {
        List<Answer> answers = answerRepository.findByTopicIdOrderByVoteScoreDesc(topicId);
        return answers.stream().map(answer -> {
            User user = userRepository.findById(answer.getUserId()).orElse(null);

            // 1. 추천/비추천 카운트 계산
            int likeCount = voteRepository.countByTargetIdAndType(answer.getId(), Vote.VoteType.LIKE);
            int dislikeCount = voteRepository.countByTargetIdAndType(answer.getId(), Vote.VoteType.DISLIKE);
            int voteCount = likeCount - dislikeCount;

            // 2. DTO 생성 및 voteCount 세팅
            AnswerResponseDto dto = new AnswerResponseDto(answer, user);
            dto.setVoteCount(voteCount);

            return dto;
        }).collect(Collectors.toList());
    }

    // 마크다운 → HTML 변환
    private String convertMarkdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Node document = parser.parse(markdown == null ? "" : markdown);
        return renderer.render(document);
    }

    // HTML 새니타이징
    private String sanitizeHtml(String html) {
        return Jsoup.clean(html, Safelist.basicWithImages());
    }


    @Transactional
    public AnswerResponseDto createAnswer(AnswerRequestDto requestDto) {
        Topic topic = topicRepository.findById(requestDto.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("해당 질문을 찾을 수 없습니다."));

        String markdown = requestDto.getMarkdownContent();
        String html = convertMarkdownToHtml(markdown);
        String sanitizedHtml = sanitizeHtml(html);

        Answer answer = Answer.builder()
                .content(sanitizedHtml)          // 변환+새니타이징된 HTML
                .markdownContent(markdown)       // 마크다운 원본
                .userId(requestDto.getUserId())
                .topic(topic)
                .isAccepted(false)
                .build();

        return new AnswerResponseDto(answerRepository.save(answer));
    }

    @Transactional
    public void updateAnswer(Long answerId, Long userId, String markdownContent) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변 없음"));
        if (!answer.getUserId().equals(userId)) try {
            throw new AccessDeniedException("수정 권한 없음");
        } catch (AccessDeniedException e) {
            throw new RuntimeException(e);
        }

        String html = convertMarkdownToHtml(markdownContent);
        String sanitizedHtml = sanitizeHtml(html);

        answer.setMarkdownContent(markdownContent);
        answer.updateContent(sanitizedHtml);
        // JPA dirty checking
    }

    @Transactional
    public void deleteAnswer(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변 없음"));
        if (!answer.getUserId().equals(userId)) try {
            throw new AccessDeniedException("삭제 권한 없음");
        } catch (AccessDeniedException e) {
            throw new RuntimeException(e);
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