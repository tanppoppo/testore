package com.tanppoppo.testore.testore.board.repository;

import com.tanppoppo.testore.testore.board.entity.BoardEntity;
import com.tanppoppo.testore.testore.common.util.BoardTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {

    List<BoardEntity> findTop5ByBoardTypeOrderByCreatedDateDesc(BoardTypeEnum boardType); //최신 게시글 3개 가져오기

    Page<BoardEntity> findByBoardTypeAndTitleContaining(BoardTypeEnum boardType, String keyword, Pageable pageable);

    Page<BoardEntity> findByBoardType(BoardTypeEnum boardType, Pageable pageable);
}
