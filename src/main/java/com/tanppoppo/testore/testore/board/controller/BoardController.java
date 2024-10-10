package com.tanppoppo.testore.testore.board.controller;


import com.tanppoppo.testore.testore.board.dto.BoardDTO;
import com.tanppoppo.testore.testore.board.service.BoardService;
import com.tanppoppo.testore.testore.common.util.BoardTypeEnum;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.tanppoppo.testore.testore.util.MessageUtil.*;

/**
 * 게시판 관련 요청을 처리하는 컨트롤러 클래스
 * @authoor dhkdtjs1541
 * @version 0.1.0
 * @since 0.1.0
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("board")
public class BoardController {

    private final BoardService bs;

    /**
     * 글쓰기 폼으로 이동하는 메서드
     * @author gyahury
     * @return 글쓰기 폼 뷰 이름
     */
    @GetMapping("/createBoardForm")
    public String createBoardForm() {
        return "board/create";
    }

    /**
     * 글쓰기 폼에서 데이터를 받아 게시글을 저장하는 메서드
     * @author gyahury
     * @param boardDTO 글 데이터 전송
     * @param user 현재 인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 사용되는 속성
     * @return 게시글 목록 페이지로 리다이렉트
     */
    @PostMapping("/create")
    public String createBoard(BoardDTO boardDTO, @AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes) {
        // 관리자 권한 체크
        if (boardDTO.getBoardType().equals(BoardTypeEnum.NOTICE) && !user.getMembershipLevel().equals((byte) 99)) {
            setFlashToastMessage(redirectAttributes, false, "관리자만 공지사항을 작성할 수 있습니다.");
            return "redirect:/board/createBoardForm"; // 글쓰기 폼으로 다시 리다이렉트
        }

        try {
            // 게시글 저장
            bs.saveBoard(boardDTO, user.getId());
            setFlashToastMessage(redirectAttributes, true, "게시글이 성공적으로 작성되었습니다.");
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "게시글 작성 중 문제가 발생했습니다.<br>다시 시도해주세요.");
        }
        return "redirect:/";
    }

    /**
     * 게시글의 상세 정보를 조회하는 메서드
     * @author dhkdtjs1541
     * @param model 모델 객체에 게시글 정보를 추가
     * @param boardId 조회할 게시글 ID
     * @param user 현재 인증된 사용자 정보
     * @return 게시글 상세 페이지 뷰 이름
     */
    @GetMapping("/detailBoard")
    public String getBoardDetail(Model model, @RequestParam(name = "board") int boardId, @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            BoardDTO boardDTO = bs.getBoardDetail(boardId);
            model.addAttribute("boardDTO", boardDTO);
            return "board/detail";
        } catch (Exception e) {
            log.error("게시글 상세 조회 중 오류 발생", e);
            return "redirect:/";
        }
    }

    /**
     * 게시글 수정 폼으로 이동하는 메서드
     * @author dhkdtjs1541
     * @param id 수정할 게시글 ID
     * @param user 현재 인증된 사용자 정보
     * @param model 모델 객체에 게시글 정보를 추가
     * @return 게시글 수정 페이지 뷰 이름
     */
    @GetMapping("/updateBoardForm")
    public String updateBoardForm(@RequestParam(name = "id") int id,
                                  @AuthenticationPrincipal AuthenticatedUser user,
                                  Model model) {
        try {
            BoardDTO boardDTO = bs.getBoardDetail(id);
            model.addAttribute("boardDTO", boardDTO);
            return "board/create";
        } catch (Exception e) {
            log.error("게시글 수정 페이지 이동 중 오류 발생", e);
            return "redirect:/";
        }
    }

    /**
     * 게시글을 수정하는 메서드
     * @author dhkdtjs1541
     * @param boardDTO 수정할 게시글 정보
     * @param user 현재 인증된 사용자 정보
     * @return 게시글 목록 페이지로 리다이렉트
     */
    @PostMapping("/updateBoard")
    public String updateBoard(BoardDTO boardDTO, @AuthenticationPrincipal AuthenticatedUser user) {

        boardDTO.setMemberId(user.getId());
        bs.updateBoard(boardDTO, user.getId());
        return "redirect:/";

    }

    /**
     * 게시글을 삭제하는 메서드
     * @author dhkdtjs1541
     * @param boardId 삭제할 게시글 ID
     * @param user 현재 인증된 사용자 정보
     * @return 게시글 목록 페이지로 리다이렉트
     */
    @GetMapping("/deleteBoard")
    public String deleteBoard(@RequestParam(name = "board") int boardId,
                              @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            bs.deleteBoard(boardId, user.getId());
            return "redirect:/";
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            return "redirect:/";
        }
    }
}