package com.tanppoppo.testore.testore.word.repository;

import com.tanppoppo.testore.testore.word.entity.WordBookEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WordBookRepository extends JpaRepository<WordBookEntity, Integer> {

    // 북마크 여부 고려 정렬 리스트 반환
    @Query("SELECT ep FROM WordBookEntity ep LEFT JOIN BookmarkEntity bm ON ep.wordBookId = bm.itemId WHERE ep.ownerId = :ownerId ORDER BY CASE WHEN bm IS NOT NULL THEN 1 ELSE 0 END DESC, ep.wordBookId DESC")
    List<WordBookEntity> findByOwnerIdWithBookmarks(@Param("ownerId") Integer ownerId, Sort sort);

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

    // 전체 시험지 랜덤 반환 -> publicOption 고려
    @Query("SELECT ep FROM WordBookEntity ep WHERE ep.publicOption = true ORDER BY FUNCTION('RAND')")
    List<WordBookEntity> findRandomWordBooks(Pageable pageable);

    // 시험지 공유수 순위 정렬하여 Entity 반환 -> publicOption 고려
    @Query("SELECT ep FROM WordBookEntity ep WHERE ep.publicOption = true GROUP BY ep ORDER BY COUNT(CASE WHEN ep.ownerId != ep.creatorId.memberId THEN 1 END) DESC")
    List<WordBookEntity> findSortedWordBooksByShareCount(Pageable pageable);

    // 이번주 인기 시험지 추천 -> publicOption 고려
    @Query("SELECT ep FROM WordBookEntity ep LEFT JOIN ItemLikeEntity il ON ep.wordBookId = il.itemId WHERE ep.publicOption = true AND il.createdDate BETWEEN :monday AND :sunday GROUP BY ep.wordBookId ORDER BY COUNT(il) DESC")
    List<WordBookEntity> findPopularWordsThisWeek(@Param("monday") LocalDateTime monday, @Param("sunday") LocalDateTime sunday, Pageable pageable);

}
