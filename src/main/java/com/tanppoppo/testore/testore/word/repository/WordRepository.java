package com.tanppoppo.testore.testore.word.repository;

import com.tanppoppo.testore.testore.word.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<WordEntity, Integer> {
}
