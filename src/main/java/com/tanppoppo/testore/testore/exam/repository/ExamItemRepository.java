package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.exam.entity.ExamItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamItemRepository extends JpaRepository<ExamItemEntity, Integer> {
}
