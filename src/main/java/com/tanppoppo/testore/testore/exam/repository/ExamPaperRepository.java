package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.exam.entity.ExamPaperEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaperEntity, Integer> {

    // count : exam_paper_id를 기준으로 exam_item 테이블의 수 : 문제수
    @Query("SELECT COUNT(ei) FROM ExamItemEntity ei WHERE ei.examPaperId.examPaperId = :examPaperId")
    Integer getExamItemCount(@Param("examPaperId") Integer examPaperId);

    // like  : exam_paper_id를 기준으로 like 테이블의 수 : 좋아요수
    @Query("SELECT COUNT(l) FROM ItemLikeEntity l WHERE l.itemLikeId = :examPaperId")
    Integer getLikeCount(@Param("examPaperId") Integer examPaperId);

    // share : 일대다 관계인 memberEntity의 member_id를 기준으로 examPaperEntity의 create_id != owner_id인 수 : 공유수
    @Query("SELECT COUNT(ep) FROM ExamPaperEntity ep WHERE ep.creatorId.memberId != ep.ownerId AND ep.creatorId.memberId = :creatorId")
    Integer getShareCount(@Param("creatorId") Integer creatorId);

}
