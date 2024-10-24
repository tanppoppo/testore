package com.tanppoppo.testore.testore.member.controller;

import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.service.ExamService;
import com.tanppoppo.testore.testore.member.dto.MemberDTO;
import com.tanppoppo.testore.testore.member.dto.NotificationDTO;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.service.MemberService;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import com.tanppoppo.testore.testore.word.dto.WordBookDTO;
import com.tanppoppo.testore.testore.word.service.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.tanppoppo.testore.testore.util.MessageUtil.*;

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
    private final ExamService es;
    private final WordService ws;

    /**
     * 계정 페이지 이동
     * @author gyahury
     * @return 계정 페이지를 반환합니다.
     */
    @GetMapping("info")
    public String info(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        Integer examPaperCount = es.countExamPapersByOwnerId(user.getId());
        Integer wordBookCount = ws.countWordBooksByOwnerId(user.getId());
        Integer pointCount = ms.getPointSumByMemberId(user.getId());

        model.addAttribute("email", user.getEmail());
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("examPaperCount", examPaperCount);
        model.addAttribute("wordBookCount", wordBookCount);
        model.addAttribute("pointCount", pointCount);

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
                    setModelToastMessage(model, false, "계정이 인증되지 않았습니다.");
                    break;
                case "badcredential":
                    setModelToastMessage(model, false, "아이디 또는 비밀번호가<br>잘못되었습니다.");
                    break;
                default:
                    setModelToastMessage(model, false, "로그인에 실패했습니다.");
                    break;
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
     * 시험 결과 페이지 이동
     * @author gyahury
     * @return 시험 결과 페이지를 반환합니다.
     */
    @GetMapping("examResult")
    public String examResult(Model model, @RequestParam(required = false) String keyword, @AuthenticationPrincipal AuthenticatedUser user) {

        model.addAttribute("resultItems", es.findExamResultByMemberId(user, keyword));
        model.addAttribute("keyword", keyword);

        return "member/exam-result";

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
            setFlashToastMessage(redirectAttributes, true, "인증 메일을 발송하였습니다.<br>인증을 완료해주세요.");
        } catch (IllegalStateException e) {
            setFlashToastMessage(redirectAttributes, false, "중복된 이메일입니다.");
            return "redirect:/member/joinForm";
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "회원가입 중 문제가 발생했습니다.<br>다시 시도해주세요.");
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
                setFlashToastMessage(redirectAttributes, true, "인증이 완료되었습니다.");
                return "redirect:/";
            } else {
                setFlashToastMessage(redirectAttributes, false, "인증에 실패했습니다.");
                return "redirect:/";
            }
        } catch (IllegalStateException e) {
            MemberEntity member = ms.findByEmailVerificationToken(token);
            setFlashModalMessage(redirectAttributes, "토큰이 만료되었습니다.<br>인증 메일을 다시 보내시겠습니까?", true, "/member/resend-verification?email="+member.getEmail());
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
            setFlashToastMessage(redirectAttributes, true, "재전송에 성공했습니다.");
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "재전송에 실패했습니다.");
        }

        return "redirect:/member/loginForm";

    }

    /**
     * 북마크 추가및 삭제 기능
     * @author KIMGEON64
     * @param examPaperId 시험지 아이디를 가져옵니다.
     * @param user 인증된 회원 정보를 가져옵니다.
     * @return 시험지 상세 페이지를 반환합니다.
     */
    @GetMapping("bookmarkExamPaper")
    public String bookmark(@RequestParam(name = "paper") Integer examPaperId, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal AuthenticatedUser user){

        ms.createAndDeleteBookmarkByMemberId(examPaperId,user);
        setFlashToastMessage(redirectAttributes, true, "요청 성공했습니다.");
        return"redirect:/exam/examPaperDetail?paper="+examPaperId;

    }

    /**
     * 좋아요 추가및 삭제 기능
     * @author KIMGEON64
     * @param examPaperId 시험지 아이디를 가져옵니다.
     * @param user 인증된 회원 정보를 가져옵니다.
     * @return 시험지 상세 페이지를 반환합니다.
     */
    @GetMapping("likeExamPaper")
    public String like(@RequestParam(name = "paper") Integer examPaperId, RedirectAttributes redirectAttributes,
                       @AuthenticationPrincipal AuthenticatedUser user){

        ms.createAndDeleteItemLikeByMemberId(examPaperId,user);

        setFlashToastMessage(redirectAttributes, true, "요청 성공했습니다.");
        return"redirect:/exam/examPaperDetail?paper="+examPaperId;

    }

    /**
     * 북마크 추가및 삭제 기능
     * @author MinCheolHa
     * @param wordBookId 시험지 아이디를 가져옵니다.
     * @param user 인증된 회원 정보를 가져옵니다.
     * @return 시험지 상세 페이지를 반환합니다.
     */
    @GetMapping("bookmarkWordBook")
    public String bookmarkWordBook(@RequestParam(name = "book") Integer wordBookId, RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal AuthenticatedUser user){

        ms.createAndDeleteWordBookBookmarkByMemberId(wordBookId,user);
        setFlashToastMessage(redirectAttributes, true, "요청 성공했습니다.");
        return"redirect:/word/wordBookDetail?book="+wordBookId;

    }

    /**
     * 단어장 좋아요 추가 및 삭제 기능
     * @author MinCheolHa
     * @param wordBookId 시험지 아이디를 가져옵니다.
     * @param user 인증된 회원 정보를 가져옵니다.
     * @return 시험지 상세 페이지를 반환합니다.
     */
    @GetMapping("likeWordBook")
    public String likeWordBook(@RequestParam(name = "book") Integer wordBookId, RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal AuthenticatedUser user){

        ms.createAndDeleteWordBookItemLikeByMemberId(wordBookId,user);

        setFlashToastMessage(redirectAttributes, true, "요청 성공했습니다.");
        return"redirect:/word/wordBookDetail?book="+wordBookId;

    }

    /**
     * 알림 리스트 조회
     * @author gyahury
     * @param user 유저 객체를 가져옵니다.
     * @param model 모델 객체를 가져옵니다.
     * @return 알림 페이지를 반환합니다.
     */
    @GetMapping("/notification")
    public String getNotifications(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        List<NotificationDTO> notifications = ms.getNotificationsAndMarkAsRead(user.getId());
        model.addAttribute("notifications", notifications);
        return "member/notification";

    }

    /**
     * 새 알림 체크
     * @author gyahury
     * @param user
     * @return 새 알림이 존재하는지를 반환합니다.
     */
    @GetMapping("/notification/check")
    public ResponseEntity<?> checkNotifications(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(ms.getUnreadNotification(user.getId()));
    }

    /**
     * 내가 좋아요한 단어장 조회
     * @author KIMGEON64
     * @param user 인증된 회원 정보를 전달합니다.
     * @param model 모델 객체를 전달합니다.
     * @param keyword 사용자 입력값을 전달합니다.
     * @return 내가 좋아요한 단어장 조회 페이지를 반환합니다.
     */
    @GetMapping("likedWordBook")
    public String likedWordBook(@AuthenticationPrincipal AuthenticatedUser user, Model model,
                                @RequestParam(required = false) String keyword){

        model.addAttribute("likeWordBookDTOList", ws.getLikedWordBook(user, keyword));
        model.addAttribute("keyword", keyword);

        return "member/word-liked";

    }

    /**
     * 내가 북마크한 단어장 조회
     * @author KIMGEON64
     * @param user 인증된 회원 정보를 전달합니다.
     * @param model 모델 객체를 전달합니다.
     * @param keyword 사용자 입력값을 전달합니다.
     * @return 내가 북마크한 단어장 조회 페이지를 반환합니다.
     */
    @GetMapping("bookmarkedWordBook")
    public String bookmarkedWordBook(@AuthenticationPrincipal AuthenticatedUser user, Model model,
                                     @RequestParam(required = false) String keyword){

        model.addAttribute("bookmarkedWordBookDTOList", ws.getBookmarkedWordBook(user, keyword));
        model.addAttribute("keyword", keyword);

        return "member/word-bookmarked";

    }

    /**
     * 내가 좋아요한 시험지 조회
     * @author KIMGEON64
     * @param user 인증된 회원 정보를 전달합니다.
     * @param model 모델 객체를 전달합니다.
     * @param keyword 사용자 입력값을 전달합니다.
     * @return 내가 좋아요한 시험지 조회 페이지를 반환합니다.
     */
    @GetMapping("likedExam")
    public String likedExam(@AuthenticationPrincipal AuthenticatedUser user, Model model,
                            @RequestParam(required = false) String keyword){

        model.addAttribute("likeExamPaperDTOList", es.getLikedExam(user, keyword));
        model.addAttribute("keyword", keyword);

        return "member/exam-liked";

    }

    /**
     * 내가 북마크한 시험지 조회
     * @author KIMGEON64
     * @param user user 인증된 회원 정보를 전달합니다.
     * @param model 모델 객체를 전달합니다.
     * @param keyword 사용자 입력값을 전달합니다.
     * @return 내가 좋아요한 시험지 조회 페이지를 반환합니다.
     */
    @GetMapping("bookmarkedExam")
    public String bookmarkedExam(@AuthenticationPrincipal AuthenticatedUser user, Model model,
                                 @RequestParam(required = false) String keyword){

        model.addAttribute("bookmarkedExamPaperDTOList", es.getBookmarkedExam(user, keyword));
        model.addAttribute("keyword", keyword);

        return "member/exam-bookmarked";

    }

}
