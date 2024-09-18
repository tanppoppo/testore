package com.tanppoppo.testore.testore.member.controller;

import com.tanppoppo.testore.testore.member.dto.MemberDTO;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Member;

/**
 * 회원 관리를 위한 컨트롤러 클래스
 * @version 0.1.1
 * @since 0.1.1
 * @author gyahury
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    private final MemberService ms;

    /**
     * 계정 페이지 이동
     * @author gyahury
     * @return 계정 페이지를 반환합니다.
     */
    @GetMapping("info")
    public String info(Model model) {
//        MemberEntity loggedInMember = ms.getLoggedInMember();
//        model.addAttribute("email", loggedInMember.getEmail());

        return "member/info-main";
    }

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
     * 알림 페이지 이동
     * @author dhkdtjs1541
     * @return 알림 페이지를 반환합니다.
     */
    @GetMapping("notification")
    public String notification() {
        return "member/notification";
    }

    /**
     * 회원 가입
     * @author KIMGEON64
     * @param memberDTO 회원 정보를 입력합니다.
     * @return 루트 리다이렉트를 반환합니다.
     */
    @PostMapping("join")
    public String join(MemberDTO memberDTO, RedirectAttributes redirectAttributes){

        ms.joinMember(memberDTO);
        redirectAttributes.addFlashAttribute("toastShown",true);
        redirectAttributes.addFlashAttribute("toastMessage","이메일 인증을 완료해주세요.");
        redirectAttributes.addFlashAttribute("isSuccess",true);
        return "redirect:/";

    }

    /**
     * 이메일 인증을 처리
     * 이메일 인증 토큰을 확인하고 인증 성공 시 성공 페이지로,
     * 실패 시 에러 페이지로 리다이렉트
     *
     * @author dhkdtjs1541
     * @param token 이메일 인증 토큰
     * @param model 이메일을 담는 모델 객체
     * @return 인증 성공 시 성공 페이지, 실패 시 에러 페이지로 리다이렉트
     */
    @GetMapping("verify-email")
    public String verify(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {

        log.info("Verify token: {}", token);
        try {
            boolean isVerified = ms.verifyEmail(token);
            log.info("Email verification result: {}", isVerified);
            if (isVerified) {
                redirectAttributes.addFlashAttribute("toastShown",true);
                redirectAttributes.addFlashAttribute("toastMessage","인증이 완료되었습니다.");
                redirectAttributes.addFlashAttribute("isSuccess",true);
                return "redirect:/";
            } else {
                redirectAttributes.addFlashAttribute("toastShown",true);
                redirectAttributes.addFlashAttribute("toastMessage","인증에 실패했습니다.");
                redirectAttributes.addFlashAttribute("isSuccess",true);
                return "redirect:/";
            }
        } catch (IllegalStateException e) {
            log.error("토큰이 만료되었습니다 : {}", e.getMessage());
            MemberEntity member = ms.findByEmailVerificationToken(token);
            model.addAttribute("email", member.getEmail());
            return "redirect:/member/join";
        }

    }

    /**
     * 이메일 인증 재전송 처리 메서드
     * 만료된 인증 토큰에 대해 새로운 이메일 인증을 요청
     *
     * @author dhkdtjs1541
     * @param email 재인증을 요청하는 이메일 주소
     * @param model 오류 메시지를 담느 모델 객체
     * @return 재인증 처리 후 성공 또는 실패 페이지 경로
     */
    @PostMapping("resend-verification")
    public String resendVerification(@RequestParam("email") String email, Model model) {

        try {
            MemberEntity member = ms.findByEmail(email);

            if (member != null && !member.getStatus()) {
                String newToken = ms.generateNewEmailVerificationToken(member);
                ms.sendEmailVerification(member.getEmail(), newToken);
                return "redirect:/";
            } else {
                throw new IllegalArgumentException("이메일 주소가 잘못되었거나 이미 인증된 이메일입니다.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("이메일 재인증 오류 : {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("email", email);
            return "redirect:/";
        }

    }

}
