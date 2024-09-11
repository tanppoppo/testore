package com.tanppoppo.testore.testore.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    @GetMapping("loginForm")
    public String loginForm() {
        return "member/loginForm";
    }

    @GetMapping("joinForm")
    public String joinForm() {
        return "member/joinForm";
    }
}
