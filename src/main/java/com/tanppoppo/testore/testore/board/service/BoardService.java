package com.tanppoppo.testore.testore.board.service;

import com.tanppoppo.testore.testore.board.dto.BoardDTO;
import com.tanppoppo.testore.testore.board.dto.CommentDTO;
import com.tanppoppo.testore.testore.common.util.BoardTypeEnum;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BoardService {

    List<BoardDTO> getRecentNotices(BoardTypeEnum boardTypeEnum);

    void saveBoard(BoardDTO boardDTO, Integer userId, BoardTypeEnum boardTypeEnum);

    BoardDTO getBoardDetail(int boardId);

    void updateBoard(BoardDTO boardDTO, Integer userId);

    void deleteBoard(int boardId, Integer userId);

    void IsMyBoard(int boardId, AuthenticatedUser user);

    List<CommentDTO> getCommentList(int boardId);

    void createComment(CommentDTO commentDTO);

    void deleteComment(Integer commentId, Integer id);

    CommentDTO getCommentById(int commentId);

    void updateComment(CommentDTO commentDTO);

    Page<BoardDTO> getBoards(int page, int pageSize, BoardTypeEnum boardType, String keyword);
}
