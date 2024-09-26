package com.tanppoppo.testore.testore.member.repository;

import com.tanppoppo.testore.testore.member.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link ReviewEntity}를 위한 데이터 접근을 관리하는 리포지토리 인터페이스
 * @author gyahury
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {
}
