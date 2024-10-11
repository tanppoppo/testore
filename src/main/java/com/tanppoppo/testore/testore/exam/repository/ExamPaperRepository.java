package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
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

    // 북마크 여부 고려 정렬 리스트 반환
    @Query("SELECT ep FROM ExamPaperEntity ep LEFT JOIN BookmarkEntity bm ON ep.examPaperId = bm.itemId AND bm.itemType = :itemType WHERE ep.ownerId = :ownerId ORDER BY CASE WHEN bm IS NOT NULL THEN 1 ELSE 0 END DESC, ep.examPaperId DESC")
    List<ExamPaperEntity> findByOwnerIdWithBookmarks(Integer ownerId, ItemTypeEnum itemType);


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

    // 이번주 인기 시험지 추천 -> publicOption 고려
    @Query("SELECT ep FROM ExamPaperEntity ep LEFT JOIN ItemLikeEntity il ON ep.examPaperId = il.itemId WHERE ep.publicOption = true AND il.createdDate BETWEEN :monday AND :sunday GROUP BY ep.examPaperId ORDER BY COUNT(il) DESC")
    List<ExamPaperEntity> findPopularExamsThisWeek(@Param("monday") LocalDateTime monday, @Param("sunday") LocalDateTime sunday, Pageable pageable);

    // 공개된 시험지 전체 반환 -> 재사용 가능
    List<ExamPaperEntity> findByPublicOption(boolean publicOption, Sort sort);

    // 키워드와 일치한 공개된 시험지 전체 반환 -> 재사용 가능
    @Query("SELECT ep FROM ExamPaperEntity ep WHERE ep.publicOption = :publicOption AND ep.title LIKE %:title%")
    List<ExamPaperEntity> findByPublicOptionAndTitleContaining(boolean publicOption, String title, Sort sort);

}
