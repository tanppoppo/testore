package com.tanppoppo.testore.testore.word.repository;

import com.tanppoppo.testore.testore.word.dto.WordDTO;
import com.tanppoppo.testore.testore.word.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<WordEntity, Integer> {

    // 특정 단어장 ID로 단어 조회
//    List<WordDTO> findByWordbookId(Integer wordbookId);

    // 특정 사용자 ID로 단어 조회
//    List<WordDTO> findByOwnerId(Integer ownerId);

}
