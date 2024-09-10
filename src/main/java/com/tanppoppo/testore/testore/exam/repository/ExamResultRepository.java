package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.exam.entity.ExamResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResultEntity, Integer> {
}
