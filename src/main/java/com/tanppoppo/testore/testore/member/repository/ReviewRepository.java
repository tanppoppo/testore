package com.tanppoppo.testore.testore.member.repository;

import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
import com.tanppoppo.testore.testore.member.entity.ReviewEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * {@link ReviewEntity}를 위한 데이터 접근을 관리하는 리포지토리 인터페이스
 * @author gyahury
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    // 리뷰 수
    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.itemId = :itemId")
    Integer getReviewCount(Integer itemId);

    // examPaper 소속 리뷰 전체 정렬 반환
    @Query("SELECT r FROM ReviewEntity r WHERE r.itemId = :itemId AND r.itemType = :itemType")
    List<ReviewEntity> findByItemIdAndItemType(Integer itemId, ItemTypeEnum itemType, Sort sort);

}
