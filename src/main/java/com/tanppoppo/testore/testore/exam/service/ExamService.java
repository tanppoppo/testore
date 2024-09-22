package com.tanppoppo.testore.testore.exam.service;

import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.dto.ExamResultDTO;
import com.tanppoppo.testore.testore.exam.dto.QuestionParagraphDTO;
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

    List<ExamResultDTO> findExamResultByMemberId(AuthenticatedUser user);

}
