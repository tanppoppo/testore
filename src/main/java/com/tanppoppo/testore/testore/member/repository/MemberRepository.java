package com.tanppoppo.testore.testore.member.repository;

import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    Optional<MemberEntity> findByEmail(String email);

    Optional<MemberEntity> findByEmailVerificationToken(String token);

    // 이메일 존재 여부 확인 메서드 추가
    boolean existsByEmail(String email);
}
