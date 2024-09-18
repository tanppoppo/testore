package com.tanppoppo.testore.testore.security;

import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.repository.MemberRepository;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticatedUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("로그인 시도 : {}", email);

        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException(email + " : 없는 ID입니다.");
                });

        if (!memberEntity.getStatus()) {
            throw new InternalAuthenticationServiceException("인증되지 않은 회원입니다.");
        }

        log.debug("조회정보 : {}", memberEntity);

        AuthenticatedUser user = AuthenticatedUser.builder()
                .id(memberEntity.getMemberId())
                .email(memberEntity.getEmail())
                .password(memberEntity.getMemberPassword())
                .nickname(memberEntity.getNickname())
                .status(memberEntity.getStatus())
                .build();
		
		log.debug("인증정보 : {}", user);
	
		return user;
	}

}
