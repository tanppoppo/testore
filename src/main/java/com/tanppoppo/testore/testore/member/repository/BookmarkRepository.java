package com.tanppoppo.testore.testore.member.repository;

import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
import com.tanppoppo.testore.testore.member.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * {@link BookmarkEntity}를 위한 데이터 접근을 관리하는 리포지토리 인터페이스
 * @author gyahury
 * @version 0.1.0
 * @since 0.1.0
 */
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Integer> {

    // 북마크 기능
    @Query("select bm from BookmarkEntity bm where bm.itemType = :itemType and bm.memberId.memberId =:memberId and bm.itemId = :itemId")
    BookmarkEntity selectBookmarkByMemberId(Integer memberId, Integer itemId, ItemTypeEnum itemType);

    // 북마크 여부
    @Query("select count(bm) > 0 from BookmarkEntity bm where bm.itemType = :itemType and bm.memberId.memberId = :memberId and bm.itemId = :itemId")
    Boolean getBookmarkState(Integer memberId, Integer itemId, ItemTypeEnum itemType);

}
