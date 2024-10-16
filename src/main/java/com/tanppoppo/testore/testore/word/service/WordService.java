package com.tanppoppo.testore.testore.word.service;

import com.tanppoppo.testore.testore.member.dto.ReviewDTO;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import com.tanppoppo.testore.testore.word.dto.WordBookDTO;
import com.tanppoppo.testore.testore.word.dto.WordDTO;

import java.util.List;
import java.util.Map;

public interface WordService {

    Integer countWordBooksByOwnerId(Integer ownerId);

    Integer addWords(WordDTO wordDTO, Integer wordBookId, Integer userId);

    Integer checkWordNum(int wordBookId, Integer id);

    int createWordBook(WordBookDTO wordBookDTO, AuthenticatedUser user);

    List<WordBookDTO> getListItems(AuthenticatedUser user);

    Map<String, Object> selectWordBookDetail(int wordbookId, AuthenticatedUser user);

    void updatePublicOptionByMemberId(Integer wordBookId, AuthenticatedUser user);

    WordBookDTO selectUpdatedBookInfo(int wordBookId, Integer id);

    Map<String, Object> getListReviews(AuthenticatedUser user, int wordBookId);

    void createReview(AuthenticatedUser user, int wordBookId, ReviewDTO reviewDTO);

    ReviewDTO selectUpdatedReviewInfo(AuthenticatedUser user, int wordBookId, int reviewId);

    void updateReview(AuthenticatedUser user, int reviewId, ReviewDTO reviewDTO);

    void deleteReview(AuthenticatedUser user, int reviewId);

    List<WordBookDTO> recommendedWordBook(AuthenticatedUser user);

    List<WordBookDTO> likedWordBook(AuthenticatedUser user);

    List<WordBookDTO> muchSharedWordBook(AuthenticatedUser user);

    List<WordBookDTO> findWordBookByMemberId(AuthenticatedUser user, String keyword);

    WordBookDTO selectUpdatedWordbookInfo(int wordbookId, AuthenticatedUser user);

    void updateWordBook(WordBookDTO wordBookDTO, AuthenticatedUser user);

    List<WordDTO> getWordsForUpdate(AuthenticatedUser user, int wordbookId);

    WordDTO getWordDetails(AuthenticatedUser user, Integer wordNum);

    void saveEditedWord(AuthenticatedUser user, int wordbookId, WordDTO wordDTO);

    List<WordBookDTO> getLikedWordBook(AuthenticatedUser user);

    List<WordBookDTO> getBookmarkedWordBook(AuthenticatedUser user);
}
