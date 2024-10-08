package com.tanppoppo.testore.testore.exam.service;

import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.dto.ExamResultDTO;
import com.tanppoppo.testore.testore.exam.dto.QuestionParagraphDTO;
import com.tanppoppo.testore.testore.member.dto.ReviewDTO;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;

import java.util.List;
import java.util.Map;

public interface ExamService {

    int examCreate(ExamPaperDTO examPaperDTO, AuthenticatedUser user);

    List<ExamPaperDTO> getListItems(AuthenticatedUser user);

    Map<String, Object> selectPaperDetail(int examPaperId, AuthenticatedUser user);

    Integer countExamPapersByOwnerId(Integer memberId);

    Map<Integer, List<QuestionParagraphDTO>> selectQuestionParagraph(int examPaperId, AuthenticatedUser user);

    int calculateScore(Map<String, String[]> choices);

    int startExam(int examResultId, AuthenticatedUser user);

    void endExam(int examPaperId, int score, AuthenticatedUser user);

    List<ExamResultDTO> findExamResultByMemberId(AuthenticatedUser user, String keyword);

    void createQuestion(Map<String, String[]> paragraphs, Integer userId);

    boolean checkQuestionExist(int examPaperId, AuthenticatedUser user);

    ExamResultDTO selectUpdatedPaperInfo(int examPaperId, Integer id);

    void updateExamPaper(ExamPaperDTO examPaperDTO, Integer id);

    void controlPublicOption(int examPaperId);

    Map<String,Object> getListReviews(AuthenticatedUser user, int examPaperId);

    void createReview(AuthenticatedUser user, int examPaperId, ReviewDTO reviewDTO);

    ReviewDTO selectUpdatedReviewInfo(AuthenticatedUser user, int examPaperId, int reviewId);

    void updateReview(AuthenticatedUser user, int reviewId, ReviewDTO reviewDTO);

    void deleteReview(AuthenticatedUser user, int reviewId);

    List<ExamPaperDTO> recommendedExamPaper(AuthenticatedUser user);

    List<ExamPaperDTO> likedExamPaper(AuthenticatedUser user);

    List<ExamPaperDTO> muchSharedExamPaper(AuthenticatedUser user);
}
