package com.tanppoppo.testore.testore.member.controller;

import com.tanppoppo.testore.testore.member.dto.MemberDTO;
import com.tanppoppo.testore.testore.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 회원 관리를 위한 컨트롤러 클래스
 * @version 0.1.0
 * @since 0.1.0
 * @author gyahury
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    private final MemberService ms;

    /**
     * 로그인 페이지 이동
     * @author gyahury
     * @return 로그인 페이지를 반환합니다.
     */
    @GetMapping("loginForm")
    public String loginForm() {
        return "member/loginForm";
    }

    /**
     * 회원가입 페이지 이동
     * @author gyahury
     * @return 회원가입 페이지를 반환합니다.
     */
    @GetMapping("joinForm")
    public String joinForm() {
        return "member/joinForm";
    }

    /**
     * 회원 가입
     * @author KIMGEON64
     * @param memberDTO 회원 정보를 입력합니다.
     * @return 루트 리다이렉트를 반환합니다.
     */
    @PostMapping("join")
    public String join(MemberDTO memberDTO){

        ms.joinMember(memberDTO);
        return "redirect:/";

    }

}
