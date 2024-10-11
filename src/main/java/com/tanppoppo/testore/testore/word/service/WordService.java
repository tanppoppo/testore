package com.tanppoppo.testore.testore.word.service;

import com.tanppoppo.testore.testore.member.dto.ReviewDTO;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import com.tanppoppo.testore.testore.word.dto.WordBookDTO;

import java.util.List;
import java.util.Map;

public interface WordService {

    Integer countWordBooksByOwnerId(Integer ownerId);

    void addWords(Map<String, String[]> words, Integer wordBookId, Integer userId);

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
}
