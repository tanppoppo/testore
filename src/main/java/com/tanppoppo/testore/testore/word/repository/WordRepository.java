package com.tanppoppo.testore.testore.word.repository;

import com.tanppoppo.testore.testore.word.entity.WordBookEntity;
import com.tanppoppo.testore.testore.word.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<WordEntity, Integer> {

    List<WordEntity> findAllByWordBookId(WordBookEntity wordBookEntity);
    List<WordEntity> findByWordBookIdOrderByWordNumDesc(WordBookEntity wordBookEntity);

}
