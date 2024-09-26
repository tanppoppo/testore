package com.tanppoppo.testore.testore.member.repository;

import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.entity.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link PointEntity}를 위한 데이터 접근을 관리하는 리포지토리 인터페이스
 * @author gyahury
 * @version 0.1.0
 * @since 0.1.0
 */
@Repository
public interface PointRepository extends JpaRepository<PointEntity, Integer>  {

    List<PointEntity> findByMemberId(MemberEntity memberId);

}
