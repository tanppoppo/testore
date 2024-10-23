package com.tanppoppo.testore.testore.exam.controller;

import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.dto.ExamResultDTO;
import com.tanppoppo.testore.testore.exam.dto.QuestionParagraphDTO;
import com.tanppoppo.testore.testore.exam.service.ExamService;
import com.tanppoppo.testore.testore.member.dto.ReviewDTO;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.tanppoppo.testore.testore.util.MessageUtil.*;

/**
 * 시험지 관리를 위한 컨트롤러 클래스
 * @version 0.1.0
 * @since 0.1.0
 * @author KIMGEON64
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("exam")
public class ExamController {

    private final ExamService es;

    /**
     * 시험지 메인 페이지 이동
     * @author KIMGEON64
     * @param model 시험지 정보를 뷰로 반환합니다.
     * @param user 인증된 회원 정보를 가져옵니다.
     * @return 시험지 메인 페이지를 반환합니다.
     */
    @GetMapping("")
    public String examMain(Model model, @AuthenticationPrincipal AuthenticatedUser user) {

        List<ExamPaperDTO> items = es.getListItems(user);
        model.addAttribute("items", items);
        return "exam/exam-main";

    }

    /**
     * 시험지 생성 페이지 이동
     * @author KIMGEON64
     * @return 시험지 생성 페이지를 반환합니다.
     */
    @GetMapping("createExamPaperForm")
    public String createExamPaperForm() {
        return "exam/exam-create";
    }

    /**
     * 시험지 수정 페이지 이동
     * @author gyahury
     * @return 시험지 생성 페이지를 반환합니다.
     */
    @GetMapping("updateExamPaperForm")
    public String updateExamPaperForm(@RequestParam(name = "paper") int examPaperId,
                             @AuthenticationPrincipal AuthenticatedUser user,
                             Model model) {

        ExamResultDTO examResultDTO = es.selectUpdatedPaperInfo(examPaperId, user.getId());

        model.addAttribute("examPaperId", examResultDTO.getExamPaperId());
        model.addAttribute("examPaperTitle", examResultDTO.getExamPaperTitle());
        model.addAttribute("examPaperContent", examResultDTO.getExamPaperContent());
        model.addAttribute("examPaperPassScore", examResultDTO.getExamPaperPassScore());

        return "exam/exam-create";
    }

    /**
     * 시험지 생성 페이지 입력 정보를 저장
     * @author KIMGEON64
     * @param examPaperDTO 시험지 정보를 입력합니다.
     * @param user 사용자 인증정보를 가져옵니다.
     * @param model examPaperId 를 반환합니다.
     * @return 시험 문제 추가 페이지를 반환합니다.
     */
    @PostMapping("createExamPaper")
    public String createExamPaper(ExamPaperDTO examPaperDTO
            , @AuthenticationPrincipal AuthenticatedUser user
            , Model model) {

        int examPaperId = es.examCreate(examPaperDTO, user);
        setModelToastMessage(model, true, "시험지가 생성되었습니다.");
        model.addAttribute("examPaperId", examPaperId);
        return "/exam/create-question-form";

    }

    /**
     * 시험지 수정
     * @author gyahury
     * @param examPaperDTO 시험지 전달 객체를 가져옵니다.
     * @param examPaperId 시험지 ID를 가져옵니다.
     * @param user 유저 객체를 가져옵니다.
     * @param redirectAttributes 리다이렉트 객체를 가져옵니다.
     * @return 메인 페이지로 리다이렉트 합니다.
     */
    @PostMapping("updateExamPaper")
    public String updateExamPaper(ExamPaperDTO examPaperDTO, @RequestParam(name = "paper") int examPaperId
            , @AuthenticationPrincipal AuthenticatedUser user
            , RedirectAttributes redirectAttributes) {

        try {
            examPaperDTO.setExamPaperId(examPaperId);
            es.updateExamPaper(examPaperDTO, user.getId());
            setFlashToastMessage(redirectAttributes, true, "수정되었습니다.");
            return "redirect:/exam/examPaperDetail?paper="+examPaperDTO.getExamPaperId();
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "알 수 없는 오류가 발생했습니다.");
        }
        return "redirect:/";

    }

    /**
     * 시험지 삭제
     * @author gyahury
     * @param examPaperId 시험지 id를 가져옵니다.
     * @param user 유저 객체를 가져옵니다.
     * @param redirectAttributes 리다이렉트 객체를 가져옵니다.
     * @return 시험지 페이지로 이동합니다.
     */
    @GetMapping("deleteExamPaper")
    public String deleteExamPaper(@RequestParam(name = "paper") int examPaperId
            , @AuthenticationPrincipal AuthenticatedUser user
            , RedirectAttributes redirectAttributes) {

        try {
            es.deleteExamPaper(examPaperId, user.getId());
            setFlashToastMessage(redirectAttributes, true, "삭제되었습니다.");
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "알 수 없는 오류가 발생했습니다.");
        }

        return "redirect:/exam";
    }

    /**
     * 시험 문제 생성 폼 이동
     * @author gyahury
     * @param examPaperId 시험지 아이디를 가져옵니다.
     * @param model 모델 객체를 가져옵니다.
     * @return 시험 문제가 존재하는지 확인 후 존재하면 수정 폼으로 이동, 존재하지 않으면 시험 문제 생성 폼을 반환합니다.
     */
    @GetMapping("createQuestionForm")
    public String createQuestionForm(@RequestParam(name = "paper") int examPaperId, RedirectAttributes redirectAttributes, Model model, @AuthenticationPrincipal AuthenticatedUser user) {

        if(es.checkQuestionExist(examPaperId, user)) {
            setFlashToastMessage(redirectAttributes, false,"문제가 이미 존재합니다.");
            return "redirect:/";
        }

        if(!es.verifyUserIsExamOwner(examPaperId, user.getId())) {
            setFlashToastMessage(redirectAttributes, false,"시험지 소유자가 아닙니다.");
            return "redirect:/";
        }

        model.addAttribute("examPaperId", examPaperId);
        return "exam/create-question-form";

    }

    /**
     * 시험 문제 생성
     * @author gyahury
     * @param request 요청 객체를 가져옵니다.
     * @return 홈화면으로 리다이렉트합니다.
     */
    @PostMapping("createQuestion")
    public String createQuestion(HttpServletRequest request, @AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes) {

        Map<String, String[]> paragraphs = request.getParameterMap();

        es.createQuestion(paragraphs, user.getId());

        setFlashToastMessage(redirectAttributes, true, "시험 문제가 생성되었습니다.");

        return "redirect:/";

    }

    /**
     * 시험지 상세 페이지 이동
     * @author KIMGEON64
     * @param model Map객체 detail을 반환합니다.
     * @param examPaperId 시험지 키값을 가져옵니다.
     * @return 시험지 상세 페이지를 반환합니다.
     */
    @GetMapping("examPaperDetail")
    public String ExamPaperDetail(Model model, RedirectAttributes redirectAttributes, @RequestParam(name = "paper") int examPaperId, @AuthenticationPrincipal AuthenticatedUser user) {

        try {
            Map<String, Object> detail = es.selectPaperDetail(examPaperId, user);
            model.addAttribute("examPaperDTO", detail.get("examPaperDTO"));
            model.addAttribute("nickname", detail.get("nickname"));
            model.addAttribute("reviewCount", detail.get("reviewCount"));
            model.addAttribute("likeState", detail.get("likeState"));
            model.addAttribute("bookmarkState", detail.get("bookmarkState"));
            return "exam/exam-detail";
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "공개된 시험지가 아닙니다.");
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "알 수 없는 오류가 발생했습니다.");
        }

        return "redirect:/";

    }

    /**
     * 시험지 찾기 페이지 이동
     * @author KIMGEON64
     * @param model 모델 객체를 전달합니다.
     * @param user user 객체를 전달합니다.
     * @return 시험지 찾아보기 페이지를 반환합니다.
     */
    @GetMapping("search")
    public String search(Model model, @AuthenticationPrincipal AuthenticatedUser user) {

        List<ExamPaperDTO> recommendedExamPaper = es.recommendedExamPaper(user);
        List<ExamPaperDTO> likedExamPaper = es.likedExamPaper(user);
        List<ExamPaperDTO> muchSharedExamPaper = es.muchSharedExamPaper(user);
        model.addAttribute("recommendedExamPaper", recommendedExamPaper);
        model.addAttribute("likedExamPaper", likedExamPaper);
        model.addAttribute("muchSharedExamPaper", muchSharedExamPaper);

        return "exam/exam-search";

    }

    /**
     * 시험지 찾기 기능
     * @author KIMGEON64
     * @param model 모델 객체를 전달합니다.
     * @param keyword 사용자 입력 값을 전달합니다.
     * @param user user 객체를 전달합니다.
     * @return 찾은 시험지 페이지를 반환합니다.
     */
    @GetMapping("examSearch")
    public String searchExam(Model model, @RequestParam(required = false) String keyword, @AuthenticationPrincipal AuthenticatedUser user){

        List<ExamPaperDTO> examPaperDTOList = es.findExamPaperByMemberId(user, keyword);
        model.addAttribute("items", examPaperDTOList);
        model.addAttribute("keyword", keyword);
        return "exam/exam-search-keyword";

    }

    /**
     * 시험 응시 페이지 이동
     * @author gyahury
     * @param model 모델 객체를 전달합니다.
     * @param redirectAttributes 리다이렉트 객체를 전달합니다.
     * @param examPaperId 시험지 id를 전달합니다.
     * @param user user 객체를 전달합니다.
     * @return 성공시 시험 응시 페이지, 실패시 메인 화면으로 리다이렉트 됩니다.
     */
    @GetMapping("examTake")
    public String examTake(Model model, RedirectAttributes redirectAttributes, @RequestParam(name = "paper") int examPaperId, @AuthenticationPrincipal AuthenticatedUser user) {

        try {
            Map<Integer, List<QuestionParagraphDTO>> questionParagraphDTOS = es.selectQuestionParagraph(examPaperId, user);
            int examResultId = es.startExam(examPaperId, user);
            model.addAttribute("items", questionParagraphDTOS);
            model.addAttribute("examPaperId", examPaperId);
            model.addAttribute("examResultId", examResultId);
            setModelToastMessage(model, true, "시험을 시작합니다.");
            return "/exam/exam-take";
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "공개된 시험지가 아닙니다.");
        } catch (NoSuchElementException e) {
            setFlashModalMessage(redirectAttributes, "시험문제가 없습니다.<br>추가하시겠습니까?", true, "/exam/createQuestionForm?paper="+examPaperId);
        }

        return "redirect:/";

    }

    /**
     * 시험 제출
     * @author gyahury
     * @param request 요청 객체를 가져옵니다.
     * @param redirectAttributes 리다이렉트 객체를 가져옵니다.
     * @param user 유저 객체를 가져옵니다.
     * @return 성공시 루트 페이지로 리다이렉트 됩니다.
     */
    @PostMapping("submitExam")
    public String submitExam(HttpServletRequest request, RedirectAttributes redirectAttributes, @AuthenticationPrincipal AuthenticatedUser user) {

        Map<String, String[]> choices = request.getParameterMap();
        int score = es.calculateScore(choices);
        es.endExam(Integer.parseInt(choices.get("result")[0]), score, user);
        setFlashToastMessage(redirectAttributes, true, "제출되었습니다.");
        return "redirect:/";

    }

    /**
     * 공개여부 설정
     * @author KIMGEON64
     * @param examPaperId 시험지 키값을 가져옵니다.
     * @return 시험지 상세 페이지를 반환합니다.
     */
    @GetMapping("controlPublicOption")
    public String controlPublicOption(@RequestParam(name = "paper") int examPaperId, RedirectAttributes redirectAttributes){

        es.controlPublicOption(examPaperId);
        setFlashToastMessage(redirectAttributes, true, "요청 성공했습니다.");

        return "redirect:/exam/examPaperDetail?paper=" + examPaperId;

    }

    /**
     * 리뷰 목록 페이지 이동
     * @author KIMGEON64
     * @param model 모델 객체를 전달합니다.
     * @param user 인증된 회원 정보를 전달합니다.
     * @param examPaperId 시험지 ID를 전달합니다.
     * @return 시험지 리뷰 목록 페이지를 반환합니다.
     */
    @GetMapping("review")
    public String reviewForm(@AuthenticationPrincipal AuthenticatedUser user,
                             @RequestParam(name = "paper") int examPaperId, Model model){

        Map<String, Object> review = es.getListReviews(user, examPaperId);
        model.addAttribute("reviewDTOList", review.get("reviewDTOList"));

        return "exam/exam-review";

    }

    /**
     * 리뷰 생성 페이지 이동
     * @author KIMGEON64
     * @return 리뷰 생성 페이지를 반환합니다.
     */
    @GetMapping("createReviewForm")
    public String createReviewForm(@RequestParam(name = "paper") int examPaperId, Model model){

        model.addAttribute("examPaperId", examPaperId);
        return "exam/create-review-form";

    }

    /**
     * 리뷰 저장
     * @author KIMGEON64
     * @param user 인증된 회원 정보를 전달합니다.
     * @param examPaperId 시험지 ID를 전달합니다.
     * @return 시험지 리뷰 목록 페이지를 반환합니다.
     */
    @PostMapping("createReview")
    public String createReview(@AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes,
                               @RequestParam(name = "paper") int examPaperId, ReviewDTO reviewDTO){

        es.createReview(user, examPaperId, reviewDTO);
        setFlashToastMessage(redirectAttributes, true, "리뷰가 저장되었습니다.");

        return "redirect:/exam/examPaperDetail?paper=" + examPaperId;
    }

    /**
     * 리뷰 수정 페이지 이동
     * @author KIMGEON64
     * @param user 인증된 회원 정보를 전달합니다.
     * @param reviewId 리뷰 ID를 전달합니다.
     * @param model 모델 객체를 전달합니다.
     * @return 시험지 리뷰 목록 페이지를 반환합니다.
     */
    @GetMapping("updateReviewForm")
    public String updateReviewForm(@AuthenticationPrincipal AuthenticatedUser user,
                                   @RequestParam(name = "review") int reviewId, Model model){

        ReviewDTO reviewDTO = es.selectUpdatedReviewInfo(user, reviewId);
        model.addAttribute("reviewDTO", reviewDTO);

        return "exam/create-review-form";

    }

    /**
     * 리뷰 수정
     * @author KIMGEON64
     * @param user 인증된 회원 정보를 전달합니다.
     * @param reviewId 리뷰 ID를 전달합니다.
     * @return 시험지 리뷰 목록 페이지를 반환합니다.
     */
    @PostMapping("updateReview")
    public String updateReview(@AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes,
                               @RequestParam(name = "review") int reviewId,
                               @RequestParam(name = "paper") int examPaperId,
                               ReviewDTO reviewDTO){

        es.updateReview(user, reviewId, reviewDTO);
        setFlashToastMessage(redirectAttributes, true, "리뷰가 수정되었습니다.");

        return "redirect:/exam/review?paper=" + examPaperId;

    }

    /**
     * 리뷰 삭제
     * @author KIMGEON64
     * @param reviewId 리뷰 ID를 전달합니다.
     * @return 시험지 리뷰 목록 페이지를 반환합니다.
     */
    @GetMapping("deleteReview")
    public String deleteReview(@AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes
                               ,@RequestParam(name = "review") int reviewId){

        Integer examPaperId = 0;
        try {
            examPaperId = es.deleteReview(user, reviewId);
            setFlashToastMessage(redirectAttributes, true, "리뷰가 삭제되었습니다.");
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
            return "redirect:/";
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "알 수 없는 에러가 발생했습니다.");
            return "redirect:/";
        }

        return "redirect:/exam/review?paper=" + examPaperId;

    }

    /**
     * 시험지 응시자 내역 조회 페이지 이동
     * @author KIMGEON64
     * @param examPaperId 시험지 ID를 전달합니다.
     * @param user 인증된 회원 정보를 전달합니다.
     * @return 시험지 응시내역 페이지를 반환합니다.
     */
    @GetMapping("examHistory")
    public String examHistory(@RequestParam(name = "paper") Integer examPaperId, @AuthenticationPrincipal AuthenticatedUser user, Model model){

        List<ExamResultDTO> examResultDTOList = es.selectExamHistory(examPaperId, user);
        model.addAttribute("items", examResultDTOList);

        return "exam/exam-take-history";

    }

}