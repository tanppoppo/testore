package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
import com.tanppoppo.testore.testore.exam.entity.ExamPaperEntity;
import com.tanppoppo.testore.testore.exam.entity.ExamResultEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaperEntity, Integer> {

    // 북마크 상위 정렬 시험지 메인 리스트
    @Query("SELECT ep FROM ExamPaperEntity ep LEFT JOIN BookmarkEntity bm ON ep.examPaperId = bm.itemId AND bm.itemType = :itemType WHERE ep.ownerId = :ownerId ORDER BY CASE WHEN bm IS NOT NULL THEN 1 ELSE 0 END DESC, ep.examPaperId DESC")
    List<ExamPaperEntity> findByOwnerIdWithBookmarks(Integer ownerId, ItemTypeEnum itemType);

    // 문제 수
    @Query("SELECT COUNT(ei) FROM ExamQuestionEntity ei WHERE ei.examPaperId.examPaperId = :examPaperId")
    Integer getExamItemCount(Integer examPaperId);

    // 공유 수 ( 예정 )
//    @Query("SELECT SUM(CASE WHEN ep.ownerId != ep.creatorId.memberId THEN 1 ELSE 0 END) FROM ExamPaperEntity ep WHERE ep.creatorId.memberId = :memberId")
//    Integer getShareCount(Integer memberId);

    // examResultEntity의 status가 COMPLETED인 경우를 count하여 Integer로 반환함 [ 응시수 ]
    @Query("SELECT COUNT(er) FROM ExamResultEntity er WHERE er.status = 'COMPLETED' AND er.examPaperId.examPaperId = :examPaperId")
    Integer getShareCount(Integer examPaperId);

    // 시험지 수
    @Query("SELECT COUNT(ep) FROM ExamPaperEntity ep WHERE ep.ownerId = :memberId")
    Integer countExamPapersByOwnerId(Integer memberId);

    // 전체 시험지 랜덤
    @Query("SELECT ep FROM ExamPaperEntity ep WHERE ep.publicOption = true ORDER BY FUNCTION('RAND')")
    List<ExamPaperEntity> findRandomExamPapers(Pageable pageable);

    // 시험지 공유수 순위 정렬 ( 예정 ) 수정중
//    @Query("SELECT ep FROM ExamPaperEntity ep WHERE ep.publicOption = true GROUP BY ep HAVING COUNT(CASE WHEN ep.ownerId != ep.creatorId.memberId THEN 1 END) > 0 ORDER BY COUNT(CASE WHEN ep.ownerId != ep.creatorId.memberId THEN 1 END) DESC")
//    List<ExamPaperEntity> findSortedExamPapersByShareCount(Pageable pageable);

    // 시험지 응시수 순위 정렬
    @Query("SELECT ep FROM ExamPaperEntity ep JOIN ExamResultEntity er ON ep.examPaperId = er.examPaperId.examPaperId WHERE ep.publicOption = true AND er.status = 'COMPLETED' GROUP BY ep HAVING COUNT(er) > 0 ORDER BY COUNT(er) DESC")
    List<ExamPaperEntity> findSortedExamPapersByShareCount(Pageable pageable);

    // 이번주 인기 시험지 순위 정렬
    @Query("SELECT ep FROM ExamPaperEntity ep LEFT JOIN ItemLikeEntity il ON ep.examPaperId = il.itemId WHERE ep.publicOption = true AND il.createdDate BETWEEN :monday AND :sunday AND il.itemType = :itemTypeEnum GROUP BY ep.examPaperId ORDER BY COUNT(il) DESC")
    List<ExamPaperEntity> findPopularExamsThisWeek(LocalDateTime monday, LocalDateTime sunday, Pageable pageable, ItemTypeEnum itemTypeEnum);

    // 공개된 시험지 전체 반환
    List<ExamPaperEntity> findByPublicOption(boolean publicOption, Sort sort);

    // 키워드와 일치한 공개된 시험지 전체
    @Query("SELECT ep FROM ExamPaperEntity ep WHERE ep.publicOption = :publicOption AND ep.title LIKE %:title%")
    List<ExamPaperEntity> findByPublicOptionAndTitleContaining(boolean publicOption, String title, Sort sort);

    // 응시자 내역 조회
    @Query("SELECT er FROM ExamResultEntity er WHERE er.examPaperId.examPaperId = :examPaperId AND er.examScore IS NOT NULL AND er.startTime IS NOT NULL ORDER BY er.startTime DESC")
    List<ExamResultEntity> findExamResultsByExamPaperId(Integer examPaperId);

    // 내가 좋아요한 시험지 조회
    @Query("SELECT ep FROM ExamPaperEntity ep JOIN ItemLikeEntity le ON le.itemId = ep.examPaperId WHERE le.memberId.memberId = :memberId and le.itemType = :itemType and ep.publicOption = true order by ep.examPaperId desc")
    List<ExamPaperEntity> findLikedExamPapersByMemberId(Integer memberId, ItemTypeEnum itemType);

    // 내가 북마크한 시험지 조회
    @Query("SELECT ep FROM ExamPaperEntity ep JOIN BookmarkEntity be ON be.itemId = ep.examPaperId WHERE be.memberId.memberId = :memberId and be.itemType = :itemType order by ep.examPaperId desc")
    List<ExamPaperEntity> findBookmarkedExamPapersByMemberId(Integer memberId, ItemTypeEnum itemType);

    // 키워드 기준으로 내가 좋아요한 시험지 조회
    @Query("SELECT ep FROM ExamPaperEntity ep JOIN ItemLikeEntity le ON le.itemId = ep.examPaperId WHERE le.memberId.memberId = :memberId OR ep.title LIKE %:keyword% and le.itemType = :itemType and ep.publicOption = true order by ep.examPaperId desc")
    List<ExamPaperEntity> findLikedExamPapersByMemberIdAndKeyword(Integer memberId, ItemTypeEnum itemType, String keyword);

    // 키워드 기준으로 내가 북마크한 시험지 조회
    @Query("SELECT ep FROM ExamPaperEntity ep JOIN BookmarkEntity bm ON bm.itemId = ep.examPaperId WHERE bm.memberId.memberId = :memberId OR ep.title LIKE %:keyword% and bm.itemType = :itemType order by ep.examPaperId desc")
    List<ExamPaperEntity> findBookmarkedExamPapersByMemberIdAndKeyword(Integer memberId, ItemTypeEnum itemType, String keyword);

}
