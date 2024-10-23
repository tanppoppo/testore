package com.tanppoppo.testore.testore.word.controller;

import com.tanppoppo.testore.testore.member.dto.ReviewDTO;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import com.tanppoppo.testore.testore.word.dto.WordBookDTO;
import com.tanppoppo.testore.testore.word.dto.WordDTO;
import com.tanppoppo.testore.testore.word.service.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.tanppoppo.testore.testore.util.MessageUtil.*;

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
     * @return 단어장 페이지를 반환합니다.
     */
    @PostMapping("createWordBook")
    public String createWordBook(WordBookDTO wordBookDTO, RedirectAttributes redirectAttributes
            , @AuthenticationPrincipal AuthenticatedUser user) {

        int wordbookId = ws.createWordBook(wordBookDTO, user);
        setFlashToastMessage(redirectAttributes, true, "단어장을 생성했습니다.");

        return "redirect:/word/wordAddForm?book="+wordbookId;

    }

    /**
     * 단어 추가 페이지
     * @author gyahury
     * @return 단어장 생성 페이지를 반환합니다.
     */
    @GetMapping("wordAddForm")
    public String wordAddForm(Model model, @AuthenticationPrincipal AuthenticatedUser user,
                              @RequestParam(name = "book") int wordBookId) {

        Integer nextWordNum = ws.checkWordNum(wordBookId, user.getId());
        model.addAttribute("wordBookId", wordBookId);
        model.addAttribute("nextWordNum", nextWordNum);

        return "word/word-add-form";

    }

    /**
     * 단어 추가
     * @author MinCheolHa
     * @param wordBookId
     * @param user
     * @param wordDTO
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/addWords")
    public String addWords(
            @RequestParam(name = "book") int wordBookId,
            @AuthenticationPrincipal AuthenticatedUser user,
            WordDTO wordDTO, RedirectAttributes redirectAttributes) {

        Integer nextWordNum = ws.addWords(wordDTO, wordBookId, user.getId());
        setFlashToastMessage(redirectAttributes, true, "성공적으로 추가했습니다.");
        redirectAttributes.addFlashAttribute("nextWordNum", nextWordNum);

        return "redirect:/word/wordAddForm?book=" + wordBookId;

    }


    /**
     * 단어장 상세 페이지 이동
     * @author MinCheolHa
     * @param model Map객체 detail을 반환합니다.
     * @param wordBookId 단어장 키값을 가져옵니다.
     * @return 단어장 상세 페이지를 반환합니다.
     */
    @GetMapping("wordBookDetail")
    public String wordBookDetail(Model model, RedirectAttributes redirectAttributes,
                                 @RequestParam(name = "book") int wordBookId, @AuthenticationPrincipal AuthenticatedUser user) {

        try{
            Map<String, Object> detail = ws.selectWordBookDetail(wordBookId, user);
            model.addAttribute("wordBookDTO", detail.get("wordBookDTO"));
            model.addAttribute("nickname", detail.get("nickname"));
            model.addAttribute("reviewCount", detail.get("reviewCount"));
            model.addAttribute("likeState", detail.get("likeState"));
            model.addAttribute("bookmarkState", detail.get("bookmarkState"));
            return "word/word-detail";
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "공개된 단어장이 아닙니다.");
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "알 수 없는 오류가 발생했습니다.");
        }
        return "redirect:/";
        
    }

    /**
     * 단어장 학습 페이지
     * @author MinCheolHa
     * @return 단어장 학습 페이지를 반환합니다.
     */
    @GetMapping("learning")
    public String learning(@RequestParam(name = "book") int wordBookId, Model model, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal AuthenticatedUser user) {

        try {
            List<WordDTO> wordList = ws.selectWordList(wordBookId, user.getId());
            model.addAttribute("wordBookId", wordBookId);
            model.addAttribute("items", wordList);
            return "word/word-learning";
        } catch (IllegalStateException e) {
            setFlashModalMessage(redirectAttributes, "단어가 없습니다.<br>추가하시겠습니까?", true, "/word/wordAddForm?book="+wordBookId);
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "알 수 없는 오류가 발생했습니다.");
        }
        return "redirect:/";

    }

    /**
     * 단어장 찾아보기 페이지 이동
     * @author gyahury
     * @param model 모델 객체를 전달합니다.
     * @param user user 객체를 전달합니다.
     * @return 단어장 상세 페이지를 반환합니다.
     */
    @GetMapping("search")
    public String search(Model model, @AuthenticationPrincipal AuthenticatedUser user) {

        List<WordBookDTO> recommendedWordBook = ws.recommendedWordBook(user);
        List<WordBookDTO> likedWordBook = ws.likedWordBook(user);
        List<WordBookDTO> muchSharedWordBook = ws.muchSharedWordBook(user);
        model.addAttribute("recommendedWordBook", recommendedWordBook);
        model.addAttribute("likedWordBook", likedWordBook);
        model.addAttribute("muchSharedWordBook", muchSharedWordBook);

        return "word/word-search";

    }

    /**
     * 단어장 찾기 기능
     * @author KIMGEON64
     * @param model 모델 객체를 전달합니다.
     * @param keyword 사용자 입력 값을 전달합니다.
     * @param user user 객체를 전달합니다.
     * @return 찾은 시험지 페이지를 반환합니다.
     */
    @GetMapping("wordBookSearch")
    public String wordBookSearch(Model model, @RequestParam(required = false) String keyword, @AuthenticationPrincipal AuthenticatedUser user){

        List<WordBookDTO> wordBookDTOList = ws.findWordBookByMemberId(user, keyword);
        model.addAttribute("items", wordBookDTOList);
        model.addAttribute("keyword", keyword);

        return "word/word-search-keyword";

    }

    /**
     * 단어장 공개여부 페이지 이동
     * @author MinCheolHa
     * @return 단어장 상세 페이지를 반환합니다.
     */
    @GetMapping("updatePublicOption")
    public String updatePublicOption(@RequestParam(name = "book") int wordBookId, @AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes){

        try {
            ws.updatePublicOptionByMemberId(wordBookId, user);
            setFlashToastMessage(redirectAttributes, true, "요청 성공했습니다.");
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
        }

        return"redirect:/word/wordBookDetail?book="+wordBookId;

    }

    /**
     * 리뷰 목록 페이지 이동
     * @author MinCheolHa
     * @param model 모델 객체를 전달합니다.
     * @param user 인증된 회원 정보를 전달합니다.
     * @param wordBookId 단어장 ID를 전달합니다.
     * @return 시험지 리뷰 목록 페이지를 반환합니다.
     */
    @GetMapping("review")
    public String reviewForm(@AuthenticationPrincipal AuthenticatedUser user,
                             @RequestParam(name = "book") int wordBookId, Model model){

        Map<String, Object> review = ws.getListReviews(user, wordBookId);
        model.addAttribute("reviewDTOList", review.get("reviewDTOList"));

        return "word/word-review";

    }

    /**
     * 리뷰 생성 페이지 이동
     * @author MinCheolHa
     * @return 리뷰 생성 페이지를 반환합니다.
     */
    @GetMapping("createReviewForm")
    public String createReviewForm(@RequestParam(name = "book") int wordBookId, Model model){

        model.addAttribute("wordBookId", wordBookId);
        return "word/create-review-form";

    }

    /**
     * 리뷰 저장
     * @author MinCheolHa
     * @param user 인증된 회원 정보를 전달합니다.
     * @param wordBookId 단어장 ID를 전달합니다.
     * @return 시험지 리뷰 목록 페이지를 반환합니다.
     */
    @PostMapping("createReview")
    public String createReview(@AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes,
                               @RequestParam(name = "book") int wordBookId, ReviewDTO reviewDTO){

        ws.createReview(user, wordBookId, reviewDTO);
        setFlashToastMessage(redirectAttributes, true, "리뷰가 저장되었습니다.");

        return "redirect:/word/wordBookDetail?book=" + wordBookId;

    }

    /**
     * 리뷰 수정 페이지 이동
     * @author MinCheolHa
     * @param user 인증된 회원 정보를 전달합니다.
     * @param reviewId 리뷰 ID를 전달합니다.
     * @param model 모델 객체를 전달합니다.
     * @return 시험지 리뷰 목록 페이지를 반환합니다.
     */
    @GetMapping("updateReviewForm")
    public String updateReviewForm(@AuthenticationPrincipal AuthenticatedUser user,
                                   @RequestParam(name = "review") int reviewId, Model model){

        ReviewDTO reviewDTO = ws.selectUpdatedReviewInfo(user, reviewId);
        model.addAttribute("reviewDTO", reviewDTO);

        return "word/create-review-form";

    }

    /**
     * 리뷰 수정
     * @author MinCheolHa
     * @param user 인증된 회원 정보를 전달합니다.
     * @param reviewId 리뷰 ID를 전달합니다.
     * @return 시험지 리뷰 목록 페이지를 반환합니다.
     */
    @PostMapping("updateReview")
    public String updateReview(@AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes,
                               @RequestParam(name = "review") int reviewId,
                               @RequestParam(name = "book") int wordBookId,
                               ReviewDTO reviewDTO){

        ws.updateReview(user, reviewId, reviewDTO);
        setFlashToastMessage(redirectAttributes, true, "리뷰가 수정되었습니다.");

        return "redirect:/word/review?book=" + wordBookId;

    }

    /**
     * 리뷰 삭제
     * @author MinCheolHa
     * @param reviewId 리뷰 ID를 전달합니다.
     * @return 시험지 리뷰 목록 페이지를 반환합니다.
     */
    @GetMapping("deleteReview")
    public String deleteReview(@AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes,
                               @RequestParam(name = "review") int reviewId){
        try {
            setFlashToastMessage(redirectAttributes, true, "리뷰가 삭제되었습니다.");
            return "redirect:/word/review?book=" + ws.deleteReview(user, reviewId);
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
            return "redirect:/";
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "알 수 없는 에러가 발생했습니다.");
            return "redirect:/";
        }

    }

    /**
     * 단어장 수정 페이지 이동
     * @author KIMGEON64
     * @param wordbookId 단어장 키값을 전달합니다.
     * @param user user 객체를 전달합니다.
     * @param model 모델 객체를 전달합니다.
     * @return 단어장 생성 페이지를 반환합니다.
     */
    @GetMapping("updateWordBookForm")
    public String updateWordBookForm(@RequestParam(name = "book") int wordbookId,
                                     @AuthenticationPrincipal AuthenticatedUser user,
                                     Model model){

        WordBookDTO wordBookDTO = ws.selectUpdatedWordbookInfo(wordbookId, user);

        model.addAttribute("wordBookId", wordBookDTO.getWordBookId());
        model.addAttribute("imagePath", wordBookDTO.getImagePath());
        model.addAttribute("title", wordBookDTO.getTitle());
        model.addAttribute("content", wordBookDTO.getContent());

        return "word/wordbook-create-form";

    }

    /**
     * 단어장 수정 페이지 입력 정보를 저장
     * @author KIMGEON64
     * @param user user 객체를 전달합니다.
     * @param wordBookDTO 단어장 표제값을 전달합니다.
     * @param redirectAttributes 오류 메세지를 전달합니다.
     * @return 단어장 상세 페이지로 이동합니다.
     */
    @PostMapping("updateWordBook")
    public String updateWordBook(@AuthenticationPrincipal AuthenticatedUser user,
                                 @RequestParam(name = "book") int wordbookId,
                                 WordBookDTO wordBookDTO,
                                 RedirectAttributes redirectAttributes){

        try {
            wordBookDTO.setWordBookId(wordbookId);
            ws.updateWordBook(wordBookDTO, user);
            setFlashToastMessage(redirectAttributes, true, "수정되었습니다.");
                        return "redirect:/word/wordBookDetail?book=" + wordBookDTO.getWordBookId();
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
        }

        return "redirect:/";

    }

    /**
     * 단어장 삭제
     * @author gyahury
     * @param wordBookId 단어장 Id를 가져옵니다
     * @param user user 객체를 가져옵니다.
     * @param redirectAttributes 리다이렉트 객체를 가져옵니다.
     * @return 단어 페이지로 리다이렉트합니다.
     */
    @GetMapping("deleteWordBook")
    public String deleteWordBook(@RequestParam(name = "book") int wordBookId
            , @AuthenticationPrincipal AuthenticatedUser user
            , RedirectAttributes redirectAttributes) {

        try {
            ws.deleteWordBook(wordBookId, user.getId());
            setFlashToastMessage(redirectAttributes, true, "삭제되었습니다.");
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
        } catch (Exception e) {
            setFlashToastMessage(redirectAttributes, false, "알 수 없는 오류가 발생했습니다.");
        }

        return "redirect:/word";
    }

    /**
     * 단어 목록 수정 페이지 이동
     * @author KIMGEON64
     * @param user user 객체를 전달합니다.
     * @param wordbookId 단어장 키값을 전달합니다.
     * @param model 모델 객체를 전달합니다.
     * @return 단어 수정 페이지를 반환합니다.
     */
    @GetMapping("updateWordForm")
    public String updateWordForm(@AuthenticationPrincipal AuthenticatedUser user,
                                 @RequestParam(name = "book") int wordbookId, Model model){

        List<WordDTO> wordDTOList = ws.getWordsForUpdate(user, wordbookId);
        model.addAttribute("words", wordDTOList);

        return "word/word-create-form";

    }

    /**
     * 단어 세부 수정 페이지 이동
     * @author KIMGEON64
     * @param user user 객체를 전달합니다.
     * @param wordDTO 단어 세부 값을 전달합니다.
     * @param model 모델 객체를 전달합니다.
     * @return 단어 세부 페이지를 반환합니다.
     */
    @GetMapping("wordEditForm")
    public String wordEdit(@AuthenticationPrincipal AuthenticatedUser user,
                           WordDTO wordDTO, Model model){

        WordDTO word = ws.getWordDetails(user, wordDTO.getWordNum());
        model.addAttribute("word", word);

        return "word/word-adit-form";
    }

    /**
     * 단어 세부 수정 사항 저장
     * @author KIMGEON64
     * @param user user 객체를 전달합니다.
     * @param wordbookId 단어장 키값을 전달합니다.
     * @param wordDTO 단어 세부 값을 전달합니다.
     * @return 단어 수정 페이지를 반환합니다.
     */
    @PostMapping("wordEdit")
    public String wordEdit(@AuthenticationPrincipal AuthenticatedUser user,
                           @RequestParam(name = "book") int wordbookId,
                           WordDTO wordDTO){

        ws.saveEditedWord(user, wordbookId, wordDTO);

        return "word/word-update-form";

    }

    @PostMapping("addLearningRecord")
    public String addLearningRecord(@AuthenticationPrincipal AuthenticatedUser user,
                           @RequestParam(name = "book") int wordbookId) {

        ws.addLearningRecord(user.getId(), wordbookId);

        return "";

    }

}
