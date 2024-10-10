package com.tanppoppo.testore.testore.board.repository;

import com.tanppoppo.testore.testore.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {

    List<BoardEntity> findTop5ByOrderByCreatedDateDesc(); //최신 공지 5개 가져오기
}
