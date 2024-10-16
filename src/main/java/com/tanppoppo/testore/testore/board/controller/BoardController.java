package com.tanppoppo.testore.testore.board.controller;


import com.tanppoppo.testore.testore.board.dto.BoardDTO;
import com.tanppoppo.testore.testore.board.dto.CommentDTO;
import com.tanppoppo.testore.testore.board.service.BoardService;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.tanppoppo.testore.testore.util.MessageUtil.*;

/**
 * 게시판 관련 요청을 처리하는 컨트롤러 클래스
 * @author dhkdtjs1541
 * @version 0.1.1
 * @since 0.1.1
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
    public String createBoardForm(@RequestParam(name = "type") String boardType, Model model) {
        model.addAttribute("boardType", boardType);
        return "board/create-board-form";
    }

    /**
     * 글쓰기 폼에서 데이터를 받아 게시글을 저장하는 메서드
     * @author gyahury
     * @param boardDTO 글 데이터 전송
     * @param user 현재 인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 사용되는 속성
     * @return 게시글 목록 페이지로 리다이렉트
     */
    @PostMapping("/createBoard")
    public String createBoard(BoardDTO boardDTO, @AuthenticationPrincipal AuthenticatedUser user,
                              RedirectAttributes redirectAttributes, @RequestParam(name = "type") String boardType) {

        try {
            boardDTO.setBoardType(boardType);
            bs.saveBoard(boardDTO, user.getId());
            setFlashToastMessage(redirectAttributes, true, "게시글이 성공적으로 작성되었습니다.");
        } catch (AccessDeniedException e) {
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "게시글 작성 중 문제가 발생했습니다.<br>다시 시도해주세요.");
        }
        return "redirect:/";

    }

    /**
     * 게시글 상세 정보 조회
     * @author dhkdtjs1541
     * @param model 게시글과 댓글 데이터를 전달할 모델 객체
     * @param boardId 조회활 게시글의 ID
     * @return 게시글 상세 뷰의 이름을 반환함
     */
    @GetMapping("/boardDetail")
    public String boardDetail(Model model, @RequestParam(name = "board") int boardId) {
        try {
            BoardDTO boardDTO = bs.getBoardDetail(boardId);
            model.addAttribute("boardDTO", boardDTO);

//            List<CommentDTO> commentDTOList = bs.getCommentList(boardId);
//            model.addAttribute("commentDTOList", commentDTOList);

            return "board/board-detail";
        } catch (Exception e) {
            log.error("게시글 상세 조회 중 오류 발생", e);
            return "redirect:/";
        }
    }

    /**
     * 게시글 수정 폼으로 이동하는 메서드
     * @author dhkdtjs1541
     * @param boardId 수정할 게시글 ID
     * @param user 현재 인증된 사용자 정보
     * @param model 모델 객체에 게시글 정보를 추가
     * @return 게시글 수정 페이지 뷰 이름
     */
    @GetMapping("/updateBoardForm")
    public String updateBoardForm(@RequestParam(name = "board") int boardId, RedirectAttributes redirectAttributes,
                                  @AuthenticationPrincipal AuthenticatedUser user,
                                  Model model) {

        try {
            BoardDTO boardDTO = bs.getBoardDetail(boardId);
            bs.IsMyBoard(boardId, user);
            model.addAttribute("boardDTO", boardDTO);
            return "board/create-board-form";
        } catch (EntityNotFoundException e) {
            log.error("게시글 수정 페이지 이동 중 조회 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "게시글을 찾을 수 없습니다.");
        } catch (AccessDeniedException e) {
            log.error("게시글 수정 페이지 이동 중 권한 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
        } catch (Exception e) {
            log.error("게시글 수정 페이지 이동 중 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "게시글 조회 중 문제가 발생했습니다.<br>다시 시도해주세요.");
        }
        return "redirect:/";

    }

    /**
     * 게시글을 수정하는 메서드
     * @author dhkdtjs1541
     * @param boardDTO 수정할 게시글 정보
     * @param user 현재 인증된 사용자 정보
     * @return 게시글 목록 페이지로 리다이렉트
     */
    @PostMapping("/updateBoard")
    public String updateBoard(BoardDTO boardDTO, @RequestParam(name = "board") int boardId,
                              @AuthenticationPrincipal AuthenticatedUser user ,RedirectAttributes redirectAttributes) {

        try {
            boardDTO.setBoardId(boardId);
            boardDTO.setMemberId(user.getId());
            bs.updateBoard(boardDTO, user.getId());
            setFlashToastMessage(redirectAttributes, true, "게시글이 성공적으로 수정되었습니다.");
        } catch (AccessDeniedException e) {
            log.error("게시글 수정 권한 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "게시글 수정 중 문제가 발생했습니다.<br>다시 시도해주세요.");
        }
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
    public String deleteBoard(@RequestParam(name = "board") int boardId, RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal AuthenticatedUser user) {

        try {
            bs.deleteBoard(boardId, user.getId());
            setFlashToastMessage(redirectAttributes, true, "게시글이 성공적으로 삭제되었습니다.");
        } catch (AccessDeniedException e) {
            log.error("게시글 삭제 권한 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "권한이 없습니다.");
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "게시글 삭제 중 문제가 발생했습니다.<br>다시 시도해주세요.");
        }
        return "redirect:/";

    }

    /**
     * 댓글 작성
     * @author dhkdtjs1541
     * @param commentDTO 작성할 댓글 데이터
     * @param user 현재 인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 플래시 메시지를 전달하기 위한 객체
     * @return 홈으로 리다이렉트
     */
    @PostMapping("/createComment")
    public String createComment(@ModelAttribute CommentDTO commentDTO,
                                @AuthenticationPrincipal AuthenticatedUser user,
                                RedirectAttributes redirectAttributes) {
        commentDTO.setMemberId(user.getId());
        commentDTO.setNickname(user.getNickname());

        try {
            bs.createComment(commentDTO);
            setFlashToastMessage(redirectAttributes, true, "댓글이 성공적으로 작성되었습니다.");
        } catch (Exception e) {
            log.error("댓글 작성 중 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "댓글 작성 중 문제가 발생했습니다.<br>다시 시도해주세요.");
        }

        return "redirect:/";
    }

    /**
     * 댓글 삭제
     * @author dhkdtjs1541
     * @param commentDTO 삭제할 댓글 데이터
     * @param user 현재 인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 플래시 메시지를 전달하기 위한 객체
     * @return 홈으로 리다이렉트
     */
    @GetMapping("/deleteComment")
    public String deleteComment(@ModelAttribute CommentDTO commentDTO,
                                @AuthenticationPrincipal AuthenticatedUser user,
                                RedirectAttributes redirectAttributes) {
        try {
            bs.deleteComment(commentDTO.getCommentId(), user.getId()); // 댓글 삭제 서비스 호출
            setFlashToastMessage(redirectAttributes, true, "댓글이 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            log.error("댓글 삭제 중 오류 발생: 댓글을 찾을 수 없습니다.", e);
            setFlashToastMessage(redirectAttributes, false, "삭제하려는 댓글을 찾을 수 없습니다.");
        } catch (AccessDeniedException e) {
            log.error("댓글 삭제 권한 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "댓글 삭제 권한이 없습니다.");
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "댓글 삭제 중 문제가 발생했습니다.<br>다시 시도해주세요.");
        }

        return "redirect:/";
    }

    /**
     * 댓글 수정 폼으로 이동
     * @author dhkdtjs1541
     * @param commentId 수정할 댓글의 ID
     * @param model 댓글 정보를 뷰로 전달할 모델 객체
     * @return 댓글 수정 폼 페이지의 뷰 이름
     */
    @GetMapping("/updateCommentForm")
    public String updateCommentForm(@RequestParam(name = "commentId") int commentId, Model model) {
        try {
            CommentDTO commentDTO = bs.getCommentById(commentId);
            model.addAttribute("commentDTO", commentDTO);
            return "board/update-comment-form";
        } catch (EntityNotFoundException e) {
            log.error("댓글 수정 페이지 이동 중 오류 발생", e);
            return "redirect:/";
        }
    }

    /**
     * 댓글 수정
     * @author dhkdtjs1541
     * @param commentDTO 수정할 댓글 정보가 담긴 DTO 객체
     * @param user 현재 인증된 사용자 정보
     * @param redirectAttributes 리다이렉트 시 플래시 메시지를 전달하기 위한 객체
     * @return 수정 후 홈으로 리다이렉트
     */
    @PostMapping("/updateComment")
    public String updateComment(@ModelAttribute CommentDTO commentDTO,
                                @AuthenticationPrincipal AuthenticatedUser user,
                                RedirectAttributes redirectAttributes) {
        commentDTO.setMemberId(user.getId());

        try {
            bs.updateComment(commentDTO);
            setFlashToastMessage(redirectAttributes, true, "댓글이 성공적으로 수정되었습니다.");
        } catch (EntityNotFoundException e) {
            log.error("댓글 수정 중 오류 발생: 댓글을 찾을 수 없습니다.", e);
            setFlashToastMessage(redirectAttributes, false, "수정하려는 댓글을 찾을 수 없습니다.");
        } catch (AccessDeniedException e) {
            log.error("댓글 수정 권한 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "댓글 수정 권한이 없습니다.");
        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생", e);
            setFlashToastMessage(redirectAttributes, false, "댓글 수정 중 문제가 발생했습니다.<br>다시 시도해주세요.");
        }
        return "redirect:/";
    }

}