package com.tanppoppo.testore.testore.word.repository;

import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
import com.tanppoppo.testore.testore.word.entity.WordBookEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WordBookRepository extends JpaRepository<WordBookEntity, Integer> {

    // 북마크 상위 정렬 단어장 전체 반환 [ 검수 완료 ]
    @Query("SELECT wb FROM WordBookEntity wb LEFT JOIN BookmarkEntity bm ON wb.wordBookId = bm.itemId AND bm.itemType = :itemType WHERE wb.ownerId = :ownerId ORDER BY CASE WHEN bm IS NOT NULL THEN 1 ELSE 0 END DESC, wb.wordBookId DESC")
    List<WordBookEntity> findByOwnerIdWithBookmarks(Integer ownerId, ItemTypeEnum itemType);


    // 단어 수 [ 검수 완료 ]
    @Query("SELECT COUNT(w) FROM WordEntity w WHERE w.wordBookId.wordBookId = :wordBookId")
    Integer getWordItemCount(Integer wordBookId);

    // 공유 수 [ 검수 완료 ]
    @Query("SELECT SUM(CASE WHEN wb.ownerId != wb.creatorId.memberId THEN 1 ELSE 0 END) FROM WordBookEntity wb WHERE wb.creatorId.memberId = :memberId")
    Integer getShareCount(Integer memberId);

    // 단어장 수 [ 검수 완료 ]
    @Query("SELECT COUNT(wb) FROM WordBookEntity wb WHERE wb.ownerId = :memberId")
    Integer countWordBooksByOwnerId(Integer memberId);

    // 추천 단어장 [ 단어장 찾기 ]
    @Query("SELECT wb FROM WordBookEntity wb WHERE wb.publicOption = true ORDER BY FUNCTION('RAND')")
    List<WordBookEntity> findRandomWordBooks(Pageable pageable);

    // 많이 공유된 단어장 [ 단어장 찾기 ]
//    @Query("SELECT wb FROM WordBookEntity wb WHERE wb.publicOption = true GROUP BY wb HAVING COUNT(CASE WHEN wb.ownerId != wb.creatorId.memberId THEN 1 END) > 0 ORDER BY COUNT(CASE WHEN wb.ownerId != wb.creatorId.memberId THEN 1 END) DESC")
//    List<WordBookEntity> findSortedWordBooksByShareCount(Pageable pageable);

    @Query("SELECT wb FROM WordBookEntity wb LEFT JOIN LearningRecordEntity lr ON wb.wordBookId = lr.wordBookId.wordBookId WHERE wb.publicOption = true GROUP BY wb ORDER BY COUNT(lr) DESC")
    List<WordBookEntity> findWordBooksOrderByLearningCountDesc(Pageable pageable);

    // 이번주 인기 시험지 [ 단어장 찾기 ]
    @Query("SELECT wb FROM WordBookEntity wb INNER JOIN ItemLikeEntity il ON wb.wordBookId = il.itemId WHERE wb.publicOption = true AND il.createdDate BETWEEN :monday AND :sunday AND il.itemType = :itemTypeEnum GROUP BY wb.wordBookId ORDER BY COUNT(il) DESC")
    List<WordBookEntity> findPopularWordBooksThisWeek(LocalDateTime monday, LocalDateTime sunday, Pageable pageable, ItemTypeEnum itemTypeEnum);

    // 공개된 시험지 전체 반환 [ 단어장 찾기 ]
    List<WordBookEntity> findByPublicOption(boolean publicOption, Sort sort);

    // 키워드와 일치한 공개된 시험지 전체 반환 [ 단어장 찾기 ]
    @Query("SELECT wb FROM WordBookEntity wb WHERE wb.publicOption = :publicOption AND wb.title LIKE %:title%")
    List<WordBookEntity> findByPublicOptionAndTitleContaining(boolean publicOption, String title, Sort sort);

    // 내가 좋아요한 단어장 조회
    @Query("SELECT wb FROM WordBookEntity wb JOIN ItemLikeEntity le ON le.itemId = wb.wordBookId WHERE le.memberId.memberId = :memberId and le.itemType = :itemType order by wb.wordBookId desc")
    List<WordBookEntity> findLikedWordBooksByMemberId(Integer memberId, ItemTypeEnum itemType);

    // 내가 북마크한 단어장 조회
    @Query("SELECT wb FROM WordBookEntity wb JOIN BookmarkEntity bm ON bm.itemId = wb.wordBookId WHERE bm.memberId.memberId = :memberId and bm.itemType = :itemType order by wb.wordBookId desc")
    List<WordBookEntity> findBookmarkedWordBooksByMemberId(Integer memberId, ItemTypeEnum itemType);

    // 키워드 기준으로 내가 좋아요한 단어장 찾기
    @Query("SELECT wb FROM WordBookEntity wb JOIN ItemLikeEntity le ON le.itemId = wb.wordBookId WHERE le.memberId.memberId = :memberId OR wb.title LIKE %:keyword% and le.itemType = :itemType order by wb.wordBookId desc")
    List<WordBookEntity> findWordBooksLikedByMemberAndKeyword(Integer memberId, ItemTypeEnum itemType, String keyword);

    // 키워드 기준으로 내가 북마크한 단어장 찾기
    @Query("SELECT wb FROM WordBookEntity wb JOIN BookmarkEntity bm ON bm.itemId = wb.wordBookId WHERE bm.memberId.memberId = :memberId OR wb.title LIKE %:keyword% and bm.itemType = :itemType order by wb.wordBookId desc")
    List<WordBookEntity> findWordBooksBookmarkedByMemberAndKeyword(Integer memberId, ItemTypeEnum itemType, String keyword);

}
