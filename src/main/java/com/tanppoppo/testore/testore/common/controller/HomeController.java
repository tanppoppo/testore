package com.tanppoppo.testore.testore.common.controller;

import com.tanppoppo.testore.testore.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

   private final BoardService bs;

    @GetMapping({"", "/"})
    public String home(Model model) {
        // 최신 공지 5개 가져오기
        model.addAttribute("notices", bs.getRecentNotices());
        return "common/home";
    }
}
