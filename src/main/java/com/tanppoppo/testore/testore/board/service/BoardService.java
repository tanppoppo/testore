package com.tanppoppo.testore.testore.board.service;

import com.tanppoppo.testore.testore.board.dto.BoardDTO;
import com.tanppoppo.testore.testore.board.entity.BoardEntity;

import java.util.List;

public interface BoardService {
    List<BoardEntity> getRecentNotices();

    void saveBoard(BoardDTO boardDTO, Integer userId);

    BoardDTO getBoardDetail(int id);

    void updateBoard(BoardDTO boardDTO, Integer userId);

    void deleteBoard(int boardId, Integer userId);
}
