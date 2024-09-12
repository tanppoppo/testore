package com.tanppoppo.testore.testore.member.service;

import com.tanppoppo.testore.testore.member.dto.MemberDTO;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository mr;
    private final BCryptPasswordEncoder PasswordEncoder;

    /* 회원가입 */
    @Override
    public void joinMember(MemberDTO memberDTO) {

        MemberEntity entity = MemberEntity.builder()
                .email(memberDTO.getEmail())
                .nickname(memberDTO.getNickname())
                .memberPassword(PasswordEncoder.encode(memberDTO.getMemberPassword()))
                .build();
        mr.save(entity);

    }
}
