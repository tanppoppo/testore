package com.tanppoppo.testore.testore.exam.controller;

import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.dto.ExamResultDTO;
import com.tanppoppo.testore.testore.exam.dto.QuestionParagraphDTO;
import com.tanppoppo.testore.testore.exam.service.ExamService;
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
        model.addAttribute("examPaperId", examPaperId);
        return "/exam/create-question-form";

    }

    @PostMapping("updateExamPaper")
    public String updateExamPaper(ExamPaperDTO examPaperDTO
            , @AuthenticationPrincipal AuthenticatedUser user
            , RedirectAttributes redirectAttributes) {

        try {
            es.updateExamPaper(examPaperDTO, user.getId());
            return "redirect:/exam/examPaperDetail?paper="+examPaperDTO.getExamPaperId();
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "공개된 시험지가 아닙니다.");
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "알 수 없는 오류가 발생했습니다.");
        }
        return "redirect:/";

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
            setFlashModalMessage(redirectAttributes, "시험 문제가 이미 존재합니다.<br>수정하시겠습니까?", true, "/exam/correctQuestionForm?paper="+examPaperId);
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
    public String createQuestion(HttpServletRequest request, @AuthenticationPrincipal AuthenticatedUser user) {

        Map<String, String[]> paragraphs = request.getParameterMap();

        es.createQuestion(paragraphs, user.getId());

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
     * @return 시험지 찾기 페이지를 반환합니다.
     * @author KIMGEON64
     */
    @GetMapping("search")
    public String search() {
        return "exam/exam-search";
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
    public String controlPublicOption(@RequestParam(name = "paper") int examPaperId){

        es.controlPublicOption(examPaperId);
        return"redirect:/exam/examPaperDetail?paper=" + examPaperId;

    }

    /**
     * 시험지 리뷰 페이지 이동
     * @author gyahury
     * @param examPaperId 시험지 id를 가져옵니다.
     * @return 시험지 리뷰 페이지를 반환합니다.
     */
    @GetMapping("review")
    public String review(@RequestParam(name = "paper") int examPaperId) {
        return "exam/exam-review";
    }

    /**
     * 시험지 리뷰 생성 페이지 이동
     * @author gyahury
     * @param examPaperId 시험지 id를 가져옵니다.
     * @return 시험지 리뷰 페이지를 반환합니다.
     */
    @GetMapping("createReviewForm")
    public String createReviewForm(@RequestParam(name = "paper") int examPaperId) {
        return "exam/create-review-form";
    }

}