package com.tanppoppo.testore.testore.word.repository;

import com.tanppoppo.testore.testore.word.entity.WordBookEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordBookRepository extends JpaRepository<WordBookEntity, Integer> {

    List<WordBookEntity> findByOwnerId(Integer ownerId);

    @Query("SELECT COUNT(wb) FROM WordBookEntity wb WHERE wb.ownerId = :ownerId")
    Integer countByOwnerId(@Param("ownerId") Integer ownerId);

    // memberId를 기준으로 wordBook을 정렬하여 List를 반환함
    List<WordBookEntity> findByOwnerId(Integer id, Sort sort);

    // count : 단어수
    @Query("SELECT COUNT(ei) FROM WordEntity ei WHERE ei.wordBookId.wordBookId = :wordBookId")
    Integer getWordItemCount(@Param("wordBookId") Integer wordBookId);

    // like  : 좋아요수
    @Query("SELECT COUNT(l) FROM ItemLikeEntity l WHERE l.itemId = :wordBookId")
    Integer getLikeCount(@Param("wordBookId") Integer wordBookId);

    // reviews : 리뷰수
    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.itemId = :wordBookId")
    Integer getReviewCount(@Param("wordBookId") Integer wordBookId);

    // share : 공유수
    @Query("SELECT SUM(CASE WHEN ep.ownerId != ep.creatorId.memberId THEN 1 ElSE 0 END) FROM WordBookEntity ep WHERE ep.creatorId.memberId = :memberId")
    Integer getShareCount(@Param("memberId") Integer memberId);

    // likeState : 좋아요 여부
//    @Query("SELECT COUNT(le) > 0 FROM ItemLikeEntity le WHERE le.memberId.memberId = :memberId AND le.itemId = :examPaperId")
//    Boolean getLikeState(@Param("memberId") Integer memberId, @Param("wordBookId") Integer wordBookId);

}
