package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.exam.entity.ExamPaperEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaperEntity, Integer> {

    // 정렬 리스트 반환
    List<ExamPaperEntity> findByOwnerId(Integer memberId, Sort sort);

    // 문제 수
    @Query("SELECT COUNT(ei) FROM ExamQuestionEntity ei WHERE ei.examPaperId.examPaperId = :examPaperId")
    Integer getExamItemCount(@Param("examPaperId") Integer examPaperId);

    // 공유 수
    @Query("SELECT SUM(CASE WHEN ep.ownerId != ep.creatorId.memberId THEN 1 ELSE 0 END) FROM ExamPaperEntity ep WHERE ep.creatorId.memberId = :memberId")
    Integer getShareCount(@Param("memberId") Integer memberId);

    // 시험지 수
    @Query("SELECT COUNT(ep) FROM ExamPaperEntity ep WHERE ep.ownerId = :memberId")
    Integer countExamPapersByOwnerId(@Param("memberId") Integer memberId);

}
