package com.tanppoppo.testore.testore.board.service;

import com.tanppoppo.testore.testore.board.dto.BoardDTO;
import com.tanppoppo.testore.testore.board.entity.BoardEntity;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;

import java.util.List;

public interface BoardService {
    List<BoardDTO> getRecentNotices();

    void saveBoard(BoardDTO boardDTO, Integer userId);

    BoardDTO getBoardDetail(int boardId);

    void updateBoard(BoardDTO boardDTO, Integer userId);

    void deleteBoard(int boardId, Integer userId);

    void IsMyBoard(int boardId, AuthenticatedUser user);
}
