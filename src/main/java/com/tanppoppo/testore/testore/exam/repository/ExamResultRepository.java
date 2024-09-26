package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.common.util.ExamStatusEnum;
import com.tanppoppo.testore.testore.exam.entity.ExamResultEntity;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResultEntity, Integer> {
    List<ExamResultEntity> findByMemberIdAndStatus(MemberEntity memberEntity, ExamStatusEnum status);

    @Query("SELECT er FROM ExamResultEntity er JOIN ExamPaperEntity ep ON er.examPaperId = ep WHERE er.memberId = :member AND er.status = :status AND ep.title LIKE %:title%")
    List<ExamResultEntity> findByMemberIdAndStatusAndExamPaperTitleContaining(MemberEntity member, ExamStatusEnum status, String title);

}
