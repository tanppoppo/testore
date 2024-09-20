package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.exam.entity.ExamQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link ExamQuestionEntity}를 위한 데이터 접근을 관리하는 리포지토리 인터페이스
 * @author gyahury
 * @version 0.1.0
 * @since 0.1.0
 */
public interface QuestionParagraphRepository extends JpaRepository<ExamQuestionEntity, Integer>  {
}
