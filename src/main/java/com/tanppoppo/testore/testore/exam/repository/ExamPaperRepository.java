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

    // memberId를 기준으로 examPaper를 정렬하여 List를 반환함 [검증]
    List<ExamPaperEntity> findByOwnerId(Integer memberId, Sort sort);

    // count : 문제수 [검증]
    @Query("SELECT COUNT(ei) FROM ExamQuestionEntity ei WHERE ei.examPaperId.examPaperId = :examPaperId")
    Integer getExamItemCount(@Param("examPaperId") Integer examPaperId);

    // like  : 좋아요수 [검증]
    @Query("SELECT COUNT(l) FROM ItemLikeEntity l WHERE l.itemId = :examPaperId")
    Integer getLikeCount(@Param("examPaperId") Integer examPaperId);

    // reviews : : 리뷰수 [확신]
    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.itemId = :examPaperId")
    Integer getReviewCount(@Param("examPaperId") Integer examPaperId);

    // share : 공유수 [확신]
    @Query("SELECT SUM(CASE WHEN ep.ownerId != ep.creatorId.memberId THEN 1 ELSE 0 END) FROM ExamPaperEntity ep WHERE ep.creatorId.memberId = :memberId")
    Integer getShareCount(@Param("memberId") Integer memberId);

    // likeState : 좋아요 여부 [확신]
    @Query("SELECT COUNT(le) > 0 FROM ItemLikeEntity le WHERE le.memberId.memberId = :memberId AND le.itemId = :examPaperId")
    Boolean getLikeState(@Param("memberId") Integer memberId, @Param("examPaperId") Integer examPaperId);

}
