package com.tanppoppo.testore.testore.exam.controller;

import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.dto.QuestionParagraphDTO;
import com.tanppoppo.testore.testore.exam.service.ExamService;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * @param model 시험지 정보를 뷰로 반환합니다.
     * @param user 인증된 회원 정보를 가져옵니다.
     * @return 시험지 메인 페이지를 반환합니다.
     * @author KIMGEON64
     */
    @GetMapping("")
    public String examMain(Model model, @AuthenticationPrincipal AuthenticatedUser user) {

        List<ExamPaperDTO> items = es.getListItems(user);
        model.addAttribute("items", items);
        return "exam/exam-main";

    }

    /**
     * 시험지 생성 페이지 이동
     * @return 시험지 생성 페이지를 반환합니다.
     * @author KIMGEON64
     */
    @GetMapping("createForm")
    public String createForm() {
        return "exam/exam-create";
    }

    /**
     * 시험지 생성 페이지 입력 정보를 저장
     * @param examPaperDTO 시험지 정보를 입력합니다.
     * @param user 사용자 인증정보를 가져옵니다.
     * @param model examPaperId 를 반환합니다.
     * @return 시험 문제 추가 페이지를 반환합니다.
     * @author KIMGEON64
     */
    @PostMapping("create")
    public String create(ExamPaperDTO examPaperDTO
            , @AuthenticationPrincipal AuthenticatedUser user
            , Model model) {

        int examPaperId = es.examCreate(examPaperDTO, user);
        model.addAttribute("examPaperId", examPaperId);
        return "redirect:create-question-form";

    }

    /* 문제 추가 페이지 주소값 */ // 진행중
    @GetMapping("createQuestionForm")
    public String createQuestionForm(/*
            @RequestParam(name = "examPaperId") int examPaperId,
            Model model */) {
//        model.addAttribute("examPaperId", examPaperId);
        return "exam/create-question-form";

    }

    /* ( Submit ) 시험지 문제 추가 페이지에서 시험 문제 최종 DB상 저장 */ // 진행중
    @PostMapping("createQuestion")
    public String QuestionsSave(/* @RequestParam(name = "examPaperId") int examPaperId */) {
//        es.QuestionsSave(examPaperId);
        return "exam-detail";
    }

    /**
     * 시험지 상세 페이지 이동
     * @param model Map객체 detail을 반환합니다.
     * @param examPaperId 시험지 키값을 가져옵니다.
     * @return 시험지 상세 페이지를 반환합니다.
     * @author KIMGEON64
     */
    @GetMapping("detail")
    public String paper(Model model, @RequestParam(name = "paper") int examPaperId) {

        Map<String, Object> detail = es.selectPaperDetail(examPaperId);
        model.addAttribute("examPaperDTO", detail.get("examPaperDTO"));
        model.addAttribute("nickname", detail.get("nickname"));
        model.addAttribute("reviewCount", detail.get("reviewCount"));
        model.addAttribute("likeState", detail.get("likeState"));
        return "exam/exam-detail";

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

    @GetMapping("examTake")
    public String examTake(Model model , @RequestParam(name = "paper") int examPaperId) {

        List<QuestionParagraphDTO> questionParagraphDTOS = es.selectQuestionParagraph(examPaperId);

        Map<Integer, List<QuestionParagraphDTO>> groupedParagraphDTOS = questionParagraphDTOS.stream()
                .sorted(Comparator.comparingInt(QuestionParagraphDTO::getParagraphOrder))
                .collect(Collectors.groupingBy(QuestionParagraphDTO::getExamQuestionId));

        model.addAttribute("items", groupedParagraphDTOS);

        return "exam/exam-take";
    }

}