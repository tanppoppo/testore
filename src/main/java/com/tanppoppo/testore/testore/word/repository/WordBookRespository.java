package com.tanppoppo.testore.testore.word.repository;

import com.tanppoppo.testore.testore.word.entity.WordBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordBookRespository extends JpaRepository<WordBookEntity, Integer> {
}
