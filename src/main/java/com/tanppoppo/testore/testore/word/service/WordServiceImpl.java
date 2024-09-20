package com.tanppoppo.testore.testore.word.service;

import com.tanppoppo.testore.testore.word.repository.WordBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {

    private final WordBookRepository wbr;

    /**
     * 특정 사용자가 소유한 단어장의 개수를 반환
     *
     * @author dhkdtjs1541
     * @param ownerId 단어장 소유자의 ID
     * @return 소유한 단어장의 개수
     */
    @Override
    public Integer countWordBooksByOwnerId(Integer ownerId) {
        return wbr.countByOwnerId(ownerId);
    }
}
