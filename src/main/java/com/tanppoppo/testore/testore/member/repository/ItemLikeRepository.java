package com.tanppoppo.testore.testore.member.repository;

import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
import com.tanppoppo.testore.testore.member.entity.BookmarkEntity;
import com.tanppoppo.testore.testore.member.entity.ItemLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * {@link ItemLikeEntity}를 위한 데이터 접근을 관리하는 리포지토리 인터페이스
 * @author gyahury
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ItemLikeRepository extends JpaRepository<ItemLikeEntity, Integer> {

    // 좋아요 기능
    @Query("select il from ItemLikeEntity il where il.itemType = :itemType and il.memberId.memberId =:memberId and il.itemId =:examPaperId")
    ItemLikeEntity selectItemLikeByMemberId(@Param("memberId") Integer memberId, @Param("examPaperId") Integer examPaperId, ItemTypeEnum itemType);

    // 좋아요 여부
    @Query("SELECT COUNT(le) > 0 FROM ItemLikeEntity le WHERE  le.itemType = 'EXAM' AND le.memberId.memberId = :memberId AND le.itemId = :examPaperId")
    Boolean getLikeState(@Param("memberId") Integer memberId, @Param("examPaperId") Integer examPaperId);

    // 좋아요 수
    @Query("SELECT COUNT(l) FROM ItemLikeEntity l WHERE l.itemId = :examPaperId")
    Integer getLikeCount(@Param("examPaperId") Integer examPaperId);

}
