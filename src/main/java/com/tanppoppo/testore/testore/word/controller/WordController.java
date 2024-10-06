package com.tanppoppo.testore.testore.word.controller;

import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import com.tanppoppo.testore.testore.word.dto.WordBookDTO;
import com.tanppoppo.testore.testore.word.service.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("word")
public class WordController {

    private final WordService ws;

    /**
     * 단어장 메인 페이지
     * @author MinCheolHa
     * @param model 회원 정보를 입력합니다.
     * @param user 인증된 회원 정보를 가져옵니다.
     * @return 단어장 메인 페이지를 반환합니다.
     */
    @GetMapping({"", "/"})
    public String wordMain(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        List<WordBookDTO> items = ws.getListItems(user);
        model.addAttribute("items", items);
        return "word/word-main";
    }

    /**
     * 단어장 생성 페이지
     * @author MinCheolHa
     * @param model 모델 객체를 가져옵니다.
     * @return 단어장 생성 페이지를 반환합니다.
     */
    @GetMapping("wordBookCreateForm")
    public String wordBookCreateForm(Model model) {

        String[] colors = {"red", "blue", "yellow", "brown", "green", "mint", "purple"};
        Random random = new Random();
        String randomColor = colors[random.nextInt(colors.length)];
        model.addAttribute("color", randomColor);

        return "word/wordbook-create-form";

    }

    /**
     * 단어장 생성
     * @author MinCheolHa
     * @param wordBookDTO 단어장 정보를 입력합니다.
     * @param user 사용자 인증정보를 가져옵니다.
     * @param model wordbookId 를 반환합니다.
     * @return 단어장 페이지를 반환합니다.
     */
    @PostMapping("createWordBook")
    public String createWordBook(WordBookDTO wordBookDTO
            , @AuthenticationPrincipal AuthenticatedUser user
            , Model model) {
        int wordbookId = ws.createWordBook(wordBookDTO, user);
        model.addAttribute("wordbookId", wordbookId);
        return "word/create-word-list";
    }

    /**
     * 단어장 상세 페이지 이동
     * @author MinCheolHa
     * @param model Map객체 detail을 반환합니다.
     * @param wordbookId 단어장 키값을 가져옵니다.
     * @return 단어장 상세 페이지를 반환합니다.
     */
    @GetMapping("detail")
    public String wordDetails(Model model, @RequestParam(name = "book") int wordbookId) {
        Map<String, Object> detail = ws.selectBookDetail(wordbookId);
        model.addAttribute("wordBookDTO", detail.get("wordBookDTO"));
        model.addAttribute("nickname", detail.get("nickName"));
        model.addAttribute("reviewCount", detail.get("reviewCount"));
        model.addAttribute("likeState", detail.get("likeState"));
        return "word/word-detail";
    }

    /**
     * 단어장 찾아보기 페이지 이동
     * @author gyahury
     * @return 단어장 상세 페이지를 반환합니다.
     */
    @GetMapping("search")
    public String search() {
        return "word/word-search";
    }

    /**
     * 단어장 학습 페이지
     * @author MinCheolHa
     * @return 단어장 학습 페이지를 반환합니다.
     */
    @GetMapping("learning")
    public String learning() { return "word/word-learning"; }

}
