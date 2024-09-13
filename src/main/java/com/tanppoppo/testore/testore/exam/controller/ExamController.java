package com.tanppoppo.testore.testore.exam.controller;

import com.tanppoppo.testore.testore.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("exam")
public class ExamController {

    private final ExamService es;

    /* 시험 기능 메인 페이지 이동 */
    @GetMapping("")
    public String examMain(){
        return"exam/exam-main";
    }

    /* 시험지 생성 페이지 이동 */
    @GetMapping("createForm")
    public String createForm(){
        return"exam/exam-create";
    }

}
