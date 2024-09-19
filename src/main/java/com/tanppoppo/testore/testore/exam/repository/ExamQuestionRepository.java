package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.exam.entity.ExamQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestionEntity, Integer> {
}
