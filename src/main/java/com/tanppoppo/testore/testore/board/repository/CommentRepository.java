package com.tanppoppo.testore.testore.board.repository;

import com.tanppoppo.testore.testore.board.entity.BoardEntity;
import com.tanppoppo.testore.testore.board.entity.CommentEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findByBoard(BoardEntity boardEntity, Sort sort);

    Integer countByBoard(BoardEntity boardEntity);

}
