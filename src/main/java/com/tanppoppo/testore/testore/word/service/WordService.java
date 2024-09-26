package com.tanppoppo.testore.testore.word.service;

import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import com.tanppoppo.testore.testore.word.dto.WordBookDTO;

import java.util.List;
import java.util.Map;

public interface WordService {

    Integer countWordBooksByOwnerId(Integer ownerId);

    int wordCreate(WordBookDTO wordBookDTO, AuthenticatedUser user);

    List<WordBookDTO> getListItems(AuthenticatedUser user);

    Map<String, Object> selectBookDetail(int wordbookId);

}
