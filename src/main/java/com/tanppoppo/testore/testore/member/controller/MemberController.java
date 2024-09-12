package com.tanppoppo.testore.testore.member.controller;

import com.tanppoppo.testore.testore.member.dto.MemberDTO;
import com.tanppoppo.testore.testore.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    private final MemberService ms;

    @GetMapping("loginForm")
    public String loginForm() {
        return "member/loginForm";
    }

    @GetMapping("joinForm")
    public String joinForm() {
        return "member/joinForm";
    }
    
    /* 회원가입 */
    @PostMapping("join")
    public String join(MemberDTO memberDTO){

        ms.joinMember(memberDTO);
        return"redirect:/";

    }

}
