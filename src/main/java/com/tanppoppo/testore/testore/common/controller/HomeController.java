package com.tanppoppo.testore.testore.common.controller;

import com.tanppoppo.testore.testore.board.service.BoardService;
import com.tanppoppo.testore.testore.common.util.BoardTypeEnum;
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

        model.addAttribute("notices", bs.getRecentNotices(BoardTypeEnum.NOTICE));
        model.addAttribute("reads", bs.getRecentNotices(BoardTypeEnum.READS));
        model.addAttribute("daily", bs.getRecentNotices(BoardTypeEnum.DAILY));
        model.addAttribute("learning_share", bs.getRecentNotices(BoardTypeEnum.LEARNING_SHARE));
        model.addAttribute("info_share", bs.getRecentNotices(BoardTypeEnum.INFO_SHARE));
        model.addAttribute("etc", bs.getRecentNotices(BoardTypeEnum.ETC));
        return "common/home";

    }
}
