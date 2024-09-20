package com.tanppoppo.testore.testore.word.repository;

import com.tanppoppo.testore.testore.word.entity.WordBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordBookRepository extends JpaRepository<WordBookEntity, Integer> {

    List<WordBookEntity> findByOwnerId(Integer ownerId);

    @Query("SELECT COUNT(wb) FROM WordBookEntity wb WHERE wb.ownerId = :ownerId")
    Integer countByOwnerId(@Param("ownerId") Integer ownerId);

}
