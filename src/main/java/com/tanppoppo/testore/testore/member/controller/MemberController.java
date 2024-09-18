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
 *
 * @author gyahury
 * @version 0.1.2
 * @since 0.1.1
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
    public String loginForm(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) {
            switch (error) {
                case "unverified":
                    model.addAttribute("toastShown", true);
                    model.addAttribute("toastMessage", "계정이 인증되지 않았습니다.");
                    model.addAttribute("isSuccess", false);
                    break;
                case "badcredential":
                    model.addAttribute("toastShown", true);
                    model.addAttribute("toastMessage", "아이디 또는 비밀번호가<br>잘못되었습니다.");
                    model.addAttribute("isSuccess", false);
                    break;
                default:
                    model.addAttribute("toastShown", true);
                    model.addAttribute("toastMessage", "로그인에 실패했습니다.");
                    model.addAttribute("isSuccess", false);
            }
        }
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
    public String join(MemberDTO memberDTO, RedirectAttributes redirectAttributes) {

        try {
            ms.joinMember(memberDTO);
            redirectAttributes.addFlashAttribute("toastShown", true);
            redirectAttributes.addFlashAttribute("toastMessage", "인증 메일을 발송하였습니다.<br>인증을 완료해주세요.");
            redirectAttributes.addFlashAttribute("isSuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastShown", true);
            redirectAttributes.addFlashAttribute("toastMessage", "회원가입 중 문제가 발생했습니다.<br>다시 시도해주세요.");
            redirectAttributes.addFlashAttribute("isSuccess", false);
        }

        return "redirect:/";

    }

    /**
     * 이메일 인증을 처리
     * 이메일 인증 토큰을 확인하고 인증 성공 시 성공 페이지로,
     * 실패 시 에러 페이지로 리다이렉트
     *
     * @author dhkdtjs1541
     * @param token 이메일 인증 토큰
     * @param redirectAttributes 리다이렉트 요소를 담는 객체
     * @return 인증 성공 시 성공 페이지, 실패 시 에러 페이지로 리다이렉트
     */
    @GetMapping("verify-email")
    public String verify(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {

        try {
            boolean isVerified = ms.verifyEmail(token);
            log.info("Email verification result: {}", isVerified);
            if (isVerified) {
                redirectAttributes.addFlashAttribute("toastShown", true);
                redirectAttributes.addFlashAttribute("toastMessage", "인증이 완료되었습니다.");
                redirectAttributes.addFlashAttribute("isSuccess", true);
                return "redirect:/";
            } else {
                redirectAttributes.addFlashAttribute("toastShown", true);
                redirectAttributes.addFlashAttribute("toastMessage", "인증에 실패했습니다.");
                redirectAttributes.addFlashAttribute("isSuccess", true);
                return "redirect:/";
            }
        } catch (IllegalStateException e) {
            MemberEntity member = ms.findByEmailVerificationToken(token);
            redirectAttributes.addFlashAttribute("email", member.getEmail());
            redirectAttributes.addFlashAttribute("modalShown", true);
            redirectAttributes.addFlashAttribute("modalMessage", "토큰이 만료되었습니다.<br>인증 메일을 다시 보내시겠습니까?");
            redirectAttributes.addFlashAttribute("canCancel", true);
            redirectAttributes.addFlashAttribute("link", "/member/resend-verification?email="+member.getEmail());
            return "redirect:/member/loginForm";
        }

    }

    /**
     * 이메일 인증 재전송 처리 메서드
     * 만료된 인증 토큰에 대해 새로운 이메일 인증을 요청
     * @author dhkdtjs1541
     * @param email              재인증을 요청하는 이메일 주소
     * @param redirectAttributes 리다이렉트 메시지를 담느 모델 객체
     * @return 재인증 처리 후 성공 또는 실패 페이지 경로
     */
    @GetMapping("resend-verification")
    public String resendVerification(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {

        try {
            ms.resendVerificationEmail(email);
            redirectAttributes.addFlashAttribute("toastShown", true);
            redirectAttributes.addFlashAttribute("toastMessage", "재전송에 성공했습니다.");
            redirectAttributes.addFlashAttribute("isSuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastShown", true);
            redirectAttributes.addFlashAttribute("toastMessage", "재전송에 실패했습니다.");
            redirectAttributes.addFlashAttribute("isSuccess", false);
        }

        return "redirect:/member/loginForm";

    }

}
