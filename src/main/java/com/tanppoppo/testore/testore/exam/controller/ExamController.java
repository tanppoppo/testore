package com.tanppoppo.testore.testore.exam.controller;

import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.service.ExamService;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
     * @return 시험지 메인 페이지를 반환합니다.
     */
    @GetMapping("")
    public String examMain(Model model) {
        
        List<ExamPaperDTO> items = es.getListItems();
        model.addAttribute("items", items);
        return "exam/exam-main";
        
    }

    /**
     * 시험지 생성 페이지 이동
     * @author KIMGEON64
     * @return 시험지 생성 페이지를 반환합니다.
     */
    @GetMapping("createForm")
    public String createForm() {
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
    @PostMapping("create")
    public String create(ExamPaperDTO examPaperDTO
            , @AuthenticationPrincipal AuthenticatedUser user
            , Model model) {

        int examPaperId = es.examCreate(examPaperDTO, user);
        model.addAttribute("examPaperId", examPaperId);
        return "redirect:create-question-form";

    }

}
