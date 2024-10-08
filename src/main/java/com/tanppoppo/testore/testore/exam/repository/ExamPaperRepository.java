package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.exam.entity.ExamPaperEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    // 전체 시험지 랜덤 반환 -> publicOption 고려
    @Query("SELECT ep FROM ExamPaperEntity ep WHERE ep.publicOption = true ORDER BY FUNCTION('RAND')")
    List<ExamPaperEntity> findRandomExamPapers(Pageable pageable);

    // 시험지 공유수 순위 정렬하여 Entity 반환 -> publicOption 고려
    @Query("SELECT ep FROM ExamPaperEntity ep WHERE ep.publicOption = true GROUP BY ep ORDER BY COUNT(CASE WHEN ep.ownerId != ep.creatorId.memberId THEN 1 END) DESC")
    List<ExamPaperEntity> findSortedExamPapersByShareCount(Pageable pageable);

    // 주제 : 이번주 인기 시험지 추천
    /* 조건 1. 현재날짜를 기준으로 월요일과 일요일 사이의 날짜만을 출력함.
     * 조건 2. publicOption이 true인것만을 출력함.
     * 조건 3. itemLikeEntity의 itemLikeId가 examPaperId와 같은 경우가 가장 많은 순서대로 정렬함. */
    @Query("SELECT ep FROM ExamPaperEntity ep LEFT JOIN ItemLikeEntity il ON ep.examPaperId = il.itemId WHERE ep.publicOption = true AND il.createdDate BETWEEN :monday AND :sunday GROUP BY ep.examPaperId ORDER BY COUNT(il) DESC")
    List<ExamPaperEntity> findPopularExamsThisWeek(@Param("monday") LocalDateTime monday, @Param("sunday") LocalDateTime sunday, Pageable pageable);

}
